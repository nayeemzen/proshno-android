package com.proshnotechnologies.proshno.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.jakewharton.rxbinding2.view.clicks
import com.proshnotechnologies.proshno.MainActivity
import com.proshnotechnologies.proshno.R
import com.proshnotechnologies.proshno.auth.ui.SignInController
import com.proshnotechnologies.proshno.home.di.DaggerHomeComponent
import com.proshnotechnologies.proshno.home.mvi.HomeIntent
import com.proshnotechnologies.proshno.home.mvi.HomeViewModel
import com.proshnotechnologies.proshno.home.mvi.HomeViewState
import com.proshnotechnologies.proshno.mvi.MviView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.home.view.home_content_layout
import kotlinx.android.synthetic.main.home.view.img_loading
import kotlinx.android.synthetic.main.home.view.img_power_button
import kotlinx.android.synthetic.main.home.view.tv_error_msg
import javax.inject.Inject

class HomeController : Controller(), MviView<HomeIntent, HomeViewState> {
    @Inject lateinit var viewModel: HomeViewModel
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun intents(view: View): Observable<HomeIntent> =
        Observable.merge(initialIntent(), signOutIntent(view))

    override fun render(state: HomeViewState) {
        view?.let {
            it.img_loading.visibility = if (state.inFlight) View.VISIBLE else View.GONE
            it.home_content_layout.visibility = if (state.success) View.VISIBLE else View.INVISIBLE
            it.tv_error_msg.visibility = if (state.error != null) View.VISIBLE else View.GONE

            if (state.signOutSuccess) {
                router.setRoot(RouterTransaction.with(SignInController())
                    .pushChangeHandler(HorizontalChangeHandler())
                    .popChangeHandler(HorizontalChangeHandler()))
            }
        }
    }

    /**
     * @implNote: The order of calling states() and processIntents() matter because
     * the view-model publishSubject is essentially a hot observable which does not re-emit
     * items. So if the intent is processed before the rendering is registered, the view state
     * won't be reflected correctly.
     */
    private fun bindIntents(view: View) {
        viewModel.states()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::render)
            .addTo(disposables)
        viewModel.processIntents(intents(view))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val layout = inflater.inflate(R.layout.home, container, false)
        DaggerHomeComponent.builder()
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

    private fun initialIntent() : Observable<HomeIntent> {
        return Observable.just(HomeIntent.InitialIntent)
    }

    private fun signOutIntent(view: View): Observable<HomeIntent> =
        view.img_power_button.clicks().map { HomeIntent.SignOutIntent }
}