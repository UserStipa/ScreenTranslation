package com.userstipa.screentranslation.domain.text_translate

import com.userstipa.screentranslation.domain.wrapper.ResultWrapper
import com.userstipa.screentranslation.languages.Language

interface TextTranslator {

    suspend fun init(sourceLanguage: Language, targetLanguage: Language, isDownloadLanguagesEnable: Boolean): State

    suspend fun translateOnline(text: String): ResultWrapper<String>

    suspend fun translateOffline(text: String): ResultWrapper<String>

    fun clear()

    enum class State {
        READY, ERROR
    }

}