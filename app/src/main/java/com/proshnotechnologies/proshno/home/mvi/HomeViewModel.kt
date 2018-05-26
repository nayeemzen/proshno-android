package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.home.mvi.HomeAction.LoadHomeSummaryAction
import com.proshnotechnologies.proshno.home.mvi.HomeAction.SignOutAction
import com.proshnotechnologies.proshno.home.mvi.HomeIntent.InitialIntent
import com.proshnotechnologies.proshno.home.mvi.HomeIntent.SignOutIntent
import com.proshnotechnologies.proshno.mvi.MviViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val processor: HomeProcessor,
    private val stateReducer: HomeStateReducer
) : MviViewModel<HomeIntent, HomeViewState> {
    private val intentsSubject = PublishSubject.create<HomeIntent>()
    private val statesObservable by lazy {
        intentsSubject
            .map(this::mapIntentToAction)
            .subscribeOn(Schedulers.io())
            .switchMap(processor::process)
            .scan(HomeViewState.Initial(), stateReducer::reduce)
    }

    override fun processIntents(intents: Observable<HomeIntent>) = intents.subscribe(intentsSubject)

    override fun states(): Observable<HomeViewState> = statesObservable

    private fun mapIntentToAction(homeIntent: HomeIntent) = when (homeIntent) {
        is InitialIntent -> {
            Timber.d("Mapping InitialIntent")
            LoadHomeSummaryAction
        }
        is SignOutIntent -> {
            SignOutAction
        }
    }
}