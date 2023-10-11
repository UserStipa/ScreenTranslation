package com.userstipa.screentranslation.domain.text_translate

import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.userstipa.screentranslation.data.api.TranslateApi
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.PreferencesKeys
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.languages.Language
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextTranslatorImpl @Inject constructor(
    private val api: TranslateApi,
    private val dataStore: DataStorePreferences
) : TextTranslator {

    private var translator: Translator? = null

    override suspend fun init(
        sourceLanguage: Language,
        targetLanguage: Language,
        isDownloadLanguagesEnable: Boolean
    ): TextTranslatorState {
        return suspendCoroutine { continuation ->
            try {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLanguage.code)
                    .setTargetLanguage(targetLanguage.code)
                    .build()
                translator = Translation.getClient(options)

                if (isDownloadLanguagesEnable) {
                    translator!!.downloadModelIfNeeded()
                        .addOnCompleteListener {
                            continuation.resume(TextTranslatorState.Ready)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resume(TextTranslatorState.Error(exception))
                        }
                } else {
                    continuation.resume(TextTranslatorState.Ready)
                }
            } catch (e: Throwable) {
                continuation.resume(TextTranslatorState.Error(e))
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
        return try {
            val sourceLanguage = dataStore.getLanguage(PreferencesKeys.SOURCE_LANGUAGE)
            val targetLanguage = dataStore.getLanguage(PreferencesKeys.TARGET_LANGUAGE)
            val networkResponse = api.translate(sourceLanguage.code, targetLanguage.code, text)
            val networkBody = networkResponse.body()
            if (networkResponse.isSuccessful && networkBody != null) {
                ResultWrapper.Success(networkBody.translatedText)
            } else {
                val message =
                    "Error code: ${networkResponse.code()} Message: ${networkResponse.message()}"
                ResultWrapper.Error(message)
            }
        } catch (e: Throwable) {
            val message =
                "Something went wrong... Error: ${e.message ?: "Something went wrong... Fail caused by ${this::class.simpleName}"}"
            ResultWrapper.Error(message)
        }
    }

    override fun clear() {
        translator?.close()
        translator = null
    }
}