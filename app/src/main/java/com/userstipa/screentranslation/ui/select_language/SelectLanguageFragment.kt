package com.userstipa.screentranslation.ui.select_language

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.databinding.FragmentSelectLanguageBinding
import com.userstipa.screentranslation.di.ViewModelFactory
import com.userstipa.screentranslation.models.Language
import javax.inject.Inject

class SelectLanguageFragment : Fragment(), ListActions {

    private var _binding: FragmentSelectLanguageBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SelectLanguageFragmentArgs>()
    private val preferencesLanguageKey by lazy { args.preferencesLanguage }
    private lateinit var adapter: SelectLanguageAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by viewModels<SelectLanguageViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(this)
    }

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
        viewModel.getCurrentLanguage(preferencesLanguageKey)
        setAdapter()
        setObservers()
        setUi()
    }

    private fun setAdapter() {
        adapter = SelectLanguageAdapter(Language.English, this)
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter
    }

    private fun setObservers() {
        viewModel.selectedLanguage.observe(viewLifecycleOwner) {
            adapter.setChecked(it)
        }
        viewModel.isLanguageDownload.observe(viewLifecycleOwner) {
            binding.switchDownload.isChecked = it
        }
    }

    private fun setUi() {
        binding.switchDownload.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isLanguagesDownload(isChecked)
        }
    }

    override fun onClickLanguage(language: Language) {
        viewModel.setLanguage(preferencesLanguageKey, language)
    }
}
