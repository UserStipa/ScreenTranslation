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
import com.userstipa.screentranslation.domain.text_scanner.TextScanner
import com.userstipa.screentranslation.domain.text_translate.TextTranslation
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.ui.uitls.screenshot_util.ScreenshotUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaProjectionServiceImpl : Service(), MediaProjectionService {

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var screenshotUtil: ScreenshotUtil

    @Inject
    lateinit var textScanner: TextScanner

    @Inject
    lateinit var textTranslator: TextTranslation

    private val binder = LocalBinder()
    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + job)
    private var mediaProjection: MediaProjection? = null

    override fun onCreate() {
        super.onCreate()
        (applicationContext as App).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(notificationUtil.getId(), notificationUtil.create())
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun translateScreen() {
        if (mediaProjection == null) return
        serviceScope.launch {
            val image = screenshotUtil.createScreenshot(mediaProjection!!)
            val text = textScanner.getText(image)
            val translatedText = textTranslator.translate(text, "auto", "ru")
        }
    }

    override fun grandPermission(intent: Intent) {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(Activity.RESULT_OK, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaProjectionServiceImpl = this@MediaProjectionServiceImpl
    }
}