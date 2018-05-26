package com.proshnotechnologies.proshno.home.di

import com.proshnotechnologies.proshno.home.repository.RealHomeRepository
import com.proshnotechnologies.proshno.home.repository.HomeRepository
import dagger.Binds
import dagger.Module

@Module
abstract class HomeModule {
    @Binds
    abstract fun homeRepository(homeRepository: RealHomeRepository): HomeRepository
}