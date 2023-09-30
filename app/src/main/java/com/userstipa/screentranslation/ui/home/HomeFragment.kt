package com.userstipa.screentranslation.ui.home

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.databinding.FragmentHomeBinding
import com.userstipa.screentranslation.models.LanguageType
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl

class HomeFragment : Fragment(), ServiceConnection {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isServiceConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.launchService.setOnClickListener {
            binding.launchService.isClickable = false
            if (isServiceConnected) {
                stopService()
            } else {
                startService()
                connectService()
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
        binding.icon.isClickable = true
        binding.launchService.text = getString(R.string.btn_launch_service_is_disable)
    }

}