package com.userstipa.screentranslation.ui.select_language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.databinding.ItemListSelectLanguageBinding
import com.userstipa.screentranslation.languages.Language

class SelectLanguageAdapter(
    preferenceLanguageKey: PreferencesKeys,
    private var selectedLanguage: Language,
    private val listener: ListActions
) : RecyclerView.Adapter<SelectLanguageAdapter.Holder>() {

    private val list: List<Language> = when(preferenceLanguageKey) {
        PreferencesKeys.SOURCE_LANGUAGE -> Language.getSourceLanguages()
        PreferencesKeys.TARGET_LANGUAGE -> Language.getTargetLanguages()
        else -> { throw Exception("${this::javaClass.name} cannot init list") }
    }

    private var previousLanguageIndex = list.indexOf(selectedLanguage)

    fun setChecked(language: Language) {
        val index = list.indexOf(language)
        selectedLanguage = language
        previousLanguageIndex = index
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemListSelectLanguageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val language = list[position]
        holder.binding.title.text = language.title
        holder.binding.checkBox.isChecked = language == selectedLanguage
        holder.binding.checkBox.setOnClickListener {
            selectedLanguage = language
            notifyItemChanged(previousLanguageIndex)
            previousLanguageIndex = holder.adapterPosition
            listener.onClickLanguage(language)
        }
    }

    inner class Holder(val binding: ItemListSelectLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)
}

interface ListActions {
    fun onClickLanguage(language: Language)
}