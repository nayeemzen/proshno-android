package com.proshnotechnologies.proshno.live.di

import com.proshnotechnologies.proshno.live.repository.FakeLiveGameRepository
import com.proshnotechnologies.proshno.live.repository.LiveGameRepository
import dagger.Binds
import dagger.Module

@Module
abstract class LiveGameModule {
    @Binds
    abstract fun liveGameRepository(repository: FakeLiveGameRepository) : LiveGameRepository
}