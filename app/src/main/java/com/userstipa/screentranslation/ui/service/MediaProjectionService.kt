package com.userstipa.screentranslation.ui.service

import android.content.Intent

interface MediaProjectionService {

    fun translateScreen()

    fun grandPermission(intent: Intent)

}