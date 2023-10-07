package com.userstipa.screentranslation.domain.text_scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextScannerImpl : TextScanner {

    private var recognizer: TextRecognizer? = null

    override fun init() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun getText(bitmap: Bitmap): ResultWrapper<String> {
        return suspendCoroutine { continuation ->
            try {
                recognizer!!.process(bitmap, ROTATION_DEGREES)
                    .addOnFailureListener {
                        val message = "Something went wrong... Error: ${it.message ?: "Something went wrong... Fail caused by ${this::class.simpleName}"}"
                        continuation.resume(ResultWrapper.Error(message))
                    }
                    .addOnSuccessListener {
                        continuation.resume(ResultWrapper.Success(it.text))
                    }
            } catch (e: Throwable) {
                continuation.resume(ResultWrapper.Error(e.message ?: "Something went wrong... Exception caused by ${this::class.simpleName}"))
            }
        }
    }

    override fun clear() {
        recognizer?.close()
        recognizer = null
    }

    companion object {
        private const val ROTATION_DEGREES = 0
    }

}