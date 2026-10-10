package com.example.data.firebase

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

data class LeaderboardEntry(
    val username: String = "",
    val level: Int = 1,
    val score: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

class RealtimeDatabaseManager {
    private val database: FirebaseDatabase? = try {
        FirebaseDatabase.getInstance()
    } catch (e: Exception) {
        Log.e("FirebaseDB", "Firebase Realtime Database not initialized: ${e.message}")
        null
    }

    suspend fun syncScore(username: String, level: Int, score: Int) {
        if (database == null) return
        try {
            val entry = LeaderboardEntry(username = username, level = level, score = score)
            database.getReference("leaderboard").child(username).setValue(entry).await()
        } catch (e: Exception) {
            Log.e("FirebaseDB", "Failed to sync score: ${e.message}")
        }
    }
}
