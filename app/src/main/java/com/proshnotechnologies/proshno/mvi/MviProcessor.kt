package com.proshnotechnologies.proshno.mvi

import io.reactivex.Observable

interface MviProcessor<A : MviAction, R : Observable<out MviResult>> {
    fun process(action: A): R
}
