package com.proshnotechnologies.proshno.utils.extensions

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Observable

fun Query.toObservable(): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        this.addSnapshotListener { snapshot, e ->
            if (e != null) {
                emitter.onError(e)
            }

            if (snapshot != null) {
                emitter.onNext(snapshot)
            }
        }
    }
}
