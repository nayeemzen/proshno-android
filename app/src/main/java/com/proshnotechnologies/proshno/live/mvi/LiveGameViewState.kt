package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.mvi.MviViewState

sealed class LiveGameViewState(val isFullScreen: Boolean) : MviViewState {
    data class Initial(val fullScreenMode: Boolean) : LiveGameViewState(fullScreenMode)

    data class ChooseAnswer(
        val inFlight: Boolean,
        val success: Boolean,
        val choiceId: Long? = null,
        val error: Throwable?
    ) : LiveGameViewState(true)

    data class ReceivedQuestion(val question: Question) : LiveGameViewState(true)

    data class ReceivedAnswer(val question: Question) : LiveGameViewState(true)

    data class ReceivedStreamStats(
        private val _isFullScreen: Boolean,
        val numLiveViewers: Int
    ) : LiveGameViewState(_isFullScreen)
}