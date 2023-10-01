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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.databinding.FragmentHomeBinding
import com.userstipa.screentranslation.di.ViewModelFactory
import com.userstipa.screentranslation.models.LanguageType
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl
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

        viewModel.sourceLanguage.observe(viewLifecycleOwner) {
            binding.sourceLanguage.text = it.title
        }

        viewModel.targetLanguage.observe(viewLifecycleOwner) {
            binding.targetLanguage.text = it.title
        }

        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is Event.ErrorEvent -> {}
                    is Event.TranslatorIsLoading -> binding.progressBar.isVisible = true
                    is Event.TranslatorIsLoadingComplete -> binding.progressBar.isVisible = false
                    is Event.TranslatorIsReady -> {
                        startService()
                        connectService()
                    }
                }
            }
        }
    }

    private fun setUi() {
        binding.launchService.setOnClickListener {
            binding.launchService.isClickable = false
            if (isServiceConnected) {
                stopService()
            } else {
                viewModel.prepareTranslate()
            }
        }
        binding.sourceLanguage.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment(LanguageType.SOURCE)
            findNavController().navigate(actions)
        }

        binding.targetLanguage.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionHomeFragmentToSelectLanguageFragment(LanguageType.TARGET)
            findNavController().navigate(actions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startService() {
        Intent(requireContext(), MediaProjectionServiceImpl::class.java).also {
            requireContext().startForegroundService(it)
        }
    }

    private fun stopService() {
        Intent(requireContext(), MediaProjectionServiceImpl::class.java).also {
            requireContext().stopService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchData()
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
        binding.launchService.isClickable = true
        binding.launchService.text = getString(R.string.btn_launch_service_is_enable)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isServiceConnected = false
        binding.icon.setImageResource(R.drawable.baseline_translate_disable_24)
        binding.launchService.isClickable = true
        binding.launchService.text = getString(R.string.btn_launch_service_is_disable)
    }

}