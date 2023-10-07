package com.userstipa.screentranslation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.domain.text_translate.TextTranslator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val textTranslation: TextTranslator
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState(
        sourceLanguage = dataStorePreferences.defaultSourceLanguage,
        targetLanguage = dataStorePreferences.defaultTargetLanguage
    ))
    val uiState: StateFlow<HomeUiState> = _homeUiState

    private val _isTranslatorReady = MutableSharedFlow<Boolean>()
    val isTranslatorReady: SharedFlow<Boolean> = _isTranslatorReady.asSharedFlow()

    fun fetchCurrentLanguages() {
        viewModelScope.launch {
            _homeUiState.update { currentUiState ->
                val sourceLanguage =
                    dataStorePreferences.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
                val targetLanguage =
                    dataStorePreferences.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
                currentUiState.copy(
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage
                )
            }
        }
    }

    fun prepareTranslateService() {
        viewModelScope.launch {
            val sourceLanguage = dataStorePreferences.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
            val targetLanguage = dataStorePreferences.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
            val isDownloadLanguage =
                dataStorePreferences.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)

            textTranslation.init(sourceLanguage, targetLanguage, isDownloadLanguage,
                onDownload = {
                    viewModelScope.launch {
                        _homeUiState.update { it.copy(isLoading = true, isServiceReady = false) }
                    }
                },
                onDownloadComplete = {
                    viewModelScope.launch {
                        _homeUiState.update { it.copy(isLoading = false, isServiceReady = false) }
                    }
                },
                onError = { exception ->
                    viewModelScope.launch {
                        _homeUiState.update {
                            it.copy(error = "Error: ${exception.message}", isServiceReady = false)
                        }
                    }
                },
                onReady = {
                    viewModelScope.launch {
                        _homeUiState.update { it.copy(isServiceReady = true) }
                        _isTranslatorReady.emit(true)
                    }
                })
        }
    }
}