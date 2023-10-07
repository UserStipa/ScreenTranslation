package com.userstipa.screentranslation.ui.translator

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.databinding.ActivityTranslatorBinding
import com.userstipa.screentranslation.di.ViewModelFactory
import com.userstipa.screentranslation.ui.service.MediaProjectionService
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl
import com.userstipa.screentranslation.ui.translator.ModalBottomSheet.Companion.OUTPUT_TEXT
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslatorActivity : AppCompatActivity(), ServiceConnection, ModalBottomSheetCallback {

    private lateinit var binding: ActivityTranslatorBinding
    private lateinit var service: MediaProjectionService
    private var isServiceConnected = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by viewModels<TranslatorViewModel> { viewModelFactory }

    private val requestProjection =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data
            if (result.resultCode != Activity.RESULT_OK) {
                showModelBottomSheet(getString(R.string.message_request_permission_denied))
            }
            if (result.resultCode == Activity.RESULT_OK && intent != null) {
                val mediaProjection = service.getMediaProjection(intent)
                translateDisplay(mediaProjection)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComponent.inject(this)
        binding = ActivityTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setObservers()
        connectToService()
    }

    private fun setObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.progressBar.isVisible = uiState.isLoading
                    uiState.outputText?.let { showModelBottomSheet(it) }
                }
            }
        }
    }

    private fun connectToService() {
        val intent = Intent(applicationContext, MediaProjectionServiceImpl::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        service = (binder as MediaProjectionServiceImpl.LocalBinder).getService()
        isServiceConnected = true
        requestPermission()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        isServiceConnected = false
    }

    private fun requestPermission() {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = manager.createScreenCaptureIntent()
        requestProjection.launch(intent)
    }

    private fun translateDisplay(mediaProjection: MediaProjection) {
        if (isServiceConnected) {
            viewModel.translateDisplay(mediaProjection)
        }
    }

    private fun showModelBottomSheet(text: String) {
        ModalBottomSheet().apply {
            arguments = bundleOf(
                OUTPUT_TEXT to text
            )
            show(supportFragmentManager, ModalBottomSheet.TAG)
        }
    }

    override fun onCloseModalBottomSheet() {
        finish()
    }
}