package com.proshnotechnologies.proshno.utils.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.Instant
import timber.log.Timber

fun Query.toObservable(): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        val snapshotListener = addSnapshotListener { snapshot, e ->
            if (e != null) {
                emitter.onError(e)
            }

            if (snapshot != null && !snapshot.isEmpty) {
                emitter.onNext(snapshot)
            } else {
                Timber.w("Encountered null/empty query snapshot!")
            }
        }

        emitter.setCancellable { snapshotListener.remove() }
    }
}

fun <T> Task<T>.toCompletable(): Completable {
    return Completable.create { emitter ->
        addOnSuccessListener { emitter.onComplete() }
        addOnFailureListener { emitter.onError(it) }
    }
}

fun <T> Task<T>.toObservable(): Observable<T> {
    return Observable.create { emitter ->
        addOnSuccessListener {
            if (it != null) {
                emitter.onNext(it)
            } else {
                Timber.w("Encountered null/empty query snapshot!")
            }
        }
        addOnFailureListener { emitter.onError(it) }
        addOnCompleteListener { emitter.onComplete() }
    }
}

fun DocumentReference.toObservable(): Observable<DocumentSnapshot> {
    return Observable.create<DocumentSnapshot> { emitter ->
        val snapshotListener = addSnapshotListener { snapshot, e ->
            if (e != null) {
                emitter.onError(e)
            }

            if (snapshot != null && snapshot.exists()) {
                emitter.onNext(snapshot)
            } else {
                Timber.w("Encountered null/empty document snapshot!")
            }
        }

        emitter.setCancellable { snapshotListener.remove() }
    }
}

fun Timestamp.toInstant(): Instant {
    return Instant.ofEpochMilli(toDate().time)
}
