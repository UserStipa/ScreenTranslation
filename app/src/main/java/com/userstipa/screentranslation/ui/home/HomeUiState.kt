package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.languages.Language

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val isSelectLanguagesClickable: Boolean = true,
    val isIconClickable: Boolean = true,
    val isIconEnable: Boolean = false
)

