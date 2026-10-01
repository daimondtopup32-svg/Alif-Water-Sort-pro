package com.example.data.firebase

import android.util.Log
import com.example.model.UserAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class RealtimeUserScore(
    val userId: String = "",
    val username: String = "",
    val avatarEmoji: String = "🎮",
    val currentLevel: Int = 1,
    val maxUnlockedLevel: Int = 1,
    val coins: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

object RealtimeDatabaseManager {
    private const val TAG = "RealtimeDbManager"
    const val DATABASE_URL = "https://alif-water-sort-pro-default-rtdb.firebaseio.com"

    private const val USERS_PATH = "users"
    private const val LEADERBOARD_PATH = "leaderboard"
    private const val STATUS_PATH = "app_status"

    val database: FirebaseDatabase by lazy {
        try {
            val db = FirebaseDatabase.getInstance(DATABASE_URL)
            try {
                db.setPersistenceEnabled(true)
            } catch (e: Exception) {
                Log.w(TAG, "Persistence notice: ${e.message}")
            }
            db
        } catch (e: Exception) {
            Log.e(TAG, "Database init fallback: ${e.message}")
            FirebaseDatabase.getInstance()
        }
    }

    /**
     * Sends an immediate connection verification ping to Realtime Database.
     */
    fun pingConnection() {
        try {
            val pingRef = database.getReference(STATUS_PATH)
            val pingData = mapOf(
                "appName" to "Alif Water Sort Pro",
                "packageName" to "Alif.WaterSortpro",
                "status" to "Connected Successfully",
                "lastPing" to System.currentTimeMillis()
            )
            pingRef.setValue(pingData)
                .addOnSuccessListener {
                    Log.i(TAG, "Firebase Realtime Database Ping SUCCESSFUL!")
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Firebase Realtime Database Ping FAILED (Check Rules in Firebase Console): ${error.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Ping exception: ${e.message}")
        }
    }

    /**
     * Synchronizes the user account to Firebase Realtime Database.
     */
    fun syncUserToDatabase(user: UserAccount) {
        try {
            val userKey = "user_${user.id}"
            val scoreEntry = RealtimeUserScore(
                userId = userKey,
                username = user.username,
                avatarEmoji = user.avatarEmoji,
                currentLevel = user.currentLevel,
                maxUnlockedLevel = user.maxUnlockedLevel,
                coins = user.coins,
                timestamp = System.currentTimeMillis()
            )

            // Write to users node
            val usersRef = database.getReference(USERS_PATH).child(userKey)
            usersRef.setValue(scoreEntry)
                .addOnSuccessListener {
                    Log.i(TAG, "User $userKey synced to users path successfully")
                }
                .addOnFailureListener { error ->
                    Log.w(TAG, "Failed to sync user to Realtime Database: ${error.message}")
                }

            // Write to leaderboard node
            val leaderboardRef = database.getReference(LEADERBOARD_PATH).child(userKey)
            leaderboardRef.setValue(scoreEntry)
                .addOnSuccessListener {
                    Log.i(TAG, "User $userKey synced to leaderboard successfully")
                }
                .addOnFailureListener { error ->
                    Log.w(TAG, "Failed to sync user to leaderboard: ${error.message}")
                }

            // Keep status alive
            pingConnection()
        } catch (e: Exception) {
            Log.w(TAG, "Exception during Realtime Database sync: ${e.message}")
        }
    }

    /**
     * Realtime flow of top leaderboard players from Firebase Realtime Database.
     */
    fun getLeaderboardFlow(): Flow<List<RealtimeUserScore>> = callbackFlow {
        val query = try {
            database.getReference(LEADERBOARD_PATH)
                .orderByChild("maxUnlockedLevel")
                .limitToLast(50)
        } catch (e: Exception) {
            Log.w(TAG, "Could not initialize leaderboard query: ${e.message}")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<RealtimeUserScore>()
                for (child in snapshot.children) {
                    val entry = child.getValue(RealtimeUserScore::class.java)
                    if (entry != null) {
                        list.add(entry)
                    }
                }
                list.sortWith(compareByDescending<RealtimeUserScore> { it.maxUnlockedLevel }.thenByDescending { it.coins })
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Leaderboard listener cancelled: ${error.message}")
            }
        }

        query.addValueEventListener(listener)

        awaitClose {
            query.removeEventListener(listener)
        }
    }
}
