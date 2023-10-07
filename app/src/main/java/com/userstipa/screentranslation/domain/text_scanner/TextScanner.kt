package com.userstipa.screentranslation.domain.text_scanner

import android.graphics.Bitmap
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper

interface TextScanner {

    fun init()

    suspend fun getText(bitmap: Bitmap): ResultWrapper<String>

    fun clear()

}