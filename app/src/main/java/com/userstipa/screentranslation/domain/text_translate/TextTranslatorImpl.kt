package com.userstipa.screentranslation.domain.text_translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.userstipa.screentranslation.data.api.TranslateApi
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextTranslatorImpl @Inject constructor(
    private val api: TranslateApi,
    private val dataStore: DataStorePreferences
) : TextTranslator {

    private var translator: Translator? = null

    override suspend fun init(
        onDownload: () -> Unit,
        onDownloadComplete: () -> Unit,
        onReady: () -> Unit,
        onError: (error: Exception) -> Unit
    ) {
        val isDownloadLanguageEnable = dataStore.getBoolean(PreferencesKeys.IS_LANGUAGES_DOWNLOAD)
        val sourceLanguage = dataStore.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
        val targetLanguage = dataStore.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.code)
            .setTargetLanguage(targetLanguage.code)
            .build()
        translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()

        if (isDownloadLanguageEnable) {
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
        } else {
            onReady.invoke()
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