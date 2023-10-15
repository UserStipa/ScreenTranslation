package com.userstipa.screentranslation.testUtils

import com.userstipa.screentranslation.data.api.ResponseTranslateText
import com.userstipa.screentranslation.data.api.TranslateApi
import kotlinx.coroutines.delay
import retrofit2.Response

class TranslateApiFake : TranslateApi{

    var responseValue: Response<ResponseTranslateText> = Response.success(ResponseTranslateText(String()))
    var testException: Throwable? = null

    override suspend fun translate(
        sourceLanguageCode: String,
        targetLanguageCode: String,
        text: String
    ): Response<ResponseTranslateText> {
        delay(1000L)
        if (testException != null) throw testException!!
        return responseValue
    }
}