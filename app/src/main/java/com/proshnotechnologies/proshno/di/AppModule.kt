package com.proshnotechnologies.proshno.di

import android.app.Application
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {
    @Provides
    fun application(): Application {
        return application
    }

    @Provides
    fun systemDefaultClock() : Clock {
        return Clock.systemDefaultZone()
    }
}