package com.proshnotechnologies.proshno.auth.ui

import android.app.ProgressDialog
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.jakewharton.rxbinding2.view.clicks
import com.proshnotechnologies.proshno.MainActivity
import com.proshnotechnologies.proshno.R
import com.proshnotechnologies.proshno.auth.di.DaggerAuthComponent
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent
import com.proshnotechnologies.proshno.auth.mvi.AuthIntent.SignInIntent
import com.proshnotechnologies.proshno.auth.mvi.AuthViewModel
import com.proshnotechnologies.proshno.auth.mvi.AuthViewState
import com.proshnotechnologies.proshno.home.ui.HomeController
import com.proshnotechnologies.proshno.mvi.MviView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.login.view.btn_login
import kotlinx.android.synthetic.main.login.view.et_password
import kotlinx.android.synthetic.main.login.view.et_username
import kotlinx.android.synthetic.main.login.view.tv_no_account
import javax.inject.Inject

class SignInController : Controller(), MviView<AuthIntent, AuthViewState> {
    @Inject lateinit var viewModel: AuthViewModel
    private val disposables = CompositeDisposable()
    private var progress : ProgressDialog? = null

    override fun intents(view: View): Observable<AuthIntent> = signInIntent(view)

    private fun bindIntents(view: View) {
        viewModel.processIntents(intents(view))
        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::render)
            .addTo(disposables)
    }

    private fun signInIntent(view: View): Observable<AuthIntent> = view.btn_login.clicks().map {
        SignInIntent(
            username = view.et_username.text.toString(),
            password = view.et_password.text.toString())
    }

    override fun render(state: AuthViewState) {
        if (state.inFlight) {
            progress = ProgressDialog(activity)
            progress?.setTitle("Signing In")
            progress?.setMessage("Please wait...")
            progress?.setCancelable(false)
            progress?.show()
        }

        if (state.success) {
            progress?.dismiss()
            router.setRoot(RouterTransaction.with(HomeController())
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
        }

        if (state.error != null) {
            progress?.dismiss()
            Toast.makeText(activity, "Error signing in: ${state.error.message}",
                Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val layout = inflater.inflate(R.layout.login, container, false)
        layout.et_password.transformationMethod = PasswordTransformationMethod()
        layout.tv_no_account.setOnClickListener {}
        DaggerAuthComponent.builder()
            .singletonComponent((activity as MainActivity).singletonComponent())
            .build()
            .inject(this)
        return layout
    }

    override fun onAttach(view: View) {
        bindIntents(view)
    }

    override fun onDetach(view: View) {
        disposables.clear()
    }

    override fun onDestroy() {
        disposables.dispose()
    }
}