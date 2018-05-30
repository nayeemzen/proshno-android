package com.proshnotechnologies.proshno.mvi

import android.view.View
import io.reactivex.Observable

interface MviView<I : MviIntent, S : MviViewState> {
    fun intents(view: View): Observable<I>
    fun render(state: S)
}