package com.userstipa.screentranslation.ui.service

import android.content.Intent

interface MediaProjectionService {

    fun translateScreen(result: (text: String) -> Unit)

    fun grandPermission(intent: Intent)


}