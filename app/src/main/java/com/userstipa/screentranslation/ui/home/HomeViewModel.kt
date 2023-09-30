package com.userstipa.screentranslation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.models.Language
import com.userstipa.screentranslation.models.LanguageType
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _sourceLanguage = MutableLiveData<Language>()
    val sourceLanguage: LiveData<Language> get() = _sourceLanguage

    private val _targetLanguage = MutableLiveData<Language>()
    val targetLanguage: LiveData<Language> get() = _targetLanguage

    fun fetchData() {
        viewModelScope.launch {
            _sourceLanguage.value = dataStorePreferences.getLanguage(LanguageType.SOURCE) ?: Language.Auto
            _targetLanguage.value = dataStorePreferences.getLanguage(LanguageType.TARGET) ?: Language.English
        }
    }

}