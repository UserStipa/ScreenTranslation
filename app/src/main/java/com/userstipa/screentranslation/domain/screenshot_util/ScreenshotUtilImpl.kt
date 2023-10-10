package com.userstipa.screentranslation.domain.screenshot_util

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import com.userstipa.screentranslation.di.dispatchers.DispatchersProvider
import com.userstipa.screentranslation.domain.virtualdisplay_util.VirtualDisplayUtil
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScreenshotUtilImpl @Inject constructor(
    private val virtualDisplayUtil: VirtualDisplayUtil,
    private val dispatchers: DispatchersProvider
) : ScreenshotUtil {

    private val displayWidth = virtualDisplayUtil.getDisplayWidth()
    private val displayHeight = virtualDisplayUtil.getDisplayHeight()
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override suspend fun createScreenshot(mediaProjection: MediaProjection): ResultWrapper<Bitmap> {
        return withContext(dispatchers.main) {
            suspendCoroutine { continuation ->
                try {
                    virtualDisplay = virtualDisplayUtil.create(mediaProjection)
                    imageReader = ImageReader.newInstance(
                        displayWidth,
                        displayHeight,
                        PixelFormat.RGBA_8888,
                        1
                    )
                    virtualDisplay!!.surface = imageReader!!.surface

                    imageReader!!.setOnImageAvailableListener({ reader ->
                        val image = reader.acquireLatestImage()
                        val bitmap = imageToBitmap(image)
                        clear()
                        mediaProjection.stop()
                        continuation.resume(ResultWrapper.Success(bitmap))
                    }, null)
                } catch (e: Throwable) {
                    clear()
                    mediaProjection.stop()
                    continuation.resume(ResultWrapper.Error(e.message ?: "Something went wrong..."))
                }
            }
        }
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * displayWidth
        val bitmap =
            Bitmap.createBitmap(
                displayWidth + rowPadding / pixelStride,
                displayHeight,
                Bitmap.Config.ARGB_8888
            )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        return bitmap
    }

    override fun clear() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
    }


}