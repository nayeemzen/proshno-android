package com.proshnotechnologies.proshno.di

import com.proshnotechnologies.proshno.MainActivity
import com.proshnotechnologies.proshno.ProshnoApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface SingletonComponent {
    fun inject(application: ProshnoApp)

    fun inject(activity: MainActivity)
}