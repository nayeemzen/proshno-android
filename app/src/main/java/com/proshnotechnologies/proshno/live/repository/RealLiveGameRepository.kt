package com.proshnotechnologies.proshno.live.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.proshnotechnologies.proshno.live.domain.Answer
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedQuestion
import com.proshnotechnologies.proshno.utils.extensions.toObservable
import io.reactivex.Observable
import org.threeten.bp.Duration
import timber.log.Timber
import javax.inject.Inject

class RealLiveGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : LiveGameRepository {
    override fun chooseAnswer(questionId: String, choice: Int): Observable<LiveGameResult> {
        val payload = mapOf(
            "userId" to auth.currentUser?.uid,
            "questionId" to questionId,
            "choice" to choice,
            "createdAt" to FieldValue.serverTimestamp()
        )

        return firestore.collection("responses")
            .add(payload)
            .toObservable()
            .map { ChooseAnswerSuccess(choice) }
    }

    override fun connect(): Observable<LiveGameResult> {
        return Observable.merge(questions(), answers())
    }

    private fun questions(): Observable<LiveGameResult> {
        return firestore.collection("questions")
            .whereEqualTo("enabled", true)
            .orderBy("order", DESCENDING)
            .limit(1)
            .toObservable()
            .map {
                val doc = it.documents.single()
                ReceivedQuestion(Question(
                    gameId = "GAME_1",
                    id = doc.id,
                    text = doc.getString("text")!!,
                    duration = Duration.ofSeconds(doc.getLong("durationSecs") ?: 10),
                    choices = (doc.get("choices") as List<Any?>).map { it as String }
                ))
            }
    }

    private fun answers(): Observable<LiveGameResult> {
        return firestore.collection("answers")
            .whereEqualTo("enabled", true)
            .orderBy("order", DESCENDING)
            .limit(1)
            .toObservable()
            .map {
                val doc = it.documents.single()
                ReceivedAnswer(
                    answer = Answer(
                        questionId = doc.getString("questionId")!!,
                        answer = doc.getLong("answer")!!.toInt()
                    )
                )
            }
    }
}