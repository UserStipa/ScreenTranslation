package com.userstipa.screentranslation.domain.text_translate

class TextTranslationImpl : TextTranslation {

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String {
        return text
    }
}