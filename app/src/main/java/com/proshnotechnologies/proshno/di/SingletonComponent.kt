package com.proshnotechnologies.proshno.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Component
import org.threeten.bp.Clock
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(FirebaseModule::class, AppModule::class))
interface SingletonComponent {
    fun firebaseAuth(): FirebaseAuth

    fun clock(): Clock
}