package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.domain.Answer
import com.proshnotechnologies.proshno.live.domain.Game
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.mvi.MviViewState

sealed class LiveGameViewState(val isFullScreen: Boolean) : MviViewState {
    data class Initial(private val fullScreenMode: Boolean) : LiveGameViewState(fullScreenMode)

    object ReceivedExpandScreen : LiveGameViewState(true)

    data class ConnectToGame(
        val inFlight: Boolean,
        val success: Boolean,
        val game: Game? = null,
        val error: Throwable?
    ) : LiveGameViewState(game?.currentQuestion == null)

    data class ChooseAnswer(
        val inFlight: Boolean,
        val success: Boolean,
        val choice: Int? = null,
        val error: Throwable?
    ) : LiveGameViewState(false)

    data class ReceivedQuestion(val question: Question) : LiveGameViewState(false)

    data class ReceivedAnswer(val answer: Answer) : LiveGameViewState(false)

    data class ReceivedStreamStats(
        private val _isFullScreen: Boolean,
        val numLiveViewers: Int
    ) : LiveGameViewState(_isFullScreen)
}