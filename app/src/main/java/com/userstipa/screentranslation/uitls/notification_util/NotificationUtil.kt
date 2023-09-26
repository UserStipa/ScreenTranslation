package com.userstipa.screentranslation.uitls.notification_util

import android.app.Notification

interface NotificationUtil {

    fun create(): Notification

    fun getId(): Int
}