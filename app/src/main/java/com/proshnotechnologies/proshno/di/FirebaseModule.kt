package com.proshnotechnologies.proshno.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestoreSettings.Builder

@Module
object FirebaseModule {
    @JvmStatic
    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @JvmStatic
    @Provides
    @Singleton
    fun firebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = Builder()
            .setPersistenceEnabled(false)
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        return firestore
    }
}