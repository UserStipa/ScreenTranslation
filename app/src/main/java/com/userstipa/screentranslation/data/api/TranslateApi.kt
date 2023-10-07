package com.userstipa.screentranslation.data.api

import retrofit2.Response


interface TranslateApi {

    fun translate() : Response<ResponseTranslateText>
}