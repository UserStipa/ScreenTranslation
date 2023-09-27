package com.userstipa.screentranslation.ui.service

import android.content.Intent

interface MediaProjectionService {

    fun createScreenshot()

    fun grandPermission(intent: Intent)

}