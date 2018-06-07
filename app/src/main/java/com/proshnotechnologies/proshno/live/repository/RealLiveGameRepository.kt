package com.proshnotechnologies.proshno.live.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.proshnotechnologies.proshno.live.domain.Answer
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedQuestion
import com.proshnotechnologies.proshno.utils.extensions.toObservable
import io.reactivex.Observable
import org.threeten.bp.Duration
import timber.log.Timber
import javax.inject.Inject

class RealLiveGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : LiveGameRepository {
    override fun chooseAnswer(questionId: String, choice: Int): Observable<LiveGameResult> {
        Timber.e("Not yet implemented!!")
        return Observable.empty()
    }

    override fun connect(): Observable<LiveGameResult> {
        return questions()
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