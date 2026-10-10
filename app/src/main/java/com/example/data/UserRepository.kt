package com.example.data

import com.example.data.db.UserDao
import com.example.model.UserAccount
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao,
    private val preferences: GamePreferences
) {
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun register(username: String, password: String):Result<UserAccount> {
        val trimmed = username.trim()
        if (trimmed.length < 3) return Result.failure(Exception("Username must be at least 3 characters"))
        if (password.length < 4) return Result.failure(Exception("Password must be at least 4 characters"))

        val existing = userDao.getUserByUsername(trimmed)
        if (existing != null) return Result.failure(Exception("Username already exists"))

        val user = UserAccount(
            username = trimmed,
            passwordHash = hashPassword(password),
            currentLevel = 1,
            highestUnlockedLevel = 1,
            coins = 100,
            isGuest = false
        )
        userDao.insertUser(user)
        preferences.setCurrentUsername(trimmed)
        return Result.success(user)
    }

    suspend fun login(username: String, password: String): Result<UserAccount> {
        val trimmed = username.trim()
        val user = userDao.getUserByUsername(trimmed) ?: return Result.failure(Exception("User not found"))
        if (user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Incorrect password"))
        }
        preferences.setCurrentUsername(trimmed)
        return Result.success(user)
    }

    suspend fun loginAsGuest(): UserAccount {
        val guestName = "Guest_" + (1000..9999).random()
        val guest = UserAccount(
            username = guestName,
            passwordHash = "",
            currentLevel = 1,
            highestUnlockedLevel = 1,
            coins = 50,
            isGuest = true
        )
        userDao.insertUser(guest)
        preferences.setCurrentUsername(guestName)
        return guest
    }

    fun observeUser(username: String): Flow<UserAccount?> = userDao.observeUser(username)

    suspend fun updateLevel(username: String, level: Int) {
        userDao.updateLevel(username, level)
    }

    suspend fun addCoins(username: String, delta: Int) {
        userDao.addCoins(username, delta)
    }

    suspend fun logout() {
        preferences.setCurrentUsername(null)
    }
}
