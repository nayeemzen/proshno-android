package com.proshnotechnologies.proshno.live.repository

import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ConnectToGameSuccess
import io.reactivex.Observable
import javax.inject.Inject

class FakeLiveGameRepository @Inject constructor(): LiveGameRepository {
    override fun chooseAnswer(questionId: Long, choiceId: Long): Observable<LiveGameResult> {
        return Observable.just(ChooseAnswerSuccess(choiceId))
    }

    override fun connect(): Observable<LiveGameResult> {
        return Observable.just(ConnectToGameSuccess)
    }
}