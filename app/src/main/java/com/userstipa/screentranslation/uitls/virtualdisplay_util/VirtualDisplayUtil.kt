package com.userstipa.screentranslation.uitls.virtualdisplay_util

import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.os.Handler

interface VirtualDisplayUtil {

    fun create(handler: Handler, mediaProjection: MediaProjection): VirtualDisplay

    fun getDisplayWidth(): Int

    fun getDisplayHeight(): Int

}