package com.userstipa.screentranslation.ui.select_language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.data.PreferencesKeys
import com.userstipa.screentranslation.models.Language
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectLanguageViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _selectedLanguage = MutableLiveData<Language>()
    val selectedLanguage: LiveData<Language> get() = _selectedLanguage

    private val _isLanguageDownload = MutableLiveData<Boolean>()
    val isLanguageDownload: LiveData<Boolean> get() = _isLanguageDownload

    fun getCurrentLanguage(preferencesKeys: PreferencesKeys) {
        viewModelScope.launch {
            _selectedLanguage.value = dataStorePreferences.getLanguage(preferencesKeys)
        }
    }

    fun setLanguage(preferencesKeys: PreferencesKeys, language: Language) {
        viewModelScope.launch {
            dataStorePreferences.setPreferences(preferencesKeys, language)
        }
    }

    fun isLanguagesDownload(value: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setPreferences(PreferencesKeys.IS_LANGUAGES_DOWNLOAD, value)
        }
    }

}