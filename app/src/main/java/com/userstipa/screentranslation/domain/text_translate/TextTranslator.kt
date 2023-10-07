package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.languages.Language

interface TextTranslator {

    fun init(
        sourceLanguage: Language,
        targetLanguage: Language,
        isDownloadLanguage: Boolean,
        onDownload: () -> Unit,
        onDownloadComplete: () -> Unit,
        onReady: () -> Unit,
        onError: (error: Exception) -> Unit
    )

    suspend fun translateOnline(text: String): ResultWrapper<String>

    suspend fun translateOffline(text: String): ResultWrapper<String>

    fun clear()

}