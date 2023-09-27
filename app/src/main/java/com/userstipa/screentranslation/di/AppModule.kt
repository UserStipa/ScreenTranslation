package com.userstipa.screentranslation.di

import android.content.Context
import com.userstipa.screentranslation.uitls.notification_util.NotificationUtil
import com.userstipa.screentranslation.uitls.notification_util.NotificationUtilImpl
import com.userstipa.screentranslation.uitls.screenshot_util.ScreenshotUtil
import com.userstipa.screentranslation.uitls.screenshot_util.ScreenshotUtilImpl
import com.userstipa.screentranslation.uitls.virtualdisplay_util.VirtualDisplayUtil
import com.userstipa.screentranslation.uitls.virtualdisplay_util.VirtualDisplayUtilImpl
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
}