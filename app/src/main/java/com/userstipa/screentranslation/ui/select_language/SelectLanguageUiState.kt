package com.userstipa.screentranslation.ui.select_language

import com.userstipa.screentranslation.data.DataStorePreferencesImpl
import com.userstipa.screentranslation.languages.Language

data class SelectLanguageUiState(
    var isDownloadLanguagesEnable: Boolean = false,
    var selectedLanguage: Language = DataStorePreferencesImpl.DEFAULT_SOURCE_LANGUAGE,
)