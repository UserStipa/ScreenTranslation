package com.userstipa.screentranslation.di

import androidx.lifecycle.ViewModel
import com.userstipa.screentranslation.ui.home.HomeViewModel
import com.userstipa.screentranslation.ui.select_language.SelectLanguageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectLanguageViewModel::class)
    abstract fun bindSelectLanguageViewModel(viewModel: SelectLanguageViewModel): ViewModel

}