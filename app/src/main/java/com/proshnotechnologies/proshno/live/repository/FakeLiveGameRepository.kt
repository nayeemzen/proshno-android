package com.proshnotechnologies.proshno.live.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proshnotechnologies.proshno.live.domain.Answer
import com.proshnotechnologies.proshno.live.domain.Game
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import io.reactivex.Observable
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class FakeLiveGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): LiveGameRepository {
    private val gameId = UUID.randomUUID().toString()
    private var userAnswer: Int? = null

    private val question = Question(
        gameId = gameId,
        id = UUID.randomUUID().toString(),
        text = "Which company initially developed the Android Mobile OS?",
        duration = Duration.ofSeconds(5),
        choices = listOf("Symbian Ltd", "Android Inc", "Google Inc")
    )

    private val fixtures = listOf(
        {
            LiveGameResult.ConnectToGameSuccess(Game(
                gameId = gameId,
                startTime = Instant.now(),
                prize = 10000,
                isLive = true,
                isUserEliminated = false,
                currentQuestion = null,
                streamUrl = "rtmp://10.88.111.6/live/test"
            ))
        },
        { LiveGameResult.ReceivedQuestion(question) },
        { LiveGameResult.ReceivedAnswer(Answer(questionId = question.id, answer = 1)) },
        { LiveGameResult.ReceivedExpandScreen }
    )

    override fun chooseAnswer(questionId: String, choice: Int): Observable<LiveGameResult> {
        userAnswer = choice
        return Observable.just(ChooseAnswerSuccess(choice))
    }

    override fun connect(): Observable<LiveGameResult> {
        return Observable.fromIterable(fixtures)
            .concatMap { Observable.just(it).delay(question.duration.seconds, SECONDS) }
            .map { it() }
            .startWith(LiveGameResult.ConnectToGameInFlight)
    }
}