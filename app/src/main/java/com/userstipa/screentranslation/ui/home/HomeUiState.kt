package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.data.DataStorePreferencesImpl
import com.userstipa.screentranslation.models.Language

data class HomeUiState(
    val isLoading: Boolean = false,
    val isServiceReady: Boolean = false,
    val sourceLanguage: Language = DataStorePreferencesImpl.DEFAULT_SOURCE_LANGUAGE,
    val targetLanguage: Language = DataStorePreferencesImpl.DEFAULT_TARGET_LANGUAGE,
    val error: String? = null
)

