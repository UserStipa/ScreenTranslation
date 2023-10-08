package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.domain.wrapper.ResultWrapper

interface TextTranslator {

    suspend fun init(
        onDownload: () -> Unit,
        onDownloadComplete: () -> Unit,
        onReady: () -> Unit,
        onError: (error: Exception) -> Unit
    )

    suspend fun translateOnline(text: String): ResultWrapper<String>

    suspend fun translateOffline(text: String): ResultWrapper<String>

    fun clear()

}