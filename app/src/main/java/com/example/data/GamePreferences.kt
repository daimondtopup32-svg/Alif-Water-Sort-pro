package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.BackgroundTheme
import com.example.model.TubeSkin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "game_settings")

class GamePreferences(private val context: Context) {
    companion object {
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val KEY_VOLUME = floatPreferencesKey("volume")
        val KEY_TUBE_SKIN = stringPreferencesKey("tube_skin")
        val KEY_BG_THEME = stringPreferencesKey("bg_theme")
        val KEY_CURRENT_USER = stringPreferencesKey("current_user")
        val KEY_EXTRA_TUBES_STOCK = intPreferencesKey("extra_tubes_stock")
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND_ENABLED] ?: true }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC_ENABLED] ?: true }
    val volume: Flow<Float> = context.dataStore.data.map { it[KEY_VOLUME] ?: 0.8f }
    val tubeSkin: Flow<TubeSkin> = context.dataStore.data.map {
        val name = it[KEY_TUBE_SKIN] ?: TubeSkin.CLASSIC.name
        try { TubeSkin.valueOf(name) } catch (e: Exception) { TubeSkin.CLASSIC }
    }
    val bgTheme: Flow<BackgroundTheme> = context.dataStore.data.map {
        val name = it[KEY_BG_THEME] ?: BackgroundTheme.DEEP_NEBULA.name
        try { BackgroundTheme.valueOf(name) } catch (e: Exception) { BackgroundTheme.DEEP_NEBULA }
    }
    val currentUsername: Flow<String?> = context.dataStore.data.map { it[KEY_CURRENT_USER] }
    val extraTubesStock: Flow<Int> = context.dataStore.data.map { it[KEY_EXTRA_TUBES_STOCK] ?: 2 }

    suspend fun setSoundEnabled(enabled: Boolean) = context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    suspend fun setHapticEnabled(enabled: Boolean) = context.dataStore.edit { it[KEY_HAPTIC_ENABLED] = enabled }
    suspend fun setVolume(vol: Float) = context.dataStore.edit { it[KEY_VOLUME] = vol }
    suspend fun setTubeSkin(skin: TubeSkin) = context.dataStore.edit { it[KEY_TUBE_SKIN] = skin.name }
    suspend fun setBgTheme(theme: BackgroundTheme) = context.dataStore.edit { it[KEY_BG_THEME] = theme.name }
    suspend fun setCurrentUsername(user: String?) = context.dataStore.edit {
        if (user == null) it.remove(KEY_CURRENT_USER) else it[KEY_CURRENT_USER] = user
    }
    suspend fun setExtraTubesStock(count: Int) = context.dataStore.edit { it[KEY_EXTRA_TUBES_STOCK] = count }
    suspend fun addExtraTubeStock(delta: Int) = context.dataStore.edit {
        val curr = it[KEY_EXTRA_TUBES_STOCK] ?: 2
        it[KEY_EXTRA_TUBES_STOCK] = (curr + delta).coerceAtLeast(0)
    }
}
