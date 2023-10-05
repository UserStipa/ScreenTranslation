package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.languages.Language

data class HomeUiState(
    val isLoading: Boolean = false,
    val isServiceReady: Boolean = false,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val error: String? = null
)

