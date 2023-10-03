package com.userstipa.screentranslation.ui.translator

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.userstipa.screentranslation.databinding.FragmentModalBottomSheetBinding

class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModalBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
    }

    private fun setUi() {
        binding.outputText.text = arguments?.getString(OUTPUT_TEXT, "Empty data")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as ModalBottomSheetCallback).onCloseModalBottomSheet()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        const val OUTPUT_TEXT = "OUTPUT_TEXT"
    }
}

interface ModalBottomSheetCallback {
    fun onCloseModalBottomSheet()
}