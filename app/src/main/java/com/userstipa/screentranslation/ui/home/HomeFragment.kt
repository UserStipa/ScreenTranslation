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
import com.userstipa.screentranslation.databinding.FragmentFirstBinding
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment(), ServiceConnection {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private var isServiceConnected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.launchService.setOnClickListener {
            binding.launchService.isEnabled = false
            if (isServiceConnected) {
                stopService()
            } else {
                startService()
                connectService()
            }
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
        binding.launchService.text = "Stop service"
        binding.launchService.isEnabled = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isServiceConnected = false
        binding.launchService.text = "Start service"
        binding.launchService.isEnabled = true
    }

}