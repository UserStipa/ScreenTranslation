package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.domain.text_translate.TextTranslatorState
import com.userstipa.screentranslation.languages.Language
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var preferencesFake: DataStorePreferencesFake
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var textTranslatorFake: TextTranslatorFake

    @Before
    fun setUp() {
        val dispatcherProvider = TestDispatcherProvider()
        testDispatcher = dispatcherProvider.testDispatcher
        preferencesFake = DataStorePreferencesFake()
        textTranslatorFake = TextTranslatorFake()
        viewModel = HomeViewModel(preferencesFake, textTranslatorFake, dispatcherProvider)
    }

    @Test
    fun fetchCurrentLanguages() = runTest {
        val expectedSourceLanguage = Language.entries.random()
        val expectedTargetLanguage = Language.entries.random()
        preferencesFake.setPreferences(PreferencesKeys.SOURCE_LANGUAGE, expectedSourceLanguage)
        preferencesFake.setPreferences(PreferencesKeys.TARGET_LANGUAGE, expectedTargetLanguage)

        viewModel.fetchCurrentLanguages()

        val actualSourceLanguage = viewModel.uiState.value.sourceLanguage
        val actualTargetLanguage = viewModel.uiState.value.targetLanguage

        Assert.assertEquals(expectedSourceLanguage, actualSourceLanguage)
        Assert.assertEquals(expectedTargetLanguage, actualTargetLanguage)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `prepareTextTranslator - Successful`() = runTest(testDispatcher) {
        textTranslatorFake.initResult = TextTranslatorState.Ready
        val expectedResults = listOf(
            HomeUiState( //Init value
                isLoading = false,
                error = null,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = true,
                isIconClickable = true,
                isIconEnable = false
            ),
            HomeUiState( //Loading value
                isLoading = true,
                error = null,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = false,
                isIconClickable = false,
                isIconEnable = false
            ),
            HomeUiState( //Final value
                isLoading = false,
                error = null,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = false,
                isIconClickable = false,
                isIconEnable = false
            )
        )
        val actualResults = mutableListOf<HomeUiState>()
        backgroundScope.launch {
            viewModel.uiState.toList(actualResults)
        }
        viewModel.prepareTextTranslator()
        advanceUntilIdle()
        Assert.assertEquals(expectedResults, actualResults)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `prepareTextTranslator - Error`() = runTest(testDispatcher) {
        val exceptionMessage = "Test exception"
        textTranslatorFake.initResult = TextTranslatorState.Error(Exception(exceptionMessage))
        val expectedResults = listOf(
            HomeUiState( //Init value
                isLoading = false,
                error = null,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = true,
                isIconClickable = true,
                isIconEnable = false
            ),
            HomeUiState( //Loading value
                isLoading = true,
                error = null,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = false,
                isIconClickable = false,
                isIconEnable = false
            ),
            HomeUiState( //Final value
                isLoading = false,
                error = exceptionMessage,
                sourceLanguage = preferencesFake.defaultSourceLanguage,
                targetLanguage = preferencesFake.defaultTargetLanguage,
                isSelectLanguagesClickable = false,
                isIconClickable = false,
                isIconEnable = false
            )
        )
        val actualResults = mutableListOf<HomeUiState>()
        backgroundScope.launch {
            viewModel.uiState.toList(actualResults)
        }
        viewModel.prepareTextTranslator()
        advanceUntilIdle()
        Assert.assertEquals(expectedResults, actualResults)
    }
}