package com.userstipa.screentranslation.domain.virtualdisplay_util

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

class VirtualDisplayUtilImpl constructor(
    private val context: Context
) : VirtualDisplayUtil {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val displayMetrics = DisplayMetrics()

    override fun create(mediaProjection: MediaProjection): VirtualDisplay {
        val densityDpi = context.resources.displayMetrics.densityDpi
        return mediaProjection.createVirtualDisplay(
            VIRTUAL_DISPLAY_NAME,
            getDisplayWidth(),
            getDisplayHeight(),
            densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR or DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
            null,
            null,
            null
        )
    }

    override fun getDisplayWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }

    override fun getDisplayHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }
    }

    companion object {
        private const val VIRTUAL_DISPLAY_NAME = "ScreenTranslationDisplay"
    }
}