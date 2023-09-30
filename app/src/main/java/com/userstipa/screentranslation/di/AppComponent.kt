package com.userstipa.screentranslation.di

import android.content.Context
import androidx.fragment.app.Fragment
import com.userstipa.screentranslation.ui.service.MediaProjectionServiceImpl
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

    fun inject(fragment: Fragment)

}