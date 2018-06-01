package com.proshnotechnologies.proshno.live.repository

import com.proshnotechnologies.proshno.live.domain.Choice
import com.proshnotechnologies.proshno.live.domain.Question
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult
import com.proshnotechnologies.proshno.live.mvi.LiveGameResult.ChooseAnswerSuccess
import io.reactivex.Observable
import org.threeten.bp.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class FakeLiveGameRepository @Inject constructor(): LiveGameRepository {
    private val question = Question(
        questionId = UUID.randomUUID().toString(),
        question = "Which company initially developed the Android Mobile OS?",
        duration = Duration.ofSeconds(5),
        answer = 2,
        choices = listOf(
            Choice(
                questionId = 123456789L,
                text = "Symbian Ltd",
                numAnswered = 10
            ),
            Choice(
                questionId = 123456789L,
                text = "Android Inc",
                numAnswered = 100
            ),
            Choice(
                questionId = 123456789L,
                text = "Google Inc",
                numAnswered = 190
            ))
    )

    private val fixtures = listOf(
        LiveGameResult.ReceivedQuestion(question.copy(
            answer = null,
            choices = question.choices.map { it.copy(numAnswered = null) }
        )),
        LiveGameResult.ReceivedAnswer(question),
        LiveGameResult.ReceivedExpandScreen
    )

    override fun chooseAnswer(questionId: String, choice: Int): Observable<LiveGameResult> {
        return Observable.just(ChooseAnswerSuccess(choice))
    }

    override fun connect(): Observable<LiveGameResult> {
        return Observable.fromIterable(fixtures)
            .concatMap { Observable.just(it).delay(question.duration.seconds, SECONDS) }
            .startWith(LiveGameResult.ConnectToGameSuccess)
    }
}