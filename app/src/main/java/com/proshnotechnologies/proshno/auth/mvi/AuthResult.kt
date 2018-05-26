package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.mvi.MviResult

sealed class AuthResult : MviResult {
    object InFlight : AuthResult()
    data class Success(val username: String) : AuthResult()
    data class Failure(val error: Throwable?) : AuthResult()
}