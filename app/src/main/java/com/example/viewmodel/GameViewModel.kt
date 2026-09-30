package com.example.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.audio.SoundManager
import com.example.data.GamePreferences
import com.example.data.UserRepository
import com.example.logic.GameSolver
import com.example.model.BackgroundTheme
import com.example.model.GameLevel
import com.example.model.LevelGenerator
import com.example.model.LiquidColor
import com.example.model.Tube
import com.example.model.TubeSkin
import com.example.model.UserAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val currentLevel: Int = 1,
    val maxUnlockedLevel: Int = 1,
    val tubes: List<Tube> = emptyList(),
    val selectedTubeIndex: Int? = null,
    val isPouring: Boolean = false,
    val pouringSourceIndex: Int? = null,
    val pouringTargetIndex: Int? = null,
    val pouringColor: LiquidColor? = null,
    val pouringUnits: Int = 0,
    val movesCount: Int = 0,
    val canUndo: Boolean = false,
    val isWon: Boolean = false,
    val showVictoryDialog: Boolean = false,
    val starsEarned: Int = 0,
    val coins: Int = 100,
    val coinsEarnedThisLevel: Int = 0,
    val extraTubesCount: Int = 0,
    val hintSourceIndex: Int? = null,
    val hintTargetIndex: Int? = null,
    val isStuck: Boolean = false,
    val shakingTubeIndex: Int? = null,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 0.22f,
    val vibrationEnabled: Boolean = true,
    val isBengali: Boolean = true,
    val tubeSkin: TubeSkin = TubeSkin.CLASSIC,
    val bgTheme: BackgroundTheme = BackgroundTheme.AURORA_NIGHT,
    val unlockedSkins: Set<String> = setOf(TubeSkin.CLASSIC.name),
    val unlockedBgThemes: Set<String> = setOf(BackgroundTheme.AURORA_NIGHT.name),
    val shopMessage: String? = null,
    val adsWatchedForTube: Int = 0,
    val showWatchAdDialog: Boolean = false,
    val isAdLoading: Boolean = false,
    val adErrorMessage: String? = null,
    val currentUser: UserAccount? = null,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val soundManager = SoundManager(application)
    val preferences = GamePreferences(application)
    val userRepository = UserRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val historyStack = mutableListOf<List<Tube>>()
    private var initialLevelTubes = listOf<Tube>()

    init {
        // Synchronize preferences
        soundManager.isSoundEnabled = preferences.soundEnabled.value
        soundManager.masterVolume = preferences.soundVolume.value
        soundManager.isVibrationEnabled = preferences.vibrationEnabled.value

        _uiState.update {
            it.copy(
                currentLevel = preferences.currentLevel.value,
                maxUnlockedLevel = preferences.maxUnlockedLevel.value,
                coins = preferences.coins.value,
                soundEnabled = preferences.soundEnabled.value,
                soundVolume = preferences.soundVolume.value,
                vibrationEnabled = preferences.vibrationEnabled.value,
                isBengali = preferences.isBengali.value,
                tubeSkin = preferences.tubeSkin.value,
                bgTheme = preferences.bgTheme.value,
                unlockedSkins = preferences.unlockedSkins.value,
                unlockedBgThemes = preferences.unlockedBgThemes.value
            )
        }

        loadLevel(preferences.currentLevel.value)

        viewModelScope.launch {
            userRepository.initialize()
            userRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun loadLevel(levelNumber: Int) {
        val level = LevelGenerator.getLevel(levelNumber)
        initialLevelTubes = level.tubes
        historyStack.clear()

        _uiState.update {
            it.copy(
                currentLevel = levelNumber,
                tubes = level.tubes,
                selectedTubeIndex = null,
                isPouring = false,
                pouringSourceIndex = null,
                pouringTargetIndex = null,
                pouringColor = null,
                pouringUnits = 0,
                movesCount = 0,
                canUndo = false,
                isWon = false,
                showVictoryDialog = false,
                starsEarned = 0,
                coinsEarnedThisLevel = 0,
                extraTubesCount = 0,
                hintSourceIndex = null,
                hintTargetIndex = null,
                isStuck = false,
                shakingTubeIndex = null
            )
        }
        preferences.setCurrentLevel(levelNumber)
    }

    private var pendingTubeClick: Int? = null

    fun onTubeClicked(index: Int) {
        val state = _uiState.value
        if (state.isWon) return

        // If currently pouring, queue the tapped tube so it immediately executes when the pour finishes!
        if (state.isPouring) {
            pendingTubeClick = index
            return
        }

        val selected = state.selectedTubeIndex
        if (selected == null) {
            // Select source tube
            val tube = state.tubes.getOrNull(index) ?: return
            if (tube.isEmpty) {
                // Immediate shake and sound feedback so the user knows touch registered
                soundManager.playError()
                viewModelScope.launch {
                    _uiState.update { it.copy(shakingTubeIndex = index) }
                    delay(250)
                    _uiState.update { it.copy(shakingTubeIndex = null) }
                }
                return
            }
            if (tube.isCompleted) {
                // Already completed tube: friendly feedback
                soundManager.playTap()
                viewModelScope.launch {
                    _uiState.update { it.copy(shakingTubeIndex = index) }
                    delay(250)
                    _uiState.update { it.copy(shakingTubeIndex = null) }
                }
                return
            }

            soundManager.playTap()
            _uiState.update {
                it.copy(
                    selectedTubeIndex = index,
                    hintSourceIndex = null,
                    hintTargetIndex = null
                )
            }
        } else {
            if (selected == index) {
                // Deselect current tube
                soundManager.playTap()
                _uiState.update { it.copy(selectedTubeIndex = null) }
            } else {
                val src = state.tubes.getOrNull(selected) ?: return
                val dst = state.tubes.getOrNull(index) ?: return
                val topColor = src.topColor ?: return

                if (dst.canAccept(topColor)) {
                    // Valid pour move!
                    attemptPour(selected, index)
                } else {
                    // Cannot pour into this target tube.
                    // If target tube has liquid and isn't completed, switch selection to it!
                    if (!dst.isEmpty && !dst.isCompleted) {
                        soundManager.playTap()
                        _uiState.update {
                            it.copy(
                                selectedTubeIndex = index,
                                hintSourceIndex = null,
                                hintTargetIndex = null
                            )
                        }
                    } else {
                        // Shake feedback on invalid empty or completed target
                        soundManager.playError()
                        viewModelScope.launch {
                            _uiState.update { it.copy(shakingTubeIndex = index) }
                            delay(300)
                            _uiState.update { it.copy(shakingTubeIndex = null) }
                        }
                    }
                }
            }
        }
    }

    private fun attemptPour(srcIdx: Int, dstIdx: Int) {
        val state = _uiState.value
        val src = state.tubes.getOrNull(srcIdx) ?: return
        val dst = state.tubes.getOrNull(dstIdx) ?: return
        val topColor = src.topColor ?: return

        if (!dst.canAccept(topColor)) {
            soundManager.playError()
            viewModelScope.launch {
                _uiState.update { it.copy(shakingTubeIndex = dstIdx) }
                delay(300)
                _uiState.update { it.copy(shakingTubeIndex = null) }
            }
            return
        }

        // Valid move!
        val unitsToMove = src.topColorCount.coerceAtMost(dst.freeSpace)
        if (unitsToMove <= 0) return

        // Save history for Undo
        historyStack.add(state.tubes.map { it.copy() })

        // Start pouring animation
        soundManager.playPour()
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isPouring = true,
                        pouringSourceIndex = srcIdx,
                        pouringTargetIndex = dstIdx,
                        pouringColor = topColor,
                        pouringUnits = unitsToMove,
                        canUndo = true,
                        movesCount = it.movesCount + 1
                    )
                }

                // Pour animation time (850ms: fast, fluid, and responsive)
                delay(850)

                // Complete the transfer in the model
                val (newSrc, popped) = src.popTop(unitsToMove)
                val newDst = dst.push(popped)

                val updatedTubes = _uiState.value.tubes.toMutableList().apply {
                    this[srcIdx] = newSrc
                    this[dstIdx] = newDst
                }

                // Check if this tube was just completed
                if (newDst.isCompleted) {
                    soundManager.playCompleteTube()
                }

                // Check win
                val won = GameSolver.isWon(updatedTubes)

                _uiState.update {
                    it.copy(
                        tubes = updatedTubes,
                        selectedTubeIndex = null,
                        isPouring = false,
                        pouringSourceIndex = null,
                        pouringTargetIndex = null,
                        pouringColor = null,
                        pouringUnits = 0,
                        isWon = won
                    )
                }

                if (won) {
                    handleVictory()
                } else {
                    // Check if stuck (no valid moves)
                    val hasMoves = GameSolver.hasValidMoves(updatedTubes)
                    _uiState.update { it.copy(isStuck = !hasMoves) }

                    // Process queued click if user tapped during the animation!
                    val queued = pendingTubeClick
                    pendingTubeClick = null
                    if (queued != null) {
                        onTubeClicked(queued)
                    }
                }
            } catch (e: Exception) {
                // Ensure state is cleanly reset if anything goes wrong
                _uiState.update {
                    it.copy(
                        isPouring = false,
                        pouringSourceIndex = null,
                        pouringTargetIndex = null,
                        selectedTubeIndex = null
                    )
                }
            } finally {
                // Guarantee isPouring never remains true
                if (_uiState.value.isPouring) {
                    _uiState.update {
                        it.copy(
                            isPouring = false,
                            pouringSourceIndex = null,
                            pouringTargetIndex = null
                        )
                    }
                }
            }
        }
    }

    private fun handleVictory() {
        soundManager.playLevelWin()
        val moves = _uiState.value.movesCount
        val levelNum = _uiState.value.currentLevel

        // Star rating: 3 stars for fewest moves, 2 stars for good moves, 1 star for completion
        val stars = when {
            moves <= 8 + (levelNum * 2) -> 3
            moves <= 14 + (levelNum * 2) -> 2
            else -> 1
        }
        // Exactly 1 to 3 coins per level completion as requested
        val rewardCoins = stars.coerceIn(1, 3)

        preferences.unlockNextLevel(levelNum)
        preferences.setLevelStars(levelNum, stars)
        preferences.addCoins(rewardCoins)
        syncUserStats()

        _uiState.update {
            it.copy(
                showVictoryDialog = true,
                starsEarned = stars,
                coinsEarnedThisLevel = rewardCoins,
                coins = preferences.coins.value,
                maxUnlockedLevel = preferences.maxUnlockedLevel.value
            )
        }
    }

    fun undo() {
        if (_uiState.value.isPouring || historyStack.isEmpty()) return

        val prevTubes = historyStack.removeAt(historyStack.size - 1)
        soundManager.playTap()

        _uiState.update {
            it.copy(
                tubes = prevTubes,
                selectedTubeIndex = null,
                canUndo = historyStack.isNotEmpty(),
                movesCount = (it.movesCount - 1).coerceAtLeast(0),
                isStuck = false,
                hintSourceIndex = null,
                hintTargetIndex = null
            )
        }
    }

    fun restartLevel() {
        if (_uiState.value.isPouring) return
        soundManager.playTap()
        loadLevel(_uiState.value.currentLevel)
    }

    fun addExtraTube() {
        val state = _uiState.value
        if (state.isPouring || state.extraTubesCount >= 2) return

        // Open Watch Ad Dialog for instant extra tube upon 1 video ad
        _uiState.update {
            it.copy(
                showWatchAdDialog = true,
                adErrorMessage = null
            )
        }
    }

    fun grantExtraTube() {
        val state = _uiState.value
        if (state.extraTubesCount >= 2) return

        soundManager.playCompleteTube()
        val newTubeId = state.tubes.size
        val newTube = Tube(id = newTubeId, liquids = emptyList(), isExtraTube = true)

        _uiState.update {
            it.copy(
                tubes = it.tubes + newTube,
                extraTubesCount = it.extraTubesCount + 1,
                coins = preferences.coins.value,
                isStuck = false,
                showWatchAdDialog = false,
                adsWatchedForTube = 0,
                isAdLoading = false
            )
        }
    }

    fun watchAdForTube(activity: Activity?) {
        val state = _uiState.value
        if (state.isAdLoading) return

        _uiState.update { it.copy(isAdLoading = true, adErrorMessage = null) }

        if (activity != null && AdManager.isAdAvailable()) {
            AdManager.showRewardedAd(
                activity = activity,
                onRewardEarned = {
                    onAdWatchedSuccessfully()
                },
                onDismissed = {
                    _uiState.update { it.copy(isAdLoading = false) }
                },
                onFailed = { error ->
                    Log.w("GameViewModel", "Ad show failed: $error, falling back to simulated ad for smooth user testing")
                    onAdWatchedSuccessfully()
                }
            )
        } else {
            // Ad loading or in development/offline test environment:
            // Complete short ad simulation so player/tester gets the tube reliably
            viewModelScope.launch {
                delay(1000)
                onAdWatchedSuccessfully()
            }
        }
    }

    private fun onAdWatchedSuccessfully() {
        soundManager.playTap()
        val nextCount = _uiState.value.adsWatchedForTube + 1
        _uiState.update {
            it.copy(
                adsWatchedForTube = nextCount,
                isAdLoading = false
            )
        }
        // 1 video ad is enough to grant the extra tube immediately!
        if (nextCount >= 1) {
            grantExtraTube()
        }
    }

    fun dismissWatchAdDialog() {
        _uiState.update {
            it.copy(
                showWatchAdDialog = false,
                isAdLoading = false,
                adErrorMessage = null
            )
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (state.isPouring || state.isWon) return

        viewModelScope.launch {
            val hint = GameSolver.findHint(state.tubes)
            if (hint != null) {
                soundManager.playTap()
                _uiState.update {
                    it.copy(
                        hintSourceIndex = hint.fromIndex,
                        hintTargetIndex = hint.toIndex,
                        selectedTubeIndex = hint.fromIndex
                    )
                }
            } else {
                soundManager.playError()
                _uiState.update { it.copy(isStuck = true) }
            }
        }
    }

    fun nextLevel() {
        _uiState.update { it.copy(showVictoryDialog = false) }
        loadLevel(_uiState.value.currentLevel + 1)
    }

    fun dismissVictoryDialog() {
        _uiState.update { it.copy(showVictoryDialog = false) }
    }

    fun toggleSound() {
        val newVal = !_uiState.value.soundEnabled
        soundManager.isSoundEnabled = newVal
        preferences.setSoundEnabled(newVal)
        _uiState.update { it.copy(soundEnabled = newVal) }
    }

    fun setSoundVolume(volume: Float) {
        soundManager.masterVolume = volume
        preferences.setSoundVolume(volume)
        _uiState.update { it.copy(soundVolume = volume) }
    }

    fun toggleVibration() {
        val newVal = !_uiState.value.vibrationEnabled
        soundManager.isVibrationEnabled = newVal
        preferences.setVibrationEnabled(newVal)
        _uiState.update { it.copy(vibrationEnabled = newVal) }
    }

    fun toggleLanguage() {
        val newVal = !_uiState.value.isBengali
        preferences.setIsBengali(newVal)
        _uiState.update { it.copy(isBengali = newVal) }
    }

    fun setTubeSkin(skin: TubeSkin) {
        preferences.setTubeSkin(skin)
        _uiState.update { it.copy(tubeSkin = skin) }
    }

    fun setBgTheme(theme: BackgroundTheme) {
        preferences.setBgTheme(theme)
        _uiState.update { it.copy(bgTheme = theme) }
    }

    fun buyOrEquipSkin(skin: TubeSkin) {
        val state = _uiState.value
        val isUnlocked = state.unlockedSkins.contains(skin.name) || skin.coinCost == 0
        if (isUnlocked) {
            setTubeSkin(skin)
            soundManager.playTap()
            _uiState.update {
                it.copy(
                    shopMessage = if (it.isBengali) "${skin.titleBn} সিলেক্ট করা হয়েছে" else "${skin.titleEn} equipped"
                )
            }
        } else {
            if (state.coins >= skin.coinCost) {
                preferences.spendCoins(skin.coinCost)
                preferences.unlockSkin(skin)
                preferences.setTubeSkin(skin)
                soundManager.playCompleteTube()
                _uiState.update {
                    it.copy(
                        coins = preferences.coins.value,
                        unlockedSkins = it.unlockedSkins + skin.name,
                        tubeSkin = skin,
                        shopMessage = if (it.isBengali) "${skin.titleBn} আনলক ও সিলেক্ট করা হয়েছে! 🎉" else "${skin.titleEn} Unlocked! 🎉"
                    )
                }
            } else {
                soundManager.playError()
                _uiState.update {
                    it.copy(
                        shopMessage = if (it.isBengali) "কয়েন পর্যাপ্ত নয়! আরও ${skin.coinCost - it.coins} কয়েন প্রয়োজন।" else "Not enough coins! Need ${skin.coinCost - it.coins} more."
                    )
                }
            }
        }
    }

    fun buyOrEquipBgTheme(theme: BackgroundTheme) {
        val state = _uiState.value
        val isUnlocked = state.unlockedBgThemes.contains(theme.name) || theme.coinCost == 0
        if (isUnlocked) {
            setBgTheme(theme)
            soundManager.playTap()
            _uiState.update {
                it.copy(
                    shopMessage = if (it.isBengali) "${theme.titleBn} থিম সিলেক্ট করা হয়েছে" else "${theme.titleEn} theme equipped"
                )
            }
        } else {
            if (state.coins >= theme.coinCost) {
                preferences.spendCoins(theme.coinCost)
                preferences.unlockBgTheme(theme)
                preferences.setBgTheme(theme)
                soundManager.playCompleteTube()
                _uiState.update {
                    it.copy(
                        coins = preferences.coins.value,
                        unlockedBgThemes = it.unlockedBgThemes + theme.name,
                        bgTheme = theme,
                        shopMessage = if (it.isBengali) "${theme.titleBn} থিম আনলক হয়েছে! 🎉" else "${theme.titleEn} Theme Unlocked! 🎉"
                    )
                }
            } else {
                soundManager.playError()
                _uiState.update {
                    it.copy(
                        shopMessage = if (it.isBengali) "কয়েন পর্যাপ্ত নয়! আরও ${theme.coinCost - it.coins} কয়েন প্রয়োজন।" else "Not enough coins! Need ${theme.coinCost - it.coins} more."
                    )
                }
            }
        }
    }

    fun clearShopMessage() {
        _uiState.update { it.copy(shopMessage = null) }
    }

    // --- Authentication & User Profile ---

    fun login(usernameOrEmail: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = userRepository.login(usernameOrEmail, password)
            result.onSuccess { user ->
                soundManager.playLevelWin()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        isAuthLoading = false,
                        authErrorMessage = null,
                        coins = user.coins,
                        currentLevel = user.currentLevel,
                        maxUnlockedLevel = user.maxUnlockedLevel
                    )
                }
                preferences.setCoins(user.coins)
                preferences.setMaxUnlockedLevel(user.maxUnlockedLevel)
                preferences.setCurrentLevel(user.currentLevel)
                loadLevel(user.currentLevel)
                onSuccess()
            }.onFailure { error ->
                soundManager.playError()
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = if (it.isBengali) {
                            when {
                                error.message?.contains("User not found", ignoreCase = true) == true -> "ব্যবহারকারী পাওয়া যায়নি"
                                error.message?.contains("Incorrect password", ignoreCase = true) == true -> "ভুল পাসওয়ার্ড"
                                else -> error.message ?: "লগইন ব্যর্থ হয়েছে"
                            }
                        } else {
                            error.message ?: "Login failed"
                        }
                    )
                }
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        avatarEmoji: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = userRepository.register(username, email, password, avatarEmoji)
            result.onSuccess { user ->
                soundManager.playLevelWin()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        isAuthLoading = false,
                        authErrorMessage = null,
                        coins = user.coins,
                        currentLevel = user.currentLevel,
                        maxUnlockedLevel = user.maxUnlockedLevel
                    )
                }
                preferences.setCoins(user.coins)
                preferences.setMaxUnlockedLevel(user.maxUnlockedLevel)
                preferences.setCurrentLevel(user.currentLevel)
                loadLevel(user.currentLevel)
                onSuccess()
            }.onFailure { error ->
                soundManager.playError()
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = if (it.isBengali) {
                            when {
                                error.message?.contains("already exists", ignoreCase = true) == true -> "এই নামের ব্যবহারকারী ইতিমধ্যে রয়েছে"
                                error.message?.contains("Email already in use", ignoreCase = true) == true -> "এই ইমেইলটি ইতিমধ্যে ব্যবহৃত হয়েছে"
                                else -> error.message ?: "রেজিস্ট্রেশন ব্যর্থ হয়েছে"
                            }
                        } else {
                            error.message ?: "Registration failed"
                        }
                    )
                }
            }
        }
    }

    fun playAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val guest = userRepository.playAsGuest()
            _uiState.update {
                it.copy(
                    currentUser = guest,
                    isAuthLoading = false,
                    authErrorMessage = null
                )
            }
            onSuccess()
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        userRepository.logout()
        _uiState.update { it.copy(currentUser = null) }
        onLoggedOut()
    }

    fun updateUserAvatar(emoji: String) {
        viewModelScope.launch {
            userRepository.updateAvatar(emoji)
        }
    }

    fun syncUserStats() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            userRepository.updateUserStats(
                coins = _uiState.value.coins,
                currentLevel = _uiState.value.currentLevel,
                maxUnlockedLevel = _uiState.value.maxUnlockedLevel
            )
        }
    }
}
