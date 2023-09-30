package com.userstipa.screentranslation.ui.select_language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.userstipa.screentranslation.databinding.FragmentSelectLanguageBinding
import com.userstipa.screentranslation.models.Language

class SelectLanguageFragment : Fragment(), ListActions {

    private var _binding: FragmentSelectLanguageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SelectLanguageFragmentArgs>()
    private val languageType by lazy { args.languageType }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    private fun setAdapter() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = SelectLanguageAdapter(languageType, Language.English, this)
    }

    override fun onClickLanguage(language: Language) {

    }
}
