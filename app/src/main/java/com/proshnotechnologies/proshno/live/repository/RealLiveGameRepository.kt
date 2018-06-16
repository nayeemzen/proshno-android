package com.proshnotechnologies.proshno.live.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.proshnotechnologies.proshno.live.domain.Answer
import com.proshnotechnologies.proshno.live.domain.Game
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.live.domain.User
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.AddToParticipantsSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ConnectToGameSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.FetchGameSuccess
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedAnswer
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedQuestion
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ReceivedUserEliminated
import com.proshnotechnologies.proshno.utils.extensions.toCompletable
import com.proshnotechnologies.proshno.utils.extensions.toInstant
import com.proshnotechnologies.proshno.utils.extensions.toObservable
import io.reactivex.Completable
import io.reactivex.Observable
import org.threeten.bp.Duration
import timber.log.Timber
import javax.inject.Inject

class RealLiveGameRepository @Inject constructor(
    private val localDataStore: LocalDataStore,
    private val firestore: FirebaseFirestore,
    private val user: User
) : LiveGameRepository {
    override fun chooseAnswer(questionId: String, choice: Int)
        : Observable<LiveGameResult> {
        return firestore.collection("responses")
            .add(mapOf(
                "userId" to user.id,
                "gameId" to localDataStore.getGameId(),
                "questionId" to questionId,
                "choice" to choice,
                "createdAt" to FieldValue.serverTimestamp()
            ))
            .toObservable()
            .doOnNext { }
            .map { ChooseAnswerSuccess(choice) }
    }

    override fun connect(): Observable<LiveGameResult> {
        return latestLiveGame()
            .switchMap { addToParticipants(it.game) }
            .switchMap {
                Observable.merge(
                    Observable.just(ConnectToGameSuccess(it.game)),
                    participation(it.game.gameId),
                    questions(it.game.gameId),
                    answers(it.game.gameId)
                )
            }
    }

    private fun participation(gameId: String): Observable<LiveGameResult> {
        return firestore.document("games/$gameId/participants/${user.id}")
            .toObservable()
            .doOnError { Timber.e(it, "Error listening for participation.") }
            .doOnNext {
                Timber.i("Started participation listener.")
                localDataStore.setIsEliminated(it.getBoolean("isEliminated") ?: false)
            }
            .filter { it.getBoolean("isEliminated") ?: false }
            .map { ReceivedUserEliminated }
    }

    private fun addToParticipants(game: Game): Observable<AddToParticipantsSuccess> {
        return firestore.document("games/${game.gameId}/participants/${user.id}")
            .set(mapOf(
                "userId" to user.id,
                "gameId" to game.gameId,
                "joinedAt" to FieldValue.serverTimestamp(),
                "isEliminated" to false
            ))
            .toCompletable()
            .onErrorResumeNext {
                // Swallow permission denied errors when adding to participants and allow the
                // participants listener to handle elimination.
                if (it is FirebaseFirestoreException && it.code == PERMISSION_DENIED) {
                    Completable.complete()
                } else {
                    Completable.error(it)
                }
            }
            .doOnError { Timber.e(it, "Error adding user to participants.") }
            .doOnComplete { Timber.i("Added user to participants") }
            .andThen(Observable.just(AddToParticipantsSuccess(game)))
    }

    private fun latestLiveGame(): Observable<FetchGameSuccess> {
        return firestore.collection("games")
            .whereEqualTo("isLive", true)
            .orderBy("startsAt", DESCENDING)
            .limit(1)
            .get()
            .toObservable()
            .doOnError { Timber.e(it, "Error fetching latest game.") }
            .doOnNext {
                Timber.i("Fetching latest game.")
                localDataStore.setGameId(it.documents.single().id)
            }
            .map {
                val doc = it.documents.single()
                FetchGameSuccess(
                    game = Game(
                        gameId = doc.id,
                        startTime = doc.getTimestamp("startsAt")!!.toInstant(),
                        prize = doc.getLong("prize")!!,
                        isLive = doc.getBoolean("isLive")!!
                    )
                )
            }
    }

    private fun questions(gameId: String): Observable<LiveGameResult> {
        return firestore.collection("questions")
            .whereEqualTo("gameId", gameId)
            .whereEqualTo("enabled", true)
            .orderBy("order", DESCENDING)
            .limit(1)
            .toObservable()
            .doOnError { Timber.e(it, "Error starting questions listener.") }
            .doOnSubscribe { Timber.i("Started questions listener.") }
            .map {
                val doc = it.documents.single()
                ReceivedQuestion(Question(
                    gameId = "GAME_1",
                    id = doc.id,
                    text = doc.getString("text")!!,
                    duration = Duration.ofSeconds(doc.getLong("durationSecs")!!),
                    choices = (doc.get("choices") as List<Any?>).map { it as String }
                ))
            }
    }

    private fun answers(gameId: String): Observable<LiveGameResult> {
        return firestore.collection("answers")
            .whereEqualTo("gameId", gameId)
            .whereEqualTo("enabled", true)
            .orderBy("order", DESCENDING)
            .limit(1)
            .toObservable()
            .doOnError { Timber.e(it, "Error starting answers listener.") }
            .doOnSubscribe { Timber.i("Started answer listener.") }
            .map {
                val doc = it.documents.single()
                val questionId = doc.getString("questionId")!!
                val userChoice = localDataStore.getChoice(questionId)
                ReceivedAnswer(
                    answer = Answer(
                        questionId = questionId,
                        answer = doc.getLong("answer")!!.toInt(),
                        numResponses = (doc.get("numResponses") as List<Any?>).map { it as Int },
                        userChoice = userChoice
                    )
                )
            }
    }

}
