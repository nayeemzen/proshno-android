package com.proshnotechnologies.proshno.live.repository

import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import io.reactivex.Observable

interface LiveGameRepository {
    fun chooseAnswer(questionId: Long, choiceId: Long) : Observable<LiveGameResult>

    fun connect() : Observable<LiveGameResult>
}