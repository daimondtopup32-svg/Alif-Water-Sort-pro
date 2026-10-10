package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey
    val username: String,
    val passwordHash: String,
    val currentLevel: Int = 1,
    val highestUnlockedLevel: Int = 1,
    val coins: Int = 100,
    val totalWins: Int = 0,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
