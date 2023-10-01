package com.userstipa.screentranslation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.domain.text_translate.TextTranslation
import com.userstipa.screentranslation.models.Language
import com.userstipa.screentranslation.models.LanguageType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val textTranslation: TextTranslation
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val eventFlow = eventChannel.receiveAsFlow()

    private val _sourceLanguage = MutableLiveData<Language>()
    val sourceLanguage: LiveData<Language> get() = _sourceLanguage

    private val _targetLanguage = MutableLiveData<Language>()
    val targetLanguage: LiveData<Language> get() = _targetLanguage

    fun fetchData() {
        viewModelScope.launch {
            _sourceLanguage.value =
                dataStorePreferences.getLanguage(LanguageType.SOURCE) ?: Language.English
            _targetLanguage.value =
                dataStorePreferences.getLanguage(LanguageType.TARGET) ?: Language.Spanish
        }
    }

    fun prepareTranslate() {
        viewModelScope.launch {
            textTranslation.create(
                onLoading = { sourceLanguage, targetLanguage ->
                    Log.d("TAG", "prepareTranslate: onLoading")
                    viewModelScope.launch {
                        eventChannel.send(Event.TranslatorIsLoading(sourceLanguage, targetLanguage))
                    }
                },
                onLoadingComplete = { sourceLanguage, targetLanguage ->
                    Log.d("TAG", "prepareTranslate: onLoadingComplete")
                    viewModelScope.launch {
                        addDownloadedLanguages(sourceLanguage, targetLanguage)
                        eventChannel.send(Event.TranslatorIsLoadingComplete(sourceLanguage, targetLanguage))
                    }
                },
                onReady = {
                    Log.d("TAG", "prepareTranslate: onReady")
                    viewModelScope.launch {
                        eventChannel.send(Event.TranslatorIsReady)
                    }
                },
                onError = {
                    it.printStackTrace()
                }

            )
        }
    }

    private fun addDownloadedLanguages(sourceLanguage: Language, targetLanguage: Language) {
        viewModelScope.launch {
            dataStorePreferences.addDownloadedLanguage(sourceLanguage)
            dataStorePreferences.addDownloadedLanguage(targetLanguage)
        }
    }


}