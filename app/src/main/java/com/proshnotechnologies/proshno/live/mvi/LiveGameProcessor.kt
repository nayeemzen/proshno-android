package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.mvi.LiveGameAction.ChooseAnswerAction
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ConnectToGameFailure
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ConnectToGameInFlight
import com.proshnotechnologies.proshno.live.repository.LiveGameRepository
import com.proshnotechnologies.proshno.mvi.MviProcessor
import io.reactivex.Observable
import javax.inject.Inject

class LiveGameProcessor @Inject constructor(private val liveGameRepository: LiveGameRepository) :
    MviProcessor<LiveGameAction, Observable<LiveGameResult>> {
    override fun process(action: LiveGameAction): Observable<LiveGameResult> = when (action) {
        is LiveGameAction.ConnectToLiveGameAction -> liveGameRepository.connect()
            .startWith(ConnectToGameInFlight)
            .onErrorReturn { ConnectToGameFailure(it) }

        is ChooseAnswerAction -> liveGameRepository
            .chooseAnswer(action.questionId, action.choice)
            .startWith(LiveGameResult.ChooseAnswerInFlight(action.choice))
            .onErrorReturn { LiveGameResult.ChooseAnswerFailure(it, action.choice) }
    }
}