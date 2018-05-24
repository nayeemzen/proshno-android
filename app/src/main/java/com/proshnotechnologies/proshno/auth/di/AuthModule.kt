package com.proshnotechnologies.proshno.auth.di

import com.proshnotechnologies.proshno.auth.repository.AuthRepository
import com.proshnotechnologies.proshno.auth.repository.FakeAuthRepository
import com.proshnotechnologies.proshno.auth.repository.RealAuthRepository
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {
    @Binds
    abstract fun authRepository(authRepository: RealAuthRepository): AuthRepository
}