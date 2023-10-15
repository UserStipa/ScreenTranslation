package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.data.api.ResponseTranslateText
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.testUtils.TranslateApiFake
import com.userstipa.screentranslation.testUtils.DataStorePreferencesFake
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class TextTranslatorImplTest {

    private lateinit var textTranslator: TextTranslator
    private lateinit var preferencesFake: DataStorePreferencesFake
    private lateinit var apiFake: TranslateApiFake

    @Before
    fun setUp() {
        apiFake = TranslateApiFake()
        preferencesFake = DataStorePreferencesFake()
        textTranslator = TextTranslatorImpl(apiFake, preferencesFake)
    }

    @Test
    fun init() {
    }

    @Test
    fun translateOffline() {
    }

    @Test
    fun `translateOnline - Success`() = runTest {
        val expectedValue = "Test response value"
        apiFake.responseValue = Response.success(ResponseTranslateText(expectedValue))

        val result = textTranslator.translateOnline("Test response value") as ResultWrapper.Success
        val actualValue = result.data

        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun `translateOnline - Error`() = runTest {
        val responseCodeError = 400
        val responseBodyError = ResponseBody.create(
            MediaType.parse("application/json"),
            "{\"error\":\"test_error_language_code is not supported\"}"
        )
        apiFake.responseValue = Response.error(responseCodeError, responseBodyError)

        val result = textTranslator.translateOnline("Test response value") as ResultWrapper.Error

        val expectedValue = "Error code: ${responseCodeError}"
        val actualValue = result.message
        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun `translateOnline - Exception`() = runTest {
        val testException = Exception("Test exception")
        apiFake.testException = Exception("Test exception")

        val result = textTranslator.translateOnline("Test response value") as ResultWrapper.Error

        val expectedValue =
            "Something went wrong... Error: ${testException.message ?: "Something went wrong... Fail caused by ${TextTranslatorImpl::class.simpleName}"}"
        val actualValue = result.message
        assertEquals(actualValue, expectedValue)
    }

    @Test
    fun clear() {
    }
}