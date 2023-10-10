package com.userstipa.screentranslation.ui.translator

import android.media.projection.MediaProjection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.di.dispatchers.DispatchersProvider
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val screenTranslator: ScreenTranslator,
    private val dispatcher: DispatchersProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState: StateFlow<TranslatorUiState> = _uiState

    fun translateDisplay(mediaProjection: MediaProjection) {
        viewModelScope.launch(dispatcher.main) {

            _uiState.update { currentState -> currentState.copy(isLoading = true) }

            when (val result = screenTranslator.translateTextFromDisplay(mediaProjection)) {
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