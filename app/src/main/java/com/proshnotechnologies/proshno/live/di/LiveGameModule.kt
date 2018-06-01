package com.proshnotechnologies.proshno.live.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.proshnotechnologies.proshno.live.repository.FakeLiveGameRepository
import com.proshnotechnologies.proshno.live.repository.LiveGameRepository
import dagger.Binds
import dagger.Module
import javax.inject.Named

@Module
abstract class LiveGameModule {
    @Binds
    abstract fun liveGameRepository(repository: FakeLiveGameRepository) : LiveGameRepository

    @Module
    companion object {
        const val LIVE_GAME_PREFERENCES: String = "LIVE_GAME_PREFERENCES"

        @JvmStatic
        @Named(LIVE_GAME_PREFERENCES)
        fun sharedPreferences(application: Application) : SharedPreferences {
            return application.getSharedPreferences(LIVE_GAME_PREFERENCES, Context.MODE_PRIVATE)
        }
    }
}