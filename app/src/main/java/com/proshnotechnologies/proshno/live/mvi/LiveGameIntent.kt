package com.proshnotechnologies.proshno.live.mvi

import com.proshnotechnologies.proshno.mvi.MviIntent

sealed class LiveGameIntent : MviIntent {
    object InitialIntent : LiveGameIntent()
    data class ChooseAnswerIntent(val questionId: String, val choice: Int) : LiveGameIntent()
}