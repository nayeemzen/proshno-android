package com.proshnotechnologies.proshno.live.domain

data class Question(
    val questionId: Long,
    val question: String,
    val choices: List<Choice>
)