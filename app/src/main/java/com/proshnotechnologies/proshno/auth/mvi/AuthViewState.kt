package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.mvi.MviViewState

data class AuthViewState(
    val inFlight : Boolean,
    val success : Boolean,
    val error : Throwable?,
    val username : String
) : MviViewState {
    companion object {
        fun Initial() = AuthViewState(false, false, null, "")
    }
}