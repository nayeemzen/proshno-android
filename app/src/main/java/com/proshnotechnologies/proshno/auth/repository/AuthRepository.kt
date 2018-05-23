package com.proshnotechnologies.proshno.auth.repository

import com.proshnotechnologies.proshno.auth.mvi.AuthResult
import io.reactivex.Observable

interface AuthRepository {
    fun signIn(username: String, password: String) : Observable<AuthResult>
    fun signUp(username: String, password: String, inviteCode: String?) : Observable<AuthResult>
}