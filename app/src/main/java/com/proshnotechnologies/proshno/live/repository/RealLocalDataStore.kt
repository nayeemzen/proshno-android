package com.proshnotechnologies.proshno.live.repository

import android.content.SharedPreferences
import javax.inject.Inject

class RealLocalDataStore @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : LocalDataStore {
    override fun setChoice(questionId: String, choice: Int) {
        sharedPreferences.edit().putInt("$questionId.choice", choice).apply()
    }

    override fun getChoice(questionId: String): Int {
       return sharedPreferences.getInt("$questionId.choice", -1)
    }

    override fun setGameId(gameId: String) {
        sharedPreferences.edit().putString("currentGameId", gameId).apply()
    }

    override fun getGameId(): String? {
        return sharedPreferences.getString("currentGameId", null)
    }

    override fun isEliminated(): Boolean {
        return sharedPreferences.getBoolean("isEliminated", false)
    }

    override fun setIsEliminated(isEliminated: Boolean) {
        sharedPreferences.edit().putBoolean("isEliminated", isEliminated).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

}