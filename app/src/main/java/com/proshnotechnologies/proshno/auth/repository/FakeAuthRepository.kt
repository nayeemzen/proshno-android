package com.proshnotechnologies.proshno.auth.repository

import com.proshnotechnologies.proshno.auth.mvi.AuthResult
import io.reactivex.Observable
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class FakeAuthRepository @Inject constructor(): AuthRepository {
    override fun signIn(username: String, password: String): Observable<AuthResult> {
        return Observable.timer(3, SECONDS)
            .map { AuthResult.Success("Zen") as AuthResult }
    }

    override fun signUp(username: String, password: String, inviteCode: String?):
        Observable<AuthResult> {
        return Observable.just(AuthResult.Success("Zen") as AuthResult)
    }
}