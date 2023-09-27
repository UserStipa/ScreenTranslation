package com.userstipa.screentranslation.domain.text_translate

interface TextTranslation {

    suspend fun translate(text: String, sourceLanguage: String, targetLanguage: String): String

}