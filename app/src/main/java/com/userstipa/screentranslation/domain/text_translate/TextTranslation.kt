package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.languages.Language

interface TextTranslation {

    fun create(
        sourceLanguage: Language,
        targetLanguage: Language,
        isDownloadLanguage: Boolean,
        onDownload: () -> Unit,
        onDownloadComplete: () -> Unit,
        onReady: () -> Unit,
        onError: (error: Exception) -> Unit
    )

    suspend fun translate(text: String): String

    fun close()

}