package com.proshnotechnologies.proshno.auth.di

import com.proshnotechnologies.proshno.auth.ui.SignInController
import com.proshnotechnologies.proshno.di.ControllerScope
import com.proshnotechnologies.proshno.di.SingletonComponent
import dagger.Component

@ControllerScope
@Component(dependencies = arrayOf(SingletonComponent::class), modules = arrayOf(AuthModule::class))
interface AuthComponent {
    fun inject(controller: SignInController)
}