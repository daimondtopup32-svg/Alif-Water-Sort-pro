package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.model.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val authPrefs: SharedPreferences =
        context.getSharedPreferences("water_sort_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    companion object {
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
    }

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            val activeId = authPrefs.getLong(KEY_ACTIVE_USER_ID, -1L)
            if (activeId != -1L) {
                val user = userDao.getUserById(activeId)
                if (user != null) {
                    _currentUser.value = user
                    return@withContext
                }
            }
            // Auto login guest if existed or first time
            val guest = userDao.getGuestUser()
            if (guest != null) {
                _currentUser.value = guest
                authPrefs.edit().putLong(KEY_ACTIVE_USER_ID, guest.id).apply()
            }
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        avatarEmoji: String
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val trimmedUsername = username.trim()
        val trimmedEmail = email.trim()

        if (trimmedUsername.length < 3) {
            return@withContext Result.failure(IllegalArgumentException("Username must be at least 3 characters"))
        }
        if (password.length < 4) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        val existingUser = userDao.getUserByUsername(trimmedUsername)
        if (existingUser != null) {
            return@withContext Result.failure(IllegalArgumentException("Username already exists"))
        }

        if (trimmedEmail.isNotEmpty()) {
            val existingEmail = userDao.getUserByEmail(trimmedEmail)
            if (existingEmail != null) {
                return@withContext Result.failure(IllegalArgumentException("Email already in use"))
            }
        }

        val passwordHash = hashPassword(password)
        val newUser = UserAccount(
            username = trimmedUsername,
            email = trimmedEmail,
            passwordHash = passwordHash,
            avatarEmoji = avatarEmoji,
            coins = 20, // Registration bonus 20 coins
            currentLevel = 1,
            maxUnlockedLevel = 1,
            isGuest = false
        )

        val id = userDao.insertUser(newUser)
        val registeredUser = newUser.copy(id = id)

        authPrefs.edit().putLong(KEY_ACTIVE_USER_ID, id).apply()
        _currentUser.value = registeredUser
        Result.success(registeredUser)
    }

    suspend fun login(
        usernameOrEmail: String,
        password: String
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val query = usernameOrEmail.trim()
        if (query.isEmpty() || password.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Please fill all fields"))
        }

        val user = userDao.getUserByUsername(query) ?: userDao.getUserByEmail(query)
        if (user == null) {
            return@withContext Result.failure(IllegalArgumentException("User not found"))
        }

        val inputHash = hashPassword(password)
        if (user.passwordHash != inputHash) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password"))
        }

        authPrefs.edit().putLong(KEY_ACTIVE_USER_ID, user.id).apply()
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun playAsGuest(): UserAccount = withContext(Dispatchers.IO) {
        val existingGuest = userDao.getGuestUser()
        if (existingGuest != null) {
            authPrefs.edit().putLong(KEY_ACTIVE_USER_ID, existingGuest.id).apply()
            _currentUser.value = existingGuest
            return@withContext existingGuest
        }

        val guest = UserAccount(
            username = "Guest_${(1000..9999).random()}",
            email = "",
            passwordHash = "",
            avatarEmoji = "🎮",
            coins = 15,
            currentLevel = 1,
            maxUnlockedLevel = 1,
            isGuest = true
        )
        val id = userDao.insertUser(guest)
        val savedGuest = guest.copy(id = id)
        authPrefs.edit().putLong(KEY_ACTIVE_USER_ID, id).apply()
        _currentUser.value = savedGuest
        savedGuest
    }

    suspend fun updateAvatar(newEmoji: String) = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext
        val updated = user.copy(avatarEmoji = newEmoji)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun updateUserStats(coins: Int, currentLevel: Int, maxUnlockedLevel: Int) = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext
        val updated = user.copy(
            coins = coins,
            currentLevel = currentLevel,
            maxUnlockedLevel = maxOf(user.maxUnlockedLevel, maxUnlockedLevel)
        )
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    fun logout() {
        authPrefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
        _currentUser.value = null
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
