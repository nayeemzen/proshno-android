package com.proshnotechnologies.proshno.mvi

interface MviStateReducer<R : MviResult, S : MviViewState> {
    fun reduce(previousState: S, result: R) : S
}