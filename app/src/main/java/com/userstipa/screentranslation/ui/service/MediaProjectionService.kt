package com.userstipa.screentranslation.ui.service

import android.content.Intent
import android.media.projection.MediaProjection

interface MediaProjectionService {
    
    fun getMediaProjection(intent: Intent): MediaProjection


}