package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.mvi.MviIntent

sealed class AuthIntent : MviIntent {
    data class SignInIntent(val username: String, val password: String) : AuthIntent()

    data class SignUpIntent(
        val username: String,
        val password: String,
        val inviteCode: String?
    ) : AuthIntent()
}