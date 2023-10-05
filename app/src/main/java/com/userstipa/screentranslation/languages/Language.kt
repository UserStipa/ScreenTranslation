package com.userstipa.screentranslation.languages

enum class Language(val title: String, val code: String) {
    English("English", "en"),
    Esperanto("Esperanto", "eo"),
    German("German", "de"),
    Italian("Italian", "it"),
    Korean("Korean", "ko"),
    Portuguese("Portuguese", "pt"),
    Spanish("Spanish", "es"),
    Russian("Russian", "ru");

    companion object {
        fun getSourceLanguages(): List<Language> {
            return entries.dropLast(1)
        }

        fun getTargetLanguages(): List<Language> {
            return entries
        }
    }
}