package com.userstipa.screentranslation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.userstipa.screentranslation.data.DataStorePreferencesImpl.Companion.PREFERENCES_NAME
import com.userstipa.screentranslation.models.Language
import com.userstipa.screentranslation.models.LanguageType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStorePreferencesImpl @Inject constructor(
    private val context: Context
) : DataStorePreferences {

    override suspend fun putLanguage(languageType: LanguageType, language: Language) {
        val preferencesKey = stringPreferencesKey(languageType.name)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = language.code
        }
    }

    override suspend fun getLanguage(languageType: LanguageType): Language? {
        val preferencesKey = stringPreferencesKey(languageType.name)
        val preferences = context.dataStore.data.first()
        return Language.entries.firstOrNull { it.code == preferences[preferencesKey] }
    }

    override suspend fun isLanguageDownloaded(language: Language): Boolean {
        val preferencesKey = booleanPreferencesKey(language.code)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey] ?: false
    }

    override suspend fun addDownloadedLanguage(language: Language) {
        val preferencesKey = booleanPreferencesKey(language.code)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = true
        }
    }

    companion object {
        const val PREFERENCES_NAME = "PREFERENCES_NAME"
    }
}