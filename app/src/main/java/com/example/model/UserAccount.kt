package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String = "",
    val passwordHash: String = "",
    val avatarEmoji: String = "💧",
    val coins: Int = 15,
    val currentLevel: Int = 1,
    val maxUnlockedLevel: Int = 1,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
