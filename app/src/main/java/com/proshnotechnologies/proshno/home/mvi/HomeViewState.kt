package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.mvi.MviViewState

data class HomeViewState (
    val inFlight : Boolean,
    val success : Boolean,
    val error : Throwable?,
    val totalWinnings : Long,
    val nextGameAtMs : Long) : MviViewState  {
    companion object {
        fun Initial() = HomeViewState(
            inFlight = false,
            success = false,
            error = null,
            totalWinnings = 0,
            nextGameAtMs = 0
        )
    }
}
