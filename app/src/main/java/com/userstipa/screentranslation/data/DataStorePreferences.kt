package com.userstipa.screentranslation.data

import com.userstipa.screentranslation.languages.Language

interface DataStorePreferences {

    suspend fun setPreferences(preferencesKeys: PreferencesKeys, boolean: Boolean)

    suspend fun setPreferences(preferencesKeys: PreferencesKeys, language: Language)

    suspend fun getBoolean(preferencesKeys: PreferencesKeys): Boolean

    suspend fun getLanguage(preferencesKeys: PreferencesKeys): Language

}