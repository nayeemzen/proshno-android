package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.mvi.MviAction

sealed class AuthAction : MviAction {
    data class SignInAction(
        val username: String,
        val password: String
    ) : AuthAction()

    data class SignUpAction(
        val username: String,
        val password: String,
        val inviteCode: String?
    ) : AuthAction()
}