package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdManager
import com.example.audio.SoundManager
import com.example.data.GamePreferences
import com.example.data.UserRepository
import com.example.data.db.AppDatabase
import com.example.data.firebase.RealtimeDatabaseManager
import com.example.logic.GameSolver
import com.example.logic.MoveHint
import com.example.model.BackgroundTheme
import com.example.model.GameLevel
import com.example.model.LiquidColor
import com.example.model.Tube
import com.example.model.TubeSkin
import com.example.model.UserAccount
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    LOADING, START_MENU, GAME, AUTH
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val preferences = GamePreferences(application)
    val userRepository = UserRepository(db.userDao(), preferences)
    val adManager = AdManager(application)
    val soundManager = SoundManager(application)
    val realtimeDb = RealtimeDatabaseManager()
    val solver = GameSolver()

    private val _screenState = MutableStateFlow(ScreenState.LOADING)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _tubes = MutableStateFlow<List<Tube>>(emptyList())
    val tubes: StateFlow<List<Tube>> = _tubes.asStateFlow()

    private val _selectedTubeId = MutableStateFlow<Int?>(null)
    val selectedTubeId: StateFlow<Int?> = _selectedTubeId.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _isPouring = MutableStateFlow(false)
    val isPouring: StateFlow<Boolean> = _isPouring.asStateFlow()

    private val _pourColor = MutableStateFlow<LiquidColor?>(null)
    val pourColor: StateFlow<LiquidColor?> = _pourColor.asStateFlow()

    private val _isVictory = MutableStateFlow(false)
    val isVictory: StateFlow<Boolean> = _isVictory.asStateFlow()

    private val _activeHint = MutableStateFlow<MoveHint?>(null)
    val activeHint: StateFlow<MoveHint?> = _activeHint.asStateFlow()

    private val moveHistory = mutableListOf<List<Tube>>()

    val tubeSkin = preferences.tubeSkin.stateIn(viewModelScope, SharingStarted.Lazily, TubeSkin.CLASSIC)
    val bgTheme = preferences.bgTheme.stateIn(viewModelScope, SharingStarted.Lazily, BackgroundTheme.DEEP_NEBULA)
    val soundEnabled = preferences.soundEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val hapticEnabled = preferences.hapticEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val volume = preferences.volume.stateIn(viewModelScope, SharingStarted.Lazily, 0.8f)
    val extraTubesStock = preferences.extraTubesStock.stateIn(viewModelScope, SharingStarted.Lazily, 2)

    init {
        viewModelScope.launch {
            soundEnabled.collect { soundManager.isSoundEnabled = it }
        }
        viewModelScope.launch {
            hapticEnabled.collect { soundManager.isHapticEnabled = it }
        }
        viewModelScope.launch {
            volume.collect { soundManager.volume = it }
        }
        viewModelScope.launch {
            preferences.currentUsername.collect { username ->
                if (username != null) {
                    val user = db.userDao().getUserByUsername(username)
                    _currentUser.value = user
                    if (user != null) {
                        _currentLevel.value = user.currentLevel
                    }
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    fun navigateTo(state: ScreenState) {
        _screenState.value = state
    }

    fun startLevel(levelNum: Int) {
        _currentLevel.value = levelNum
        val level = GameLevel.generateLevel(levelNum)
        _tubes.value = level.initialTubes
        _selectedTubeId.value = null
        _movesCount.value = 0
        _isVictory.value = false
        _activeHint.value = null
        moveHistory.clear()
        _screenState.value = ScreenState.GAME
    }

    fun restartCurrentLevel() {
        startLevel(_currentLevel.value)
    }

    fun nextLevel(activity: Activity?) {
        val next = _currentLevel.value + 1
        _currentLevel.value = next
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                userRepository.updateLevel(user.username, next)
                realtimeDb.syncScore(user.username, next, (user.coins + 20))
            }
        }
        if (activity != null && next % 3 == 0) {
            adManager.showInterstitial(activity) {
                startLevel(next)
            }
        } else {
            startLevel(next)
        }
    }

    fun onTubeClicked(tubeId: Int) {
        if (_isPouring.value || _isVictory.value) return

        val currentSelected = _selectedTubeId.value
        if (currentSelected == null) {
            val tube = _tubes.value.find { it.id == tubeId } ?: return
            if (tube.isEmpty || tube.isCompleted) {
                soundManager.vibrateShort()
                return
            }
            _selectedTubeId.value = tubeId
            soundManager.playSelectSound()
            soundManager.vibrateShort()
        } else if (currentSelected == tubeId) {
            _selectedTubeId.value = null
            soundManager.vibrateShort()
        } else {
            pour(currentSelected, tubeId)
        }
    }

    private fun pour(fromId: Int, toId: Int) {
        val currentTubes = _tubes.value
        val fromTube = currentTubes.find { it.id == fromId } ?: return
        val toTube = currentTubes.find { it.id == toId } ?: return

        if (fromTube.isEmpty || toTube.isFull) {
            _selectedTubeId.value = null
            return
        }

        val movingColor = fromTube.topColor ?: return
        if (!toTube.isEmpty && toTube.topColor != movingColor) {
            _selectedTubeId.value = toId
            soundManager.vibrateShort()
            return
        }

        val countToMove = fromTube.topColorCount().coerceAtMost(toTube.availableSpace)
        if (countToMove <= 0) {
            _selectedTubeId.value = null
            return
        }

        viewModelScope.launch {
            moveHistory.add(currentTubes)
            _isPouring.value = true
            _pourColor.value = movingColor
            soundManager.playPourSound()
            soundManager.vibrateShort()

            val newFromSegments = fromTube.segments.dropLast(countToMove)
            val newToSegments = toTube.segments + List(countToMove) { movingColor }

            val updatedList = currentTubes.map { tube ->
                when (tube.id) {
                    fromId -> tube.copy(segments = newFromSegments)
                    toId -> tube.copy(segments = newToSegments)
                    else -> tube
                }
            }

            delay(280)
            _tubes.value = updatedList
            _selectedTubeId.value = null
            _isPouring.value = false
            _pourColor.value = null
            _movesCount.value += 1
            _activeHint.value = null

            checkVictory(updatedList)
        }
    }

    private fun checkVictory(tubesList: List<Tube>) {
        val nonEmpties = tubesList.filter { !it.isEmpty }
        val allCompleted = nonEmpties.all { it.isCompleted }
        if (allCompleted) {
            _isVictory.value = true
            soundManager.playVictorySound()
            soundManager.vibrateSuccess()

            viewModelScope.launch {
                _currentUser.value?.let { user ->
                    userRepository.addCoins(user.username, 25)
                }
            }
        }
    }

    fun undoMove() {
        if (moveHistory.isNotEmpty()) {
            _tubes.value = moveHistory.removeAt(moveHistory.size - 1)
            _selectedTubeId.value = null
            _movesCount.value = (_movesCount.value - 1).coerceAtLeast(0)
            soundManager.vibrateShort()
        }
    }

    fun requestHint() {
        val hint = solver.findNextMove(_tubes.value)
        _activeHint.value = hint
        soundManager.vibrateShort()
    }

    fun addExtraTube() {
        val current = _tubes.value
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
        _tubes.value = current + Tube(id = newId, segments = emptyList(), isExtraTube = true)
        viewModelScope.launch {
            preferences.addExtraTubeStock(-1)
        }
        soundManager.vibrateSuccess()
    }

    fun addStockFromRewardedAd(activity: Activity, onDone: () -> Unit) {
        adManager.showRewarded(
            activity,
            onRewardEarned = {
                viewModelScope.launch {
                    preferences.addExtraTubeStock(1)
                    addExtraTube()
                    onDone()
                }
            },
            onFailedOrDismiss = {
                onDone()
            }
        )
    }

    fun buyTubeWithCoins(onDone: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null && user.coins >= 50) {
                userRepository.addCoins(user.username, -50)
                addExtraTube()
                onDone()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
