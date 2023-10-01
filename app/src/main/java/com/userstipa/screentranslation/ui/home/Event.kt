package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.models.Language

sealed class Event {
    data class ErrorEvent(val message: String): Event()
    data class TranslatorIsLoading(val sourceLanguage: Language, val targetLanguage: Language): Event()
    data class TranslatorIsLoadingComplete(val sourceLanguage: Language, val targetLanguage: Language): Event()
    object TranslatorIsReady : Event()
}


