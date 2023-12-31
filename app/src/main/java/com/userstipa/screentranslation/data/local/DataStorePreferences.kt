package com.userstipa.screentranslation.data.local

import com.userstipa.screentranslation.languages.Language

interface DataStorePreferences {

    val defaultSourceLanguage: Language

    val defaultTargetLanguage: Language

    suspend fun setPreferences(preferencesKeys: PreferencesKeys, boolean: Boolean)

    suspend fun setPreferences(preferencesKeys: PreferencesKeys, language: Language)

    suspend fun getBoolean(preferencesKeys: PreferencesKeys): Boolean

    suspend fun getLanguage(preferencesKeys: PreferencesKeys): Language
}