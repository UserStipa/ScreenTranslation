package com.userstipa.screentranslation.domain.screen_translator

import android.media.projection.MediaProjection
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper

interface ScreenTranslator {

    fun init()

    suspend fun translateTextFromDisplay(
        mediaProjection: MediaProjection,
    ): ResultWrapper<String>

    fun clear()

    enum class InternetStatus {
        ONLINE, OFFLINE
    }
}