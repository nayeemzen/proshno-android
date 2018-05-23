package com.proshnotechnologies.proshno.auth.mvi

import com.proshnotechnologies.proshno.auth.mvi.AuthAction.SignInAction
import com.proshnotechnologies.proshno.auth.mvi.AuthAction.SignUpAction
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent.SignInIntent
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent.SignUpIntent
import com.proshnotechnologies.proshno.mvi.MviViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authProcessor: AuthProcessor,
    val authStateReducer: AuthStateReducer) : MviViewModel<AuthIntent, AuthViewState> {

    private val intentsSubject = PublishSubject.create<AuthIntent>()
    private val statesObservable by lazy {
        intentsSubject
            .map(this::mapIntentToAction)
            .subscribeOn(Schedulers.io())
            .switchMap(authProcessor::process)
            .scan(AuthViewState.Initial(), authStateReducer::reduce)
    }

    override fun processIntents(intents: Observable<AuthIntent>) = intents.subscribe(intentsSubject)

    override fun states(): Observable<AuthViewState> = statesObservable

    private fun mapIntentToAction(intent: AuthIntent): AuthAction =
        when (intent) {
            is SignInIntent -> SignInAction(intent.username, intent.password)
            is SignUpIntent -> SignUpAction(intent.username, intent.password, intent.inviteCode)
        }
}