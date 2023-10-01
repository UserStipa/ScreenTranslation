package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.models.Language
import java.lang.Exception

interface TextTranslation {

    suspend fun create(
        onLoading: (sourceLanguage: Language, targetLanguage: Language) -> Unit,
        onLoadingComplete: (sourceLanguage: Language, targetLanguage: Language) -> Unit,
        onReady: () -> Unit,
        onError: (e: Exception) -> Unit
    )

    suspend fun translate(text: String): String

    fun close()

}