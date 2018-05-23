package com.proshnotechnologies.proshno.auth.ui

import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.jakewharton.rxbinding2.view.clicks
import com.proshnotechnologies.proshno.R
import com.proshnotechnologies.proshno.auth.di.DaggerAuthComponent
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent.SignInIntent
import com.proshnotechnologies.proshno.auth.mvi.AuthViewModel
import com.proshnotechnologies.proshno.auth.mvi.AuthViewState
import com.proshnotechnologies.proshno.di.ControllerScope
import com.proshnotechnologies.proshno.mvi.MviView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.login.view.btn_login
import kotlinx.android.synthetic.main.login.view.et_password
import kotlinx.android.synthetic.main.login.view.et_username
import kotlinx.android.synthetic.main.login.view.tv_no_account
import javax.inject.Inject

class SignInController : Controller(), MviView<AuthIntent, AuthViewState> {
    @Inject lateinit var viewModel: AuthViewModel
    private lateinit var layout: View
    private val disposables = CompositeDisposable()

    override fun intents(): Observable<AuthIntent> = signInIntent()

    private fun bindIntents() {
        viewModel.states().subscribe(this::render).addTo(disposables)
        viewModel.processIntents(intents())
    }

    private fun signInIntent(): Observable<AuthIntent> = layout.btn_login.clicks().map {
        SignInIntent(
            username = layout.et_username.text.toString(),
            password = layout.et_password.text.toString())
    }

    override fun render(state: AuthViewState) {

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        layout = inflater.inflate(R.layout.login, container, false)
        layout.et_password.transformationMethod = PasswordTransformationMethod()
        layout.tv_no_account.setOnClickListener {
        }

        DaggerAuthComponent.builder().build().inject(this)
        bindIntents()
        return layout
    }

    override fun onDestroy() {
        disposables.dispose()
    }
}