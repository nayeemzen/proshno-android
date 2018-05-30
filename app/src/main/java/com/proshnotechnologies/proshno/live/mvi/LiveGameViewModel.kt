package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.mvi.LiveGameAction.ChooseAnswerAction
import com.proshnotechnologies.proshno.live.mvi.LiveGameAction.ConnectToLiveGameAction
import com.proshnotechnologies.proshno.live.mvi.LiveGameIntent.ChooseAnswerIntent
import com.proshnotechnologies.proshno.live.mvi.LiveGameIntent.InitialIntent
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.Initial
import com.proshnotechnologies.proshno.mvi.MviViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class LiveGameViewModel @Inject constructor(
    private val stateReducer: LiveGameStateReducer,
    private val processor: LiveGameProcessor
) : MviViewModel<LiveGameIntent, LiveGameViewState> {
    private val intentsSubject = PublishSubject.create<LiveGameIntent>()
    private val statesObservable by lazy {
        intentsSubject
            .map(this::mapIntentToAction)
            .subscribeOn(Schedulers.io())
            .switchMap(processor::process)
            .scan(Initial(true), stateReducer::reduce)
    }

    override fun processIntents(intents: Observable<LiveGameIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<LiveGameViewState> = statesObservable

    private fun mapIntentToAction(intent: LiveGameIntent) : LiveGameAction {
        return when(intent) {
            is InitialIntent -> ConnectToLiveGameAction
            is ChooseAnswerIntent -> ChooseAnswerAction(intent.questionId, intent.choiceId)
        }
    }
}