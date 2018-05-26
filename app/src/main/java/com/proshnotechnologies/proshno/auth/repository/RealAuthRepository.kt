package com.proshnotechnologies.proshno.auth.repository

import com.google.firebase.auth.FirebaseAuth
import com.proshnotechnologies.proshno.auth.mvi.AuthResult
import com.proshnotechnologies.proshno.home.mvi.HomeResult
import io.reactivex.Observable
import javax.inject.Inject

class RealAuthRepository @Inject constructor(val auth: FirebaseAuth) : AuthRepository {
    override fun signIn(username: String, password: String): Observable<AuthResult> {
        return Observable.create<AuthResult> { emitter ->
            auth.signInWithEmailAndPassword(username, password)
                .addOnSuccessListener {
                    it.user.email?.let {
                        emitter.onNext(AuthResult.Success(it))
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
                .addOnCompleteListener {
                    emitter.onComplete()
                }
        }
    }

    override fun signUp(username: String, password: String,
        inviteCode: String?): Observable<AuthResult> {
        TODO("not implemented")
    }
}