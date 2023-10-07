package com.userstipa.screentranslation.ui.service

import android.content.Intent

interface MediaProjectionService {

    fun translateScreen(callback: (text: String) -> Unit)

    fun grandPermission(intent: Intent)


}