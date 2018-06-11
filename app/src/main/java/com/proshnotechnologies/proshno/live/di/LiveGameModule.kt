package com.proshnotechnologies.proshno.live.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.proshnotechnologies.proshno.live.domain.User
import com.proshnotechnologies.proshno.live.repository.LiveGameRepository
import com.proshnotechnologies.proshno.live.repository.LocalDataStore
import com.proshnotechnologies.proshno.live.repository.RealLiveGameRepository
import com.proshnotechnologies.proshno.live.repository.RealLocalDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class LiveGameModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun sharedPreferences(user: User, application: Application) =
            application.getSharedPreferences("${user.id}.LIVE_GAME_PREFERENCES", Context.MODE_PRIVATE)

        @JvmStatic
        @Provides
        fun currentUser(auth: FirebaseAuth) = User(auth.currentUser!!.uid)
    }

    @Binds
    abstract fun liveGameRepository(repository: RealLiveGameRepository) : LiveGameRepository

    @Binds
    abstract fun localDataStore(localDataStore: RealLocalDataStore) : LocalDataStore
}