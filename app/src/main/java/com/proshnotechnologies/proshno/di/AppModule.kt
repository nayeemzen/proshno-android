package com.proshnotechnologies.proshno.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(val application: Application) {
    @Provides
    @Singleton
    fun application(): Application {
        return application
    }

    @Provides
    @Singleton
    fun systemDefaultClock() : Clock {
        return Clock.systemDefaultZone()
    }
}