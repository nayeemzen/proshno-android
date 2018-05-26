package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.mvi.MviResult

sealed class HomeResult : MviResult {
    object SignOutSuccess : HomeResult()
    object InFlight : HomeResult()
    data class Success(val totalWinnings: Long, val nextGameAtMs: Long) : HomeResult()
    data class Failure(val error: Throwable?) : HomeResult()
}