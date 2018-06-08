package com.proshnotechnologies.proshno.utils.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Completable
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

fun Task<Void>.toCompletable(): Completable {
    return Completable.create { emitter ->
        addOnSuccessListener { emitter.onComplete() }
        addOnFailureListener { emitter.onError(it) }
    }
}

fun <T> Task<T>.toObservable(): Observable<T> {
    return Observable.create { emitter ->
        addOnSuccessListener { emitter.onNext(it) }
        addOnFailureListener { emitter.onError(it) }
        addOnCompleteListener { emitter.onComplete() }
    }
}
