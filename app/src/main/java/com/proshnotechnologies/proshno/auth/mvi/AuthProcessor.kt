package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.auth.repository.AuthRepository
import com.proshnotechnologies.proshno.auth.mvi.AuthAction.SignInAction
import com.proshnotechnologies.proshno.auth.mvi.AuthAction.SignUpAction
import com.proshnotechnologies.proshno.mvi.MviProcessor
import io.reactivex.Observable
import javax.inject.Inject

class AuthProcessor @Inject constructor(val authRepository: AuthRepository)
    : MviProcessor<AuthAction, Observable<AuthResult>> {
    override fun process(action: AuthAction): Observable<AuthResult> {
        return when (action) {
            is SignInAction -> authRepository.signIn(action.username, action.password)
            is SignUpAction -> authRepository.signUp(action.username, action.password,
                action.inviteCode)
        }
    }
}
