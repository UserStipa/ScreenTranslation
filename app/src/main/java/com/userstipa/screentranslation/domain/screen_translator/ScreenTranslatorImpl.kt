package com.userstipa.screentranslation.domain.screen_translator

import android.media.projection.MediaProjection
import com.userstipa.screentranslation.domain.text_scanner.TextScanner
import com.userstipa.screentranslation.domain.text_translate.TextTranslator
import com.userstipa.screentranslation.domain.screenshot_util.ScreenshotUtil
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator.InternetStatus
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import javax.inject.Inject

class ScreenTranslatorImpl @Inject constructor(
    private val textTranslator: TextTranslator,
    private val textScanner: TextScanner,
    private val screenshotUtil: ScreenshotUtil
) : ScreenTranslator {

    override fun init() {
        textScanner.init()
    }

    override suspend fun translateTextFromDisplay(mediaProjection: MediaProjection, internetStatus: InternetStatus): ResultWrapper<String> {
        val screenshot = when(val result = screenshotUtil.createScreenshot(mediaProjection)) {
            is ResultWrapper.Error -> return ResultWrapper.Error(result.message)
            is ResultWrapper.Success -> result.data
        }

        val text = when(val result = textScanner.getText(screenshot)) {
            is ResultWrapper.Error -> return ResultWrapper.Error(result.message)
            is ResultWrapper.Success -> result.data
        }

        val result = when(internetStatus) {
            InternetStatus.ONLINE -> textTranslator.translateOnline(text)
            InternetStatus.OFFLINE -> textTranslator.translateOffline(text)
        }

        return result
    }

    override fun clear() {
        textScanner.clear()
        textTranslator.clear()
        screenshotUtil.clear()
    }
}