package com.proshnotechnologies.proshno.live.repository

interface LocalDataStore {
    fun setChoice(questionId: String, choice: Int)

    fun getChoice(questionId: String): Int

    fun setGameId(gameId: String)

    fun getGameId(): String?

    fun isEliminated(): Boolean

    fun setIsEliminated(isEliminated: Boolean)

    fun clear()
}