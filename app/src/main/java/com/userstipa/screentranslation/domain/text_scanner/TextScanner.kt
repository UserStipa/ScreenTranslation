package com.userstipa.screentranslation.domain.text_scanner

import android.graphics.Bitmap

interface TextScanner {

    suspend fun getText(bitmap: Bitmap): String

    fun create()

    fun close()

}