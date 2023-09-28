package com.userstipa.screentranslation.ui.service

import android.content.Intent
import androidx.lifecycle.LiveData

interface MediaProjectionService {

    val result: LiveData<String>

    fun translateScreen(result: (text: String) -> Unit)

    fun grandPermission(intent: Intent)


}