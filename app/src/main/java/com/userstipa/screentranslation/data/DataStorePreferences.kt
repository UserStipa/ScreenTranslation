package com.userstipa.screentranslation.data

import com.userstipa.screentranslation.models.Language
import com.userstipa.screentranslation.models.LanguageType

interface DataStorePreferences {

    suspend fun putLanguage(languageType: LanguageType, language: Language)

    suspend fun getLanguage(languageType: LanguageType): Language?

    suspend fun isLanguageDownloaded(language: Language): Boolean

    suspend fun addDownloadedLanguage(language: Language)

    suspend fun setPreferences(preferencesKeys: PreferencesKeys, boolean: Boolean)

    suspend fun getPreferences(preferencesKeys: PreferencesKeys): Boolean


}