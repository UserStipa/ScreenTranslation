package com.userstipa.screentranslation.ui.home

import com.userstipa.screentranslation.domain.text_translate.TextTranslator
import com.userstipa.screentranslation.domain.text_translate.TextTranslatorState
import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.languages.Language
import kotlinx.coroutines.delay

class TextTranslatorFake : TextTranslator {

    var initResult: TextTranslatorState = TextTranslatorState.Ready

    override suspend fun init(
        sourceLanguage: Language,
        targetLanguage: Language,
        isDownloadLanguagesEnable: Boolean
    ): TextTranslatorState {
        delay(3000)
        return initResult
    }

    override suspend fun translateOnline(text: String): ResultWrapper<String> {
        TODO("Not yet implemented")
    }

    override suspend fun translateOffline(text: String): ResultWrapper<String> {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}