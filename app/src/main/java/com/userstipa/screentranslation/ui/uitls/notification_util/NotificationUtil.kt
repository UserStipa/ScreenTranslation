package com.userstipa.screentranslation.ui.uitls.notification_util

import android.app.Notification

interface NotificationUtil {

    fun create(): Notification

    fun getId(): Int
}