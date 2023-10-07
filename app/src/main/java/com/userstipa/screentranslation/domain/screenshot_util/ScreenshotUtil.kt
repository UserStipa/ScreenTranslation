package com.userstipa.screentranslation.domain.screenshot_util

import android.graphics.Bitmap
import android.media.projection.MediaProjection
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper

interface ScreenshotUtil {

    suspend fun createScreenshot(mediaProjection: MediaProjection): ResultWrapper<Bitmap>

    fun clear()
}