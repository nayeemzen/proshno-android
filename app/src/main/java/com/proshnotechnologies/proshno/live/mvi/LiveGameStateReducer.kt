package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerFailure
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerInFlight
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedExpandScreen
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedQuestion
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedStreamStats
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ChooseAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameViewState.ConnectToGame
import com.proshnotechnologies.proshno.mvi.MviStateReducer
import javax.inject.Inject

class LiveGameStateReducer @Inject constructor(): MviStateReducer<LiveGameResult, LiveGameViewState> {
    override fun reduce(previousState: LiveGameViewState, result: LiveGameResult)
        : LiveGameViewState = when (result) {
        is ChooseAnswerInFlight -> ChooseAnswer(
            inFlight = true,
            success = false,
            error = null,
            choice = result.choice
        )

        is ChooseAnswerSuccess -> ChooseAnswer(
            inFlight = false,
            success = true,
            error = null,
            choice = result.choice
        )

        is ChooseAnswerFailure -> ChooseAnswer(
            inFlight = false,
            success = false,
            error = result.error,
            choice = result.choice
        )

        is LiveGameResult.ConnectToGameInFlight -> ConnectToGame(
            inFlight = true,
            success = false,
            error = null
        )

        is LiveGameResult.ConnectToGameSuccess -> ConnectToGame(
            inFlight = false,
            success = true,
            game = result.game,
            error = null
        )

        is LiveGameResult.ConnectToGameFailure -> ConnectToGame(
            inFlight = false,
            success = false,
            error = result.error
        )

        is ReceivedExpandScreen -> LiveGameViewState.ReceivedExpandScreen
        is ReceivedQuestion -> LiveGameViewState.ReceivedQuestion(result.question)
        is ReceivedAnswer -> LiveGameViewState.ReceivedAnswer(result.answer)
        is ReceivedStreamStats -> LiveGameViewState.ReceivedStreamStats(
            previousState.isFullScreen, result.numLiveViewers)
    }
}