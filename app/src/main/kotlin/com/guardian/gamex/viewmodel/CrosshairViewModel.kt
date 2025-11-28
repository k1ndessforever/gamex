package com.guardian.gamex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.guardian.gamex.data.prefs.PreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CrosshairSettings(
    val style: String = "dot",
    val size: Float = 10f,
    val color: Int = 0xFFFF0844.toInt(),
    val opacity: Float = 0.8f,
    val enabled: Boolean = false,
    val tosAccepted: Boolean = false
)

class CrosshairViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)

    private val _settings = MutableStateFlow(CrosshairSettings())
    val settings: StateFlow<CrosshairSettings> = _settings

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                prefsManager.crosshairStyle,
                prefsManager.crosshairSize,
                prefsManager.crosshairColor,
                prefsManager.crosshairOpacity,
                prefsManager.overlayEnabled,
                prefsManager.tosAccepted
            ) { flows: Array<Any?> ->
                CrosshairSettings(
                    style = flows[0] as String,
                    size = flows[1] as Float,
                    color = flows[2] as Int,
                    opacity = flows[3] as Float,
                    enabled = flows[4] as Boolean,
                    tosAccepted = flows[5] as Boolean
                )
            }.collect { _settings.value = it }
        }
    }

    fun setStyle(style: String) {
        viewModelScope.launch {
            prefsManager.setCrosshairStyle(style)
        }
    }

    fun setSize(size: Float) {
        viewModelScope.launch {
            prefsManager.setCrosshairSize(size)
        }
    }

    fun setColor(color: Int) {
        viewModelScope.launch {
            prefsManager.setCrosshairColor(color)
        }
    }

    fun setOpacity(opacity: Float) {
        viewModelScope.launch {
            prefsManager.setCrosshairOpacity(opacity)
        }
    }

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefsManager.setOverlayEnabled(enabled)
        }
    }

    fun acceptTos() {
        viewModelScope.launch {
            prefsManager.setTosAccepted(true)
        }
    }
}