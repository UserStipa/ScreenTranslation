package com.userstipa.screentranslation.data.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query


interface TranslateApi {

    @POST("/translate")
    suspend fun translate(
        @Query("source") sourceLanguageCode: String,
        @Query("target") targetLanguageCode: String,
        @Query("q") text: String
    ): Response<ResponseTranslateText>
}