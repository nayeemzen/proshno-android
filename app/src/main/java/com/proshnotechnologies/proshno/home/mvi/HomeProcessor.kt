package com.proshnotechnologies.proshno.home.mvi

import com.proshnotechnologies.proshno.home.mvi.HomeAction.LoadHomeSummaryAction
import com.proshnotechnologies.proshno.home.mvi.HomeAction.SignOutAction
import com.proshnotechnologies.proshno.home.repository.HomeRepository
import com.proshnotechnologies.proshno.mvi.MviProcessor
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class HomeProcessor @Inject constructor(private val homeRepository: HomeRepository)
    : MviProcessor<HomeAction, Observable<HomeResult>> {
    override fun process(action: HomeAction): Observable<HomeResult> =
        when (action) {
            is SignOutAction -> homeRepository
                .signOut()
                .startWith(HomeResult.InFlight)
                .onErrorReturn { HomeResult.Failure(it) }

            is LoadHomeSummaryAction -> homeRepository
                .fetchHomeSummary()
                .startWith(HomeResult.InFlight)
                .onErrorReturn { HomeResult.Failure(it) }
        }
}