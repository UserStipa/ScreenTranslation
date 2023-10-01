package com.userstipa.screentranslation.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.userstipa.screentranslation.data.DataStorePreferences
import com.userstipa.screentranslation.data.DataStorePreferencesImpl
import com.userstipa.screentranslation.domain.text_scanner.TextScanner
import com.userstipa.screentranslation.domain.text_scanner.TextScannerImpl
import com.userstipa.screentranslation.domain.text_translate.TextTranslation
import com.userstipa.screentranslation.domain.text_translate.TextTranslationImpl
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.ui.uitls.notification_util.NotificationUtilImpl
import com.userstipa.screentranslation.ui.uitls.screenshot_util.ScreenshotUtil
import com.userstipa.screentranslation.ui.uitls.screenshot_util.ScreenshotUtilImpl
import com.userstipa.screentranslation.ui.uitls.virtualdisplay_util.VirtualDisplayUtil
import com.userstipa.screentranslation.ui.uitls.virtualdisplay_util.VirtualDisplayUtilImpl
import dagger.Module
import dagger.Provides
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
    fun provideScreenshotUtil(virtualDisplayUtil: VirtualDisplayUtil): ScreenshotUtil {
        return ScreenshotUtilImpl(virtualDisplayUtil)
    }

    @Provides
    @Singleton
    fun provideNotificationUtil(context: Context): NotificationUtil {
        return NotificationUtilImpl(context)
    }

    @Provides
    @Singleton
    fun provideTranslator(dataStorePreferences: DataStorePreferences): TextTranslation {
        return TextTranslationImpl(dataStorePreferences)
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
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory {
        return factory
    }
}