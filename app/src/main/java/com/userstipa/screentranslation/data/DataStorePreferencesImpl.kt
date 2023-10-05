package com.userstipa.screentranslation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.userstipa.screentranslation.languages.Language
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStorePreferencesImpl @Inject constructor(
    private val context: Context,
) : DataStorePreferences {

    override val defaultSourceLanguage = Language.English
    override val defaultTargetLanguage = Language.Spanish

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

    override suspend fun setPreferences(
        preferencesKeys: PreferencesKeys,
        boolean: Boolean
    ) {
        val preferenceKey = booleanPreferencesKey(preferencesKeys.name)
        context.dataStore.edit { preferences ->
            preferences[preferenceKey] = boolean
        }
    }

    override suspend fun setPreferences(preferencesKeys: PreferencesKeys, language: Language) {
        val preferencesKey = stringPreferencesKey(preferencesKeys.name)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = language.code
        }
    }

    override suspend fun getBoolean(preferencesKeys: PreferencesKeys): Boolean {
        val preferencesKey = booleanPreferencesKey(preferencesKeys.name)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey] ?: false
    }

    override suspend fun getLanguage(preferencesKeys: PreferencesKeys): Language {
        val preferencesKey = stringPreferencesKey(preferencesKeys.name)
        val preferences = context.dataStore.data.first()
        val value = preferences[preferencesKey]

        if (value == null && preferencesKey.name == PreferencesKeys.SOURCE_LANGUAGE.name) return defaultSourceLanguage
        if (value == null && preferencesKey.name == PreferencesKeys.TARGET_LANGUAGE.name) return defaultTargetLanguage

        return Language.entries.first { it.code == value }
    }

    companion object {
        private const val PREFERENCES_NAME = "PREFERENCES_NAME"
    }
}