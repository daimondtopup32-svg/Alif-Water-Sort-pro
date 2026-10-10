package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccount?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun observeUser(username: String): Flow<UserAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("UPDATE users SET currentLevel = :level, highestUnlockedLevel = CASE WHEN highestUnlockedLevel < :level THEN :level ELSE highestUnlockedLevel END WHERE username = :username")
    suspend fun updateLevel(username: String, level: Int)

    @Query("UPDATE users SET coins = coins + :delta WHERE username = :username")
    suspend fun addCoins(username: String, delta: Int)
}
