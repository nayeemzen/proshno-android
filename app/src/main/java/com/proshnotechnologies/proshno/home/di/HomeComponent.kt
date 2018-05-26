package com.proshnotechnologies.proshno.home.di

import com.proshnotechnologies.proshno.di.ControllerScope
import com.proshnotechnologies.proshno.di.SingletonComponent
import com.proshnotechnologies.proshno.home.ui.HomeController
import dagger.Component

@ControllerScope
@Component(dependencies = arrayOf(SingletonComponent::class), modules = arrayOf(HomeModule::class))
interface HomeComponent {
    fun inject(controller: HomeController)
}