package com.userstipa.screentranslation.domain.virtualdisplay_util

import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection

interface VirtualDisplayUtil {

    fun create(mediaProjection: MediaProjection): VirtualDisplay

    fun getDisplayWidth(): Int

    fun getDisplayHeight(): Int

}