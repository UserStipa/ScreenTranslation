package com.userstipa.screentranslation.testUtils

import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.languages.Language

class DataStorePreferencesFake : DataStorePreferences {

    private val mapBooleans = mutableMapOf<String, Boolean>()
    private val mapLanguages = mutableMapOf<String, String>()

    override val defaultSourceLanguage: Language
        get() = Language.English
    override val defaultTargetLanguage: Language
        get() = Language.Spanish

    override suspend fun setPreferences(preferencesKeys: PreferencesKeys, boolean: Boolean) {
        mapBooleans[preferencesKeys.name] = boolean
    }

    override suspend fun setPreferences(preferencesKeys: PreferencesKeys, language: Language) {
        mapLanguages[preferencesKeys.name] = language.code
    }

    override suspend fun getBoolean(preferencesKeys: PreferencesKeys): Boolean {
        return mapBooleans[preferencesKeys.name] ?: false
    }

    override suspend fun getLanguage(preferencesKeys: PreferencesKeys): Language {
        val value = mapLanguages[preferencesKeys.name]

        if (value == null && preferencesKeys.name == PreferencesKeys.SOURCE_LANGUAGE.name) return defaultSourceLanguage
        if (value == null && preferencesKeys.name == PreferencesKeys.TARGET_LANGUAGE.name) return defaultTargetLanguage

        return Language.entries.first { it.code == value }
    }
}