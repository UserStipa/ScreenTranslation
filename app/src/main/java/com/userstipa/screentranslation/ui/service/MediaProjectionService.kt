package com.userstipa.screentranslation.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.userstipa.screentranslation.uitls.notification_util.NotificationUtilImpl

class MediaProjectionService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationUtil = NotificationUtilImpl(applicationContext)
        startForeground(notificationUtil.getId(), notificationUtil.create())
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        throw Exception("The service is not allowed for binding")
    }


}