package com.userstipa.screentranslation.domain.text_translate

sealed interface TextTranslatorState {
    object Ready : TextTranslatorState
    data class Error(val error: Throwable) : TextTranslatorState
}
