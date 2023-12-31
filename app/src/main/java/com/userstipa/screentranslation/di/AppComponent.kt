package com.userstipa.screentranslation.di

import android.content.Context
import com.userstipa.screentranslation.ui.home.HomeFragment
import com.userstipa.screentranslation.ui.select_language.SelectLanguageFragment
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl
import com.userstipa.screentranslation.ui.translator.TranslatorActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(service: MediaProjectionServiceImpl)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: SelectLanguageFragment)

    fun inject(activity: TranslatorActivity)

}