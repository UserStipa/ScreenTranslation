package com.userstipa.screentranslation

import android.app.Application
import com.userstipa.screentranslation.di.AppComponent
import com.userstipa.screentranslation.di.DaggerAppComponent

class App : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

}