package com.userstipa.screentranslation.ui.uitls.screenshot_util

import android.graphics.Bitmap
import android.media.projection.MediaProjection

interface ScreenshotUtil {

    suspend fun createScreenshot(mediaProjection: MediaProjection): Bitmap
}