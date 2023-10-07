package com.userstipa.screentranslation.ui.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import javax.inject.Inject

class MediaProjectionServiceImpl : Service(), MediaProjectionService {

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var screenTranslator: ScreenTranslator

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        (applicationContext as App).appComponent.inject(this)
        screenTranslator.init()
        startForeground(notificationUtil.getId(), notificationUtil.create())
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun getMediaProjection(intent: Intent): MediaProjection {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        return manager.getMediaProjection(Activity.RESULT_OK, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        screenTranslator.clear()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaProjectionServiceImpl = this@MediaProjectionServiceImpl
    }
}