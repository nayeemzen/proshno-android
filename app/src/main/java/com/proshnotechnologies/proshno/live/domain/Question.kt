package com.proshnotechnologies.proshno.live.domain

import org.threeten.bp.Duration

data class Question(
    val gameId: String,
    val id: String,
    val text: String,
    val duration: Duration = Duration.ofSeconds(10),
    val choices: List<String>
)