package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.mvi.MviAction

sealed class LiveGameAction : MviAction {
    object ConnectToLiveGameAction : LiveGameAction()
    data class ChooseAnswerAction(val questionId: Long, val choiceId: Long) : LiveGameAction()
}
