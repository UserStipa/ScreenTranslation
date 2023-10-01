package com.userstipa.screentranslation.domain.text_translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.models.Language
import com.userstipa.screentranslation.models.LanguageType
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TextTranslationImpl @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : TextTranslation {

    private var translator: Translator? = null

    override suspend fun create(
        onLoading: (sourceLanguage: Language, targetLanguage: Language) -> Unit,
        onLoadingComplete: (sourceLanguage: Language, targetLanguage: Language) -> Unit,
        onReady: () -> Unit,
        onError: (e: Exception) -> Unit
    ) {
        try {
            val sourceLanguage =
                dataStorePreferences.getLanguage(LanguageType.SOURCE) ?: Language.English
            val targetLanguage =
                dataStorePreferences.getLanguage(LanguageType.TARGET) ?: Language.Spanish

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage.code)
                .setTargetLanguage(targetLanguage.code)
                .build()
            translator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            val isSourceLanguageDownloaded =
                dataStorePreferences.isLanguageDownloaded(sourceLanguage)
            val isTargetLanguageDownloaded =
                dataStorePreferences.isLanguageDownloaded(targetLanguage)

            if (isSourceLanguageDownloaded && isTargetLanguageDownloaded) {
                onReady.invoke()
            } else {
                onLoading.invoke(sourceLanguage, targetLanguage)
                translator!!.downloadModelIfNeeded(conditions)
                    .addOnCompleteListener {
                        onLoadingComplete.invoke(sourceLanguage, targetLanguage)
                        onReady.invoke()
                    }
                    .addOnFailureListener {
                        onError.invoke(it)
                    }
            }
        } catch (e: Exception) {
            onError.invoke(e)
        }
    }

    override suspend fun translate(text: String): String {
        return suspendCoroutine { continuation ->
            translator!!.translate(text)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override fun close() {
        translator?.close()
    }
}