package com.guardian.gamex.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gamex_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val OVERLAY_ENABLED = booleanPreferencesKey("overlay_enabled")
        val CROSSHAIR_STYLE = stringPreferencesKey("crosshair_style")
        val CROSSHAIR_SIZE = floatPreferencesKey("crosshair_size")
        val CROSSHAIR_COLOR = intPreferencesKey("crosshair_color")
        val CROSSHAIR_OPACITY = floatPreferencesKey("crosshair_opacity")
        val CURRENT_PROFILE = stringPreferencesKey("current_profile")
        val TOS_ACCEPTED = booleanPreferencesKey("tos_accepted")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val overlayEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[OVERLAY_ENABLED] ?: false }

    val crosshairStyle: Flow<String> = context.dataStore.data
        .map { it[CROSSHAIR_STYLE] ?: "dot" }

    val crosshairSize: Flow<Float> = context.dataStore.data
        .map { it[CROSSHAIR_SIZE] ?: 10f }

    val crosshairColor: Flow<Int> = context.dataStore.data
        .map { it[CROSSHAIR_COLOR] ?: 0xFFFF0844.toInt() }

    val crosshairOpacity: Flow<Float> = context.dataStore.data
        .map { it[CROSSHAIR_OPACITY] ?: 0.8f }

    val currentProfile: Flow<String> = context.dataStore.data
        .map { it[CURRENT_PROFILE] ?: "balanced" }

    val tosAccepted: Flow<Boolean> = context.dataStore.data
        .map { it[TOS_ACCEPTED] ?: false }

    val darkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[DARK_THEME] ?: true }

    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[OVERLAY_ENABLED] = enabled }
    }

    suspend fun setCrosshairStyle(style: String) {
        context.dataStore.edit { it[CROSSHAIR_STYLE] = style }
    }

    suspend fun setCrosshairSize(size: Float) {
        context.dataStore.edit { it[CROSSHAIR_SIZE] = size }
    }

    suspend fun setCrosshairColor(color: Int) {
        context.dataStore.edit { it[CROSSHAIR_COLOR] = color }
    }

    suspend fun setCrosshairOpacity(opacity: Float) {
        context.dataStore.edit { it[CROSSHAIR_OPACITY] = opacity }
    }

    suspend fun setCurrentProfile(profile: String) {
        context.dataStore.edit { it[CURRENT_PROFILE] = profile }
    }

    suspend fun setTosAccepted(accepted: Boolean) {
        context.dataStore.edit { it[TOS_ACCEPTED] = accepted }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = enabled }
    }
}