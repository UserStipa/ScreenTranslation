package com.userstipa.screentranslation.domain.text_translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.userstipa.screentranslation.data.api.TranslateApi
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.languages.Language
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextTranslatorImpl @Inject constructor(
    private val api: TranslateApi
) : TextTranslator {

    private var translator: Translator? = null

    override fun init(
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


    override suspend fun translateOffline(text: String): ResultWrapper<String> {
        return suspendCoroutine { continuation ->
            try {
                translator!!.translate(text)
                    .addOnSuccessListener {
                        continuation.resume(ResultWrapper.Success(it))
                    }
                    .addOnFailureListener {
                        val message =
                            "Something went wrong... Error: ${it.message ?: "Something went wrong... Fail caused by ${this::class.simpleName}"}"
                        continuation.resume(ResultWrapper.Error(message))
                    }
            } catch (e: Throwable) {
                throw e
                continuation.resume(
                    ResultWrapper.Error(
                        e.message
                            ?: "Something went wrong... Exception caused by ${this::class.simpleName}"
                    )
                )
            }
        }
    }

    override suspend fun translateOnline(text: String): ResultWrapper<String> {
        return ResultWrapper.Success(text)
    }

    override fun clear() {
        translator?.close()
        translator = null
    }
}