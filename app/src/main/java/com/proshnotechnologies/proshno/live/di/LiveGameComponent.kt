package com.proshnotechnologies.proshno.live.di

import com.proshnotechnologies.proshno.di.ControllerScope
import com.proshnotechnologies.proshno.di.SingletonComponent
import com.proshnotechnologies.proshno.live.ui.LiveGameController
import dagger.Component

@ControllerScope
@Component(
    dependencies = arrayOf(SingletonComponent::class),
    modules = arrayOf(LiveGameModule::class)
)
interface LiveGameComponent {
    fun inject(controller: LiveGameController)
}
