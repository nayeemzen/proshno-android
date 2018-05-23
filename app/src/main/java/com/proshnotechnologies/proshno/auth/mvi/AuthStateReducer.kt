package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.auth.mvi.AuthResult.Failure
import com.proshnotechnologies.proshno.auth.mvi.AuthResult.InFlight
import com.proshnotechnologies.proshno.auth.mvi.AuthResult.Success
import com.proshnotechnologies.proshno.mvi.MviStateReducer
import javax.inject.Inject

class AuthStateReducer @Inject constructor() : MviStateReducer<AuthResult, AuthViewState> {
    override fun reduce(previousState: AuthViewState, result: AuthResult): AuthViewState =
        when (result) {
            is InFlight -> previousState.copy(inFlight = true, error = null)
            is Failure -> previousState.copy(inFlight = false, error = result.error)
            is Success -> previousState.copy(
                inFlight = false,
                success = true,
                error = null,
                username = result.username
            )
        }
}