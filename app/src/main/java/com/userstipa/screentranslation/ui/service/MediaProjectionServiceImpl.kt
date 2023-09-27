package com.userstipa.screentranslation.ui.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.uitls.screenshot_util.ScreenshotUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class MediaProjectionServiceImpl : Service(), MediaProjectionService {

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var screenshotUtil: ScreenshotUtil

    private val binder = LocalBinder()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

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

    override fun createScreenshot() {
        scope.launch {
            val bitmap = screenshotUtil.createScreenshot(
                mediaProjection ?: throw Exception("Service need media projection")
            )
            saveImage(bitmap)
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

    private fun saveImage(bitmap: Bitmap) {
        try {
            openFileOutput("screenshot.jpeg", MODE_PRIVATE).use {
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}