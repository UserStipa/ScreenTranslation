package com.userstipa.screentranslation.domain.text_scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TextScannerImpl : TextScanner {

    private var recognizer: TextRecognizer? = null

    override fun create() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun getText(bitmap: Bitmap): String {
        return suspendCoroutine { continuation ->
            recognizer!!.process(bitmap, ROTATION_DEGREES)
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
                .addOnSuccessListener {
                    continuation.resume(it.text)
                }
        }
    }

    override fun close() {
        recognizer!!.close()
    }

    companion object {
        private const val ROTATION_DEGREES = 0
    }

}