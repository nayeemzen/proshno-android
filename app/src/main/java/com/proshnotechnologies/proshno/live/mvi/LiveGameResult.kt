package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.mvi.MviResult

sealed class LiveGameResult : MviResult {
    data class ChooseAnswerInFlight(val choiceId: Long) : LiveGameResult()
    data class ChooseAnswerSuccess(val choiceId: Long) : LiveGameResult()
    data class ChooseAnswerFailure(val error: Throwable, val choiceId: Long) : LiveGameResult()

    object ConnectToGameInFlight : LiveGameResult()
    object ConnectToGameSuccess : LiveGameResult()
    data class ConnectToGameFailure(val error: Throwable): LiveGameResult()

    data class ReceivedQuestion(val question: Question) : LiveGameResult()
    data class ReceivedAnswer(val question: Question) : LiveGameResult()
    data class ReceivedStreamStats(val numLiveViewers: Int) : LiveGameResult()
}
