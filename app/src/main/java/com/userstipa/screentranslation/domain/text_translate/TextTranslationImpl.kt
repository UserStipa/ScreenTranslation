package com.userstipa.screentranslation.domain.text_translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.userstipa.screentranslation.models.Language
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextTranslationImpl : TextTranslation {

    private var translator: Translator? = null

    override fun create(
        sourceLanguage: Language,
        targetLanguage: Language,
        isDownloadLanguage: Boolean,
        onDownload: () -> Unit,
        onDownloadComplete: () -> Unit,
        onReady: () -> Unit,
        onError: (error: Exception) -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.code)
            .setTargetLanguage(targetLanguage.code)
            .build()
        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        if (!isDownloadLanguage) {
            onReady.invoke()
            return

        } else {
            onDownload.invoke()
            translator!!.downloadModelIfNeeded(conditions)
                .addOnCompleteListener {
                    onDownloadComplete.invoke()
                    onReady.invoke()
                    return@addOnCompleteListener
                }
                .addOnFailureListener {
                    onError.invoke(it)
                    return@addOnFailureListener
                }
        }
    }


    override suspend fun translate(text: String): String {
        return suspendCoroutine { continuation ->
            translator!!.translate(text)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resume("Something went wrong... Error: ${it.message ?: "Message is empty"}")
                }
        }
    }

    override fun close() {
        translator?.close()
    }
}