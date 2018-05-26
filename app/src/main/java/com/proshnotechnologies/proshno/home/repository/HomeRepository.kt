package com.proshnotechnologies.proshno.home.repository

import com.proshnotechnologies.proshno.home.mvi.HomeResult
import io.reactivex.Observable

interface HomeRepository {
    fun signOut(): Observable<HomeResult>
    fun fetchHomeSummary() : Observable<HomeResult>
}