package com.userstipa.screentranslation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.di.dispatchers.DispatchersProvider
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
    private val textTranslation: TextTranslator,
    private val dispatcher: DispatchersProvider
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(
        HomeUiState(
            sourceLanguage = dataStorePreferences.defaultSourceLanguage,
            targetLanguage = dataStorePreferences.defaultTargetLanguage
        )
    )
    val uiState: StateFlow<HomeUiState> = _homeUiState

    private val _isTranslatorReady = MutableSharedFlow<Boolean>()
    val isTranslatorReady: SharedFlow<Boolean> = _isTranslatorReady.asSharedFlow()

    fun fetchCurrentLanguages() {
        viewModelScope.launch(dispatcher.io) {
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

    fun prepareTextTranslator() {
        viewModelScope.launch(dispatcher.io) {
            _homeUiState.update { currentUiState -> currentUiState.copy(isLoading = true) }
            val sourceLanguage = dataStorePreferences.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
            val targetLanguage = dataStorePreferences.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
            val isDownloadLanguageEnable =
                dataStorePreferences.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)
            when (textTranslation.init(sourceLanguage, targetLanguage, isDownloadLanguageEnable)) {
                TextTranslator.State.ERROR -> {
                    _homeUiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, error = "Error")
                    }
                }

                TextTranslator.State.READY -> {
                    _isTranslatorReady.emit(true)
                    _homeUiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false)
                    }
                }
            }
        }
    }
}