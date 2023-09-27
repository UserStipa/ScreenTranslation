package com.userstipa.screentranslation.ui.translator

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.databinding.ActivityTranslatorBinding
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl

class TranslatorActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityTranslatorBinding
    private lateinit var service: MediaProjectionServiceImpl
    private var isServiceConnected = false

    private val requestProjection =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intent = result.data
            if (result.resultCode != Activity.RESULT_OK) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.projection_denied),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            if (result.resultCode == Activity.RESULT_OK && intent != null) {
                service.grandPermission(intent)
                translateDisplay()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectToService()
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

    private fun translateDisplay() {
        if (isServiceConnected) service.createScreenshot()
    }
}