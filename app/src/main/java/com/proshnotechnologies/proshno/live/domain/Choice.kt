package com.proshnotechnologies.proshno.live.domain

data class Choice(
    val questionId: Long,
    val text: String,
    val numAnswered: Long? = null
)