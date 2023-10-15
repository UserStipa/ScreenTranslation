package com.userstipa.screentranslation.ui.select_language

import com.userstipa.screentranslation.testUtils.DataStorePreferencesFake
import com.userstipa.screentranslation.testUtils.DispatcherProviderTest
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.languages.Language
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SelectLanguageViewModelTest {

    private lateinit var viewModel: SelectLanguageViewModel
    private lateinit var preferencesFake: DataStorePreferencesFake
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        val dispatcherProvider = DispatcherProviderTest()
        testDispatcher = dispatcherProvider.testDispatcher
        preferencesFake = DataStorePreferencesFake()
        viewModel = SelectLanguageViewModel(preferencesFake, dispatcherProvider)
    }

    @Test
    fun getUiState() {
        val expectedValue = SelectLanguageUiState(
            isDownloadLanguagesEnable = false,
            selectedLanguage = preferencesFake.defaultSourceLanguage
        )
        val actualValue = viewModel.uiState.value
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `fetchData - Source Language`() = runTest {
        val expectedSelectedLanguage = Language.entries.random()

        preferencesFake.setPreferences(PreferencesKeys.SOURCE_LANGUAGE, expectedSelectedLanguage)

        viewModel.fetchData(PreferencesKeys.SOURCE_LANGUAGE)

        val actualValue = viewModel.uiState.value
        val expectedValue = SelectLanguageUiState(
            isDownloadLanguagesEnable = false,
            selectedLanguage = expectedSelectedLanguage
        )
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `fetchData - Default Source Language`() = runTest {
        val expectedSelectedLanguage = preferencesFake.defaultSourceLanguage

        viewModel.fetchData(PreferencesKeys.SOURCE_LANGUAGE)

        val actualValue = viewModel.uiState.value
        val expectedValue = SelectLanguageUiState(
            isDownloadLanguagesEnable = false,
            selectedLanguage = expectedSelectedLanguage
        )
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `fetchData - Target Language`() = runTest {
        val expectedSelectedLanguage = Language.entries.random()

        preferencesFake.setPreferences(PreferencesKeys.TARGET_LANGUAGE, expectedSelectedLanguage)

        viewModel.fetchData(PreferencesKeys.TARGET_LANGUAGE)

        val actualValue = viewModel.uiState.value
        val expectedValue = SelectLanguageUiState(
            isDownloadLanguagesEnable = false,
            selectedLanguage = expectedSelectedLanguage
        )
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `fetchData - Default Target Language`() = runTest {
        val expectedSelectedLanguage = preferencesFake.defaultTargetLanguage

        viewModel.fetchData(PreferencesKeys.TARGET_LANGUAGE)

        val actualValue = viewModel.uiState.value
        val expectedValue = SelectLanguageUiState(
            isDownloadLanguagesEnable = false,
            selectedLanguage = expectedSelectedLanguage
        )
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `setLanguage - Source Language`() = runTest {
        val language = Language.entries.random()
        viewModel.setLanguage(PreferencesKeys.SOURCE_LANGUAGE, language)

        val actualValue = preferencesFake.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
        assertEquals(language, actualValue)
    }

    @Test
    fun `setLanguage - Target Language`() = runTest {
        val language = Language.entries.random()
        viewModel.setLanguage(PreferencesKeys.TARGET_LANGUAGE, language)

        val actualValue = preferencesFake.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
        assertEquals(language, actualValue)
    }

    @Test
    fun `setDownloadLanguages - enable`() = runTest {
        val expectedValue = true
        viewModel.setDownloadLanguages(expectedValue)
        val actualValue = preferencesFake.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `setDownloadLanguages - disable`() = runTest {
        val expectedValue = false
        viewModel.setDownloadLanguages(expectedValue)
        val actualValue = preferencesFake.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)
        assertEquals(expectedValue, actualValue)
    }
}