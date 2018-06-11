package com.proshnotechnologies.proshno.live.domain

data class Answer(
    val questionId: String,
    val answer: Int,
    val userChoice: Int,
    val numResponses: List<Int>
) {
    fun isCorrect() = userChoice == answer
}