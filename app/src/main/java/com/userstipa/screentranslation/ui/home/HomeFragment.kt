package com.userstipa.screentranslation.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.data.PreferencesKeys
import com.userstipa.screentranslation.databinding.FragmentHomeBinding
import com.userstipa.screentranslation.di.ViewModelFactory
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragment : Fragment(), ServiceConnection {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isServiceConnected = false

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setUi()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchCurrentLanguages()
                viewModel.uiState.collectLatest {
                    binding.sourceLanguage.text = it.sourceLanguage.title
                    binding.targetLanguage.text = it.targetLanguage.title
                    binding.progressBar.isVisible = it.isLoading
                    binding.launchService.isEnabled = !it.isLoading
                    binding.sourceLanguage.isEnabled = !it.isLoading
                    binding.targetLanguage.isEnabled = !it.isLoading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTranslatorReady.collectLatest {
                startAndConnectToService()
            }
        }
    }

    private fun setUi() {
        binding.launchService.setOnClickListener {
            if (isServiceConnected) stopService()
            else viewModel.prepareTranslateService()
        }

        binding.sourceLanguage.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment(PreferencesKeys.SOURCE_LANGUAGE)
            findNavController().navigate(actions)
        }

        binding.targetLanguage.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment(PreferencesKeys.TARGET_LANGUAGE)
            findNavController().navigate(actions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startAndConnectToService() {
        Intent(requireContext(), MediaProjectionServiceImpl::class.java).also {
            requireContext().startForegroundService(it)
            connectService()
        }
    }

    private fun stopService() {
        Intent(requireContext(), MediaProjectionServiceImpl::class.java).also {
            requireContext().stopService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        connectService()
    }

    override fun onPause() {
        super.onPause()
        disconnectService()
    }

    private fun connectService() {
        val intent = Intent(requireContext(), MediaProjectionServiceImpl::class.java)
        requireActivity().bindService(intent, this, 0)
    }

    private fun disconnectService() {
        requireActivity().unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isServiceConnected = true
        binding.icon.setImageResource(R.drawable.baseline_translate_enable_24)
        binding.targetLanguage.isEnabled = false
        binding.sourceLanguage.isEnabled = false
        binding.launchService.isClickable = true
        binding.launchService.text = getString(R.string.btn_launch_service_is_enable)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isServiceConnected = false
        binding.icon.setImageResource(R.drawable.baseline_translate_disable_24)
        binding.targetLanguage.isEnabled = true
        binding.sourceLanguage.isEnabled = true
        binding.launchService.isClickable = true
        binding.launchService.text = getString(R.string.btn_launch_service_is_disable)
    }

}