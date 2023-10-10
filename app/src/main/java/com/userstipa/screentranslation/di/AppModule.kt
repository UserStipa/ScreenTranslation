package com.userstipa.screentranslation.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.userstipa.screentranslation.data.api.TranslateApi
import com.userstipa.screentranslation.data.local.DataStorePreferences
import com.userstipa.screentranslation.data.local.DataStorePreferencesImpl
import com.userstipa.screentranslation.di.dispatchers.DispatchersProvider
import com.userstipa.screentranslation.di.dispatchers.DispatchersProviderImpl
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslator
import com.userstipa.screentranslation.domain.screen_translator.ScreenTranslatorImpl
import com.userstipa.screentranslation.domain.screenshot_util.ScreenshotUtil
import com.userstipa.screentranslation.domain.screenshot_util.ScreenshotUtilImpl
import com.userstipa.screentranslation.domain.text_scanner.TextScanner
import com.userstipa.screentranslation.domain.text_scanner.TextScannerImpl
import com.userstipa.screentranslation.domain.text_translate.TextTranslator
import com.userstipa.screentranslation.domain.text_translate.TextTranslatorImpl
import com.userstipa.screentranslation.domain.virtualdisplay_util.VirtualDisplayUtil
import com.userstipa.screentranslation.domain.virtualdisplay_util.VirtualDisplayUtilImpl
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtilImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideVirtualDisplayUtil(context: Context): VirtualDisplayUtil {
        return VirtualDisplayUtilImpl(context)
    }

    @Provides
    @Singleton
    fun provideScreenshotUtil(virtualDisplayUtil: VirtualDisplayUtil, dispatchersProvider: DispatchersProvider): ScreenshotUtil {
        return ScreenshotUtilImpl(virtualDisplayUtil, dispatchersProvider)
    }

    @Provides
    @Singleton
    fun provideNotificationUtil(context: Context): NotificationUtil {
        return NotificationUtilImpl(context)
    }

    @Provides
    @Singleton
    fun provideTextScanner(): TextScanner {
        return TextScannerImpl()
    }

    @Provides
    @Singleton
    fun provideDataStorePreferences(context: Context): DataStorePreferences {
        return DataStorePreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): TranslateApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://stipacloud.ru")
            .build()
            .create(TranslateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTranslator(api: TranslateApi, dataStorePreferences: DataStorePreferences): TextTranslator {
        return TextTranslatorImpl(api, dataStorePreferences)
    }

    @Provides
    @Singleton
    fun provideScreenTranslator(
        textTranslator: TextTranslator,
        textScanner: TextScanner,
        screenshotUtil: ScreenshotUtil,
        context: Context
    ): ScreenTranslator {
        return ScreenTranslatorImpl(textTranslator, textScanner, screenshotUtil, context)
    }

    @Provides
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory {
        return factory
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatchersProvider {
        return DispatchersProviderImpl()
    }
}