package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.home.mvi.HomeResult.Failure
import com.proshnotechnologies.proshno.home.mvi.HomeResult.InFlight
import com.proshnotechnologies.proshno.home.mvi.HomeResult.SignOutSuccess
import com.proshnotechnologies.proshno.home.mvi.HomeResult.Success
import com.proshnotechnologies.proshno.mvi.MviStateReducer
import timber.log.Timber
import javax.inject.Inject

class HomeStateReducer @Inject constructor() : MviStateReducer<HomeResult, HomeViewState> {
    override fun reduce(previousState: HomeViewState, result: HomeResult) = when (result) {
        is InFlight -> previousState.copy(inFlight = true)
        is Failure -> previousState.copy(inFlight = false, error = result.error)
        is SignOutSuccess -> previousState.copy(inFlight = false, signOutSuccess = true)
        is Success -> previousState.copy(
            inFlight = false,
            success = true,
            totalWinnings = result.totalWinnings,
            nextGameAtMs = result.nextGameAtMs
        )
    }
}