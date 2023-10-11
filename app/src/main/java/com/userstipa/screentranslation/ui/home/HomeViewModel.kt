package com.userstipa.screentranslation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.di.dispatchers.DispatchersProvider
import com.userstipa.screentranslation.domain.text_translate.TextTranslator
import com.userstipa.screentranslation.domain.text_translate.TextTranslatorState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val preferences: DataStorePreferences,
    private val textTranslation: TextTranslator,
    private val dispatcher: DispatchersProvider
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(
        HomeUiState(
            sourceLanguage = preferences.defaultSourceLanguage,
            targetLanguage = preferences.defaultTargetLanguage
        )
    )
    val uiState: StateFlow<HomeUiState> = _homeUiState

    private val _isTranslatorReady = MutableSharedFlow<Boolean>()
    val isTranslatorReady: SharedFlow<Boolean> = _isTranslatorReady.asSharedFlow()

    fun fetchCurrentLanguages() {
        viewModelScope.launch(dispatcher.io) {
            _homeUiState.update { currentUiState ->
                val sourceLanguage =
                    preferences.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
                val targetLanguage =
                    preferences.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
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
            val sourceLanguage = preferences.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
            val targetLanguage = preferences.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
            val isDownloadEnable = preferences.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)

            when (val result = textTranslation.init(sourceLanguage, targetLanguage, isDownloadEnable)) {
                is TextTranslatorState.Error -> {
                    _homeUiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false, error = result.error.message)
                    }
                }
                is TextTranslatorState.Ready -> {
                    _isTranslatorReady.emit(true)
                    _homeUiState.update { currentUiState ->
                        currentUiState.copy(isLoading = false)
                    }
                }
            }
        }
    }
}