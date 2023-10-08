package com.userstipa.screentranslation.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface TranslateApi {

    @GET("/translate")
    fun translate(@Query("source") sourceLanguageCode: String, @Query("target") targetLanguageCode: String, @Query("q") text: String) : Response<ResponseTranslateText>
}