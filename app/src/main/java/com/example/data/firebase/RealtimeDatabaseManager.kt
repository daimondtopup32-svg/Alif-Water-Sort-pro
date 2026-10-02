package com.example.data.firebase

import android.util.Log
import com.example.model.UserAccount
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

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

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

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
     * Sends an immediate connection verification ping to Realtime Database
     * using both Firebase SDK and Direct HTTPS REST API.
     */
    fun pingConnection() {
        // 1. Firebase SDK Ping
        try {
            val pingRef = database.getReference(STATUS_PATH)
            val pingData = mapOf(
                "appName" to "Alif Water Sort Pro",
                "packageName" to "alif.WaterSortpro",
                "status" to "Connected Successfully",
                "lastPing" to System.currentTimeMillis()
            )
            pingRef.setValue(pingData)
                .addOnSuccessListener {
                    Log.i(TAG, "Firebase SDK Ping SUCCESSFUL!")
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Firebase SDK Ping FAILED: ${error.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Ping SDK exception: ${e.message}")
        }

        // 2. Direct HTTPS REST Ping (guarantees write over raw HTTP)
        ioScope.launch {
            try {
                val json = """
                    {
                      "appName": "Alif Water Sort Pro",
                      "packageName": "alif.WaterSortpro",
                      "status": "Connected Successfully (REST)",
                      "lastPing": ${System.currentTimeMillis()}
                    }
                """.trimIndent()
                val request = Request.Builder()
                    .url("$DATABASE_URL/$STATUS_PATH.json")
                    .put(json.toRequestBody("application/json".toMediaType()))
                    .build()
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.i(TAG, "Direct REST Ping SUCCESSFUL: HTTP ${response.code}")
                    } else {
                        Log.e(TAG, "Direct REST Ping FAILED: HTTP ${response.code} (Check Firebase Database Rules!)")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Direct REST Ping Network Exception: ${e.message}")
            }
        }
    }

    /**
     * Synchronizes the user account to Firebase Realtime Database
     * using BOTH Firebase Native SDK and Direct REST API.
     */
    fun syncUserToDatabase(user: UserAccount) {
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

        // 1. Firebase Native SDK Sync
        try {
            val usersRef = database.getReference(USERS_PATH).child(userKey)
            usersRef.setValue(scoreEntry)
                .addOnSuccessListener {
                    Log.i(TAG, "SDK User $userKey synced to users path successfully")
                }
                .addOnFailureListener { error ->
                    Log.w(TAG, "SDK Failed to sync user: ${error.message}")
                }

            val leaderboardRef = database.getReference(LEADERBOARD_PATH).child(userKey)
            leaderboardRef.setValue(scoreEntry)
                .addOnSuccessListener {
                    Log.i(TAG, "SDK User $userKey synced to leaderboard successfully")
                }
                .addOnFailureListener { error ->
                    Log.w(TAG, "SDK Failed to sync leaderboard: ${error.message}")
                }
        } catch (e: Exception) {
            Log.w(TAG, "SDK Exception during sync: ${e.message}")
        }

        // 2. Direct HTTPS REST API Sync (Instant fallback that bypasses Play Services and guarantees immediate commit)
        ioScope.launch {
            try {
                val userJson = """
                    {
                      "userId": "$userKey",
                      "username": "${user.username.replace("\"", "\\\"")}",
                      "avatarEmoji": "${user.avatarEmoji}",
                      "currentLevel": ${user.currentLevel},
                      "maxUnlockedLevel": ${user.maxUnlockedLevel},
                      "coins": ${user.coins},
                      "timestamp": ${System.currentTimeMillis()}
                    }
                """.trimIndent()

                val body = userJson.toRequestBody("application/json".toMediaType())

                // PUT /users/user_X.json
                val userReq = Request.Builder()
                    .url("$DATABASE_URL/$USERS_PATH/$userKey.json")
                    .put(body)
                    .build()
                httpClient.newCall(userReq).execute().use { resp ->
                    if (resp.isSuccessful) {
                        Log.i(TAG, "Direct REST sync for $userKey SUCCESSFUL: HTTP ${resp.code}")
                    } else {
                        val errorBody = resp.body?.string()
                        Log.e(TAG, "Direct REST sync FAILED: HTTP ${resp.code} - $errorBody (Rules might be locked!)")
                    }
                }

                // PUT /leaderboard/user_X.json
                val lbReq = Request.Builder()
                    .url("$DATABASE_URL/$LEADERBOARD_PATH/$userKey.json")
                    .put(userJson.toRequestBody("application/json".toMediaType()))
                    .build()
                httpClient.newCall(lbReq).execute().close()

            } catch (e: Exception) {
                Log.e(TAG, "Direct REST user sync network error: ${e.message}")
            }
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
