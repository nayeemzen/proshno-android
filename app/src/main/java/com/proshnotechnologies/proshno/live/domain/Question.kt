package com.proshnotechnologies.proshno.live.domain

import org.threeten.bp.Duration

data class Question(
    val questionId: String,
    val question: String,
    val duration: Duration = Duration.ofSeconds(10),
    val choices: List<Choice>,
    val answer: Int?
)