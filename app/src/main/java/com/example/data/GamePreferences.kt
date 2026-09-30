package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.BackgroundTheme
import com.example.model.TubeSkin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("water_sort_prefs", Context.MODE_PRIVATE)

    private val _currentLevel = MutableStateFlow(prefs.getInt("current_level", 1))
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _maxUnlockedLevel = MutableStateFlow(prefs.getInt("max_unlocked_level", 1))
    val maxUnlockedLevel: StateFlow<Int> = _maxUnlockedLevel.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt("coins", 5))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _soundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _soundVolume = MutableStateFlow(prefs.getFloat("sound_volume", 0.22f))
    val soundVolume: StateFlow<Float> = _soundVolume.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean("vibration_enabled", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _isBengali = MutableStateFlow(prefs.getBoolean("is_bengali", true))
    val isBengali: StateFlow<Boolean> = _isBengali.asStateFlow()

    private val _tubeSkin = MutableStateFlow(
        try {
            TubeSkin.valueOf(prefs.getString("tube_skin", TubeSkin.CLASSIC.name) ?: TubeSkin.CLASSIC.name)
        } catch (_: Exception) {
            TubeSkin.CLASSIC
        }
    )
    val tubeSkin: StateFlow<TubeSkin> = _tubeSkin.asStateFlow()

    private val _bgTheme = MutableStateFlow(
        try {
            BackgroundTheme.valueOf(prefs.getString("bg_theme", BackgroundTheme.AURORA_NIGHT.name) ?: BackgroundTheme.AURORA_NIGHT.name)
        } catch (_: Exception) {
            BackgroundTheme.AURORA_NIGHT
        }
    )
    val bgTheme: StateFlow<BackgroundTheme> = _bgTheme.asStateFlow()

    private val _unlockedSkins = MutableStateFlow(
        prefs.getStringSet("unlocked_skins", setOf(TubeSkin.CLASSIC.name)) ?: setOf(TubeSkin.CLASSIC.name)
    )
    val unlockedSkins: StateFlow<Set<String>> = _unlockedSkins.asStateFlow()

    private val _unlockedBgThemes = MutableStateFlow(
        prefs.getStringSet("unlocked_bg_themes", setOf(BackgroundTheme.AURORA_NIGHT.name)) ?: setOf(BackgroundTheme.AURORA_NIGHT.name)
    )
    val unlockedBgThemes: StateFlow<Set<String>> = _unlockedBgThemes.asStateFlow()

    fun unlockSkin(skin: TubeSkin) {
        val updated = _unlockedSkins.value + skin.name
        _unlockedSkins.value = updated
        prefs.edit().putStringSet("unlocked_skins", updated).apply()
    }

    fun unlockBgTheme(theme: BackgroundTheme) {
        val updated = _unlockedBgThemes.value + theme.name
        _unlockedBgThemes.value = updated
        prefs.edit().putStringSet("unlocked_bg_themes", updated).apply()
    }

    fun setCurrentLevel(level: Int) {
        _currentLevel.value = level
        prefs.edit().putInt("current_level", level).apply()
        if (level > _maxUnlockedLevel.value) {
            _maxUnlockedLevel.value = level
            prefs.edit().putInt("max_unlocked_level", level).apply()
        }
    }

    fun setMaxUnlockedLevel(level: Int) {
        _maxUnlockedLevel.value = level
        prefs.edit().putInt("max_unlocked_level", level).apply()
    }

    fun unlockNextLevel(completedLevel: Int) {
        val next = completedLevel + 1
        if (next > _maxUnlockedLevel.value) {
            _maxUnlockedLevel.value = next
            prefs.edit().putInt("max_unlocked_level", next).apply()
        }
    }

    fun setLevelStars(level: Int, stars: Int) {
        val current = prefs.getInt("stars_level_$level", 0)
        if (stars > current) {
            prefs.edit().putInt("stars_level_$level", stars).apply()
        }
    }

    fun getLevelStars(level: Int): Int {
        return prefs.getInt("stars_level_$level", 0)
    }

    fun setCoins(amount: Int) {
        _coins.value = amount
        prefs.edit().putInt("coins", amount).apply()
    }

    fun addCoins(amount: Int) {
        val updated = _coins.value + amount
        _coins.value = updated
        prefs.edit().putInt("coins", updated).apply()
    }

    fun spendCoins(amount: Int): Boolean {
        if (_coins.value >= amount) {
            val updated = _coins.value - amount
            _coins.value = updated
            prefs.edit().putInt("coins", updated).apply()
            return true
        }
        return false
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun setSoundVolume(volume: Float) {
        val clamped = volume.coerceIn(0.05f, 1f)
        _soundVolume.value = clamped
        prefs.edit().putFloat("sound_volume", clamped).apply()
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
    }

    fun setIsBengali(isBn: Boolean) {
        _isBengali.value = isBn
        prefs.edit().putBoolean("is_bengali", isBn).apply()
    }

    fun setTubeSkin(skin: TubeSkin) {
        _tubeSkin.value = skin
        prefs.edit().putString("tube_skin", skin.name).apply()
    }

    fun setBgTheme(theme: BackgroundTheme) {
        _bgTheme.value = theme
        prefs.edit().putString("bg_theme", theme.name).apply()
    }
}
