package com.proshnotechnologies.proshno.utils.extensions

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Observable
import timber.log.Timber

fun Query.toObservable(): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        addSnapshotListener { snapshot, e ->
            if (e != null) {
                Timber.e(e)
                emitter.onError(e)
            }

            if (snapshot != null && !snapshot.isEmpty) {
                emitter.onNext(snapshot)
            }
        }
    }
}
