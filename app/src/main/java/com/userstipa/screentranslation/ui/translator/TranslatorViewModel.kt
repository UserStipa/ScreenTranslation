package com.userstipa.screentranslation.ui.translator

import android.media.projection.MediaProjection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val screenTranslator: ScreenTranslator,
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState: StateFlow<TranslatorUiState> = _uiState

    fun translateDisplay(mediaProjection: MediaProjection) {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }

            val isOnlineModeEnable = when(dataStorePreferences.getBoolean(PreferencesKeys.IS_ONLINE_MODE_ENABLE)) {
                true -> ScreenTranslator.InternetStatus.ONLINE
                false -> ScreenTranslator.InternetStatus.OFFLINE
            }

            when (val result = screenTranslator.translateTextFromDisplay(mediaProjection, isOnlineModeEnable)) {
                is ResultWrapper.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(outputText = result.message, isLoading = false)
                    }
                }

                is ResultWrapper.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(outputText = result.data, isLoading = false)
                    }
                }
            }
        }
    }
}