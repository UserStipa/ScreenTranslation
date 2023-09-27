package com.userstipa.screentranslation.ui.uitls.screenshot_util

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import com.userstipa.screentranslation.ui.uitls.virtualdisplay_util.VirtualDisplayUtil
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ScreenshotUtilImpl @Inject constructor(
    private val virtualDisplayUtil: VirtualDisplayUtil
) : ScreenshotUtil {

    private val displayWidth = virtualDisplayUtil.getDisplayWidth()
    private val displayHeight = virtualDisplayUtil.getDisplayHeight()

    override suspend fun createScreenshot(mediaProjection: MediaProjection): Bitmap {

        val virtualDisplay = virtualDisplayUtil.create(mediaProjection)
        val imageReader =
            ImageReader.newInstance(displayWidth, displayHeight, PixelFormat.RGBA_8888, 2)
        virtualDisplay.surface = imageReader.surface

        return suspendCoroutine { continuation ->
            imageReader.setOnImageAvailableListener({ reader ->
                try {
                    val image = reader.acquireLatestImage()
                    val bitmap = imageToBitmap(image)
                    continuation.resume(bitmap)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                } finally {
                    virtualDisplay.release()
                    imageReader.close()
                }
            }, null)
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


}