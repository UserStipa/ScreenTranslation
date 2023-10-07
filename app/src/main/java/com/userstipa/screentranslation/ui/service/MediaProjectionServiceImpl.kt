package com.userstipa.screentranslation.ui.service

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaProjectionServiceImpl : Service(), MediaProjectionService {

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var screenTranslator: ScreenTranslator

    private val binder = LocalBinder()
    private var mediaProjection: MediaProjection? = null
    private val job = SupervisorJob()
    private val handlerThread = HandlerThread("ServiceThread")
    private val serviceCoroutineScope: CoroutineScope

    init {
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val dispatcher = handler.asCoroutineDispatcher("ServiceDispatcher")
        serviceCoroutineScope = CoroutineScope(dispatcher + job)
    }

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

    override fun translateScreen(callback: (text: String) -> Unit) {
        if (mediaProjection == null) return

        serviceCoroutineScope.launch {
            when (val result = screenTranslator.translateTextFromDisplay(mediaProjection!!)) {
                is ResultWrapper.Error -> {
                    callback.invoke(result.message)
                }

                is ResultWrapper.Success -> {
                    callback.invoke(result.data)
                }
            }
        }
    }

    override fun grandPermission(intent: Intent) {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(Activity.RESULT_OK, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        screenTranslator.clear()
        handlerThread.quit()
        job.cancel()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaProjectionServiceImpl = this@MediaProjectionServiceImpl
    }
}