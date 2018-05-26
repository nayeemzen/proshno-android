package com.proshnotechnologies.proshno.home.repository

import com.google.firebase.auth.FirebaseAuth
import com.proshnotechnologies.proshno.auth.mvi.AuthResult
import com.proshnotechnologies.proshno.home.mvi.HomeResult
import io.reactivex.Observable
import org.threeten.bp.Clock
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class RealHomeRepository @Inject constructor(
    val auth: FirebaseAuth,
    val clock: Clock
) : HomeRepository {
    override fun signOut(): Observable<HomeResult> = Observable
        .fromCallable { auth.signOut() }
        .map { HomeResult.SignOutSuccess }

    override fun fetchHomeSummary(): Observable<HomeResult> = Observable
        .timer(3, SECONDS)
        .map { HomeResult.Success(0, clock.millis()) as HomeResult }
}