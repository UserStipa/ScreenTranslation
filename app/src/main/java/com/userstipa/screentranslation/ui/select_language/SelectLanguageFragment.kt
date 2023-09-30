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
    private val languageType by lazy { args.languageType }

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
        setAdapter()
    }

    private fun setAdapter() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = SelectLanguageAdapter(languageType, Language.English, this)
    }

    override fun onClickLanguage(language: Language) {

    }
}
