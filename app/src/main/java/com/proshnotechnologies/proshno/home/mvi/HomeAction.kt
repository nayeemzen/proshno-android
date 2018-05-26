package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.mvi.MviAction

sealed class HomeAction : MviAction {
    object LoadHomeSummaryAction : HomeAction()
    object SignOutAction : HomeAction()
}