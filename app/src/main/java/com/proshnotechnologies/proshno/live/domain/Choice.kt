package com.proshnotechnologies.proshno.live.domain

data class Choice(
    val choiceId: Long,
    val questionId: Long,
    val text: String,
    val order: Int,
    val numSelected: Long?,
    val isCorrect: Boolean?
)