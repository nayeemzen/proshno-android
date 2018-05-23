package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.mvi.MviResult

sealed class AuthResult : MviResult {
    data class Success(val username: String) : AuthResult()
    data class Failure(val error: Throwable?) : AuthResult()
    object InFlight : AuthResult()
}