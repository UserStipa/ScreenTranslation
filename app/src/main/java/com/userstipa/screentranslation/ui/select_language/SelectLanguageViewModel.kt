package com.userstipa.screentranslation.ui.select_language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.data.PreferencesKeys
import com.userstipa.screentranslation.models.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectLanguageViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectLanguageUiState())
    val uiState: StateFlow<SelectLanguageUiState> = _uiState

    fun fetchData(preferencesKeys: PreferencesKeys) {
        viewModelScope.launch {
            val isLanguagesDownloadEnable = dataStorePreferences.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)
            val selectedLanguage = dataStorePreferences.getLanguage(preferencesKeys)
            _uiState.emit(SelectLanguageUiState(
                isDownloadLanguagesEnable = isLanguagesDownloadEnable,
                selectedLanguage = selectedLanguage
            ))
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