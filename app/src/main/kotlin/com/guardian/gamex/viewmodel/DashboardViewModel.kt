package com.guardian.gamex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.guardian.gamex.core.detection.DeviceCapabilities
import com.guardian.gamex.core.detection.DeviceDetector
import com.guardian.gamex.data.prefs.PreferencesManager
import com.guardian.gamex.service.FpsData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardState(
    val fpsData: FpsData = FpsData(),
    val cpuUsage: Float = 0f,
    val batteryTemp: Float = 0f,
    val currentProfile: String = "balanced",
    val deviceCapabilities: DeviceCapabilities? = null,
    val overlayEnabled: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)
    private val deviceDetector = DeviceDetector(application)

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            combine(
                prefsManager.currentProfile,
                prefsManager.overlayEnabled,
                flowOf(deviceDetector.detectCapabilities())
            ) { profile, overlay, capabilities ->
                _state.value.copy(
                    currentProfile = profile,
                    overlayEnabled = overlay,
                    deviceCapabilities = capabilities
                )
            }.collect { _state.value = it }
        }
    }

    fun updateFpsData(fpsData: FpsData) {
        _state.value = _state.value.copy(fpsData = fpsData)
    }

    fun updateCpuUsage(usage: Float) {
        _state.value = _state.value.copy(cpuUsage = usage)
    }

    fun updateBatteryTemp(temp: Float) {
        _state.value = _state.value.copy(batteryTemp = temp)
    }

    fun setProfile(profile: String) {
        viewModelScope.launch {
            prefsManager.setCurrentProfile(profile)
        }
    }

    fun toggleOverlay() {
        viewModelScope.launch {
            prefsManager.setOverlayEnabled(!_state.value.overlayEnabled)
        }
    }
}