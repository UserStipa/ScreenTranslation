package com.userstipa.screentranslation.uitls.notification_util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import com.userstipa.screentranslation.R
import com.userstipa.screentranslation.ui.translator.TranslatorActivity

class NotificationUtilImpl(
    private val context: Context
) : NotificationUtil {

    private var notificationManager: NotificationManager? = null

    override fun create(): Notification {
        createChannel()
        return Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setContentIntent(createPIntent())
            .setSmallIcon(R.drawable.baseline_translate_24)
            .build()
    }

    override fun getId(): Int {
        return NOTIFICATION_ID
    }

    private fun createPIntent(): PendingIntent {
        val intent = Intent(context, TranslatorActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            context.applicationContext,
            INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        getManager().createNotificationChannel(channel)
    }

    private fun getManager(): NotificationManager {
        if (notificationManager == null) notificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager
        return notificationManager as NotificationManager
    }

    companion object {
        private const val NOTIFICATION_ID = 1010
        private const val CHANNEL_ID = "SCREEN_TRANSLATION_NOTIFICATION_CHANNEL"
        private const val CHANNEL_NAME = "Screen Translation Notifications"
        private const val INTENT_REQUEST_CODE = 0
    }
}