package com.proshnotechnologies.proshno.live.domain

import org.threeten.bp.Instant

data class Game(
    val gameId: String,
    val startTime: Instant,
    val prize: Long,
    val isLive: Boolean
)