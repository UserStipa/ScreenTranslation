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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.userstipa.screentranslation.App
import com.userstipa.screentranslation.domain.text_scanner.TextScanner
import com.userstipa.screentranslation.domain.text_translate.TextTranslation
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.ui.uitls.screenshot_util.ScreenshotUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.android.asCoroutineDispatcher
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
    private var mediaProjection: MediaProjection? = null
    private val job = SupervisorJob()
    private val handlerThread = HandlerThread("ServiceThread")
    private val serviceCoroutineScope: CoroutineScope

    private val _result = MutableLiveData<String>()
    override val result: LiveData<String> get() = _result

    init {
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val dispatcher = handler.asCoroutineDispatcher("ServiceDispatcher")
        serviceCoroutineScope = CoroutineScope(dispatcher + job)
    }

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

    override fun translateScreen(result: (text: String) -> Unit) {
        if (mediaProjection == null) return

        serviceCoroutineScope.launch {
            val image = screenshotUtil.createScreenshot(mediaProjection!!)
            val text = textScanner.getText(image)
            val translatedText = textTranslator.translate(text, "auto", "ru")
            result.invoke(translatedText)
        }
    }

    override fun grandPermission(intent: Intent) {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = manager.getMediaProjection(Activity.RESULT_OK, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quit()
        job.cancel()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaProjectionServiceImpl = this@MediaProjectionServiceImpl
    }
}