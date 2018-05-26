package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.mvi.MviIntent

sealed class HomeIntent : MviIntent {
    object InitialIntent : HomeIntent()
    object SignOutIntent : HomeIntent()
}