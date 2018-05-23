package com.proshnotechnologies.proshno.auth.di

import com.proshnotechnologies.proshno.auth.ui.SignInController
import com.proshnotechnologies.proshno.di.ControllerScope
import dagger.Component

@ControllerScope
@Component(modules = arrayOf(AuthModule::class))
interface AuthComponent {
    fun inject(controller: SignInController)
}