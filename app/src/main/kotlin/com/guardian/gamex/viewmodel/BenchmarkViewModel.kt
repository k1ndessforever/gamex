package com.gamex.viewmodel

import android.app.Application
import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gamex.data.room.BenchmarkDatabase
import com.gamex.data.room.BenchmarkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BenchmarkState(
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val currentResult: BenchmarkResult? = null,
    val recentResults: List<BenchmarkResult> = emptyList()
)

class BenchmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BenchmarkDatabase.getDatabase(application)
    private val dao = database.benchmarkDao()

    private val _state = MutableStateFlow(BenchmarkState())
    val state: StateFlow<BenchmarkState> = _state

    init {
        viewModelScope.launch {
            dao.getRecentResults().collect { results ->
                _state.value = _state.value.copy(recentResults = results)
            }
        }
    }

    suspend fun runBenchmark(durationMs: Long, profile: String): BenchmarkResult {
        _state.value = _state.value.copy(isRunning = true, progress = 0f)

        val startTime = System.currentTimeMillis()
        val frameTimes = mutableListOf<Float>()
        var totalFrames = 0
        var droppedFrames = 0

        // Simulate rendering loop (in real implementation, use actual GL rendering)
        while (System.currentTimeMillis() - startTime < durationMs) {
            val frameStart = System.nanoTime()

            // Simulate frame work
            delay(16) // ~60fps target

            val frameTime = (System.nanoTime() - frameStart) / 1_000_000f
            frameTimes.add(frameTime)
            totalFrames++

            if (frameTime > 33.33f) droppedFrames++

            val progress = (System.currentTimeMillis() - startTime).toFloat() / durationMs
            _state.value = _state.value.copy(progress = progress)
        }

        // Calculate metrics
        val sorted = frameTimes.sorted()
        val avgFps = 1000f / frameTimes.average().toFloat()
        val minFps = 1000f / sorted.last()
        val maxFps = 1000f / sorted.first()
        val p50 = sorted[sorted.size / 2]
        val p95 = sorted[(sorted.size * 0.95).toInt()]
        val p99 = sorted[(sorted.size * 0.99).toInt()]

        val cpuUsage = getCpuUsage()
        val batteryTemp = getBatteryTemperature()

        val result = BenchmarkResult(
            timestamp = System.currentTimeMillis(),
            durationMs = durationMs,
            avgFps = avgFps,
            minFps = minFps,
            maxFps = maxFps,
            frameTimeP50 = p50,
            frameTimeP95 = p95,
            frameTimeP99 = p99,
            totalFrames = totalFrames,
            droppedFrames = droppedFrames,
            cpuUsagePercent = cpuUsage,
            batteryTempCelsius = batteryTemp,
            profileUsed = profile
        )

        dao.insert(result)
        _state.value = _state.value.copy(isRunning = false, currentResult = result)

        return result
    }

    private fun getCpuUsage(): Float {
        val am = getApplication<Application>().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        // Simplified CPU usage estimation
        val usedMemPercent = (1 - memInfo.availMem.toFloat() / memInfo.totalMem) * 100
        return usedMemPercent.coerceIn(0f, 100f)
    }

    private fun getBatteryTemperature(): Float {
        val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val temp = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return temp / 10f // Convert to Celsius (simplified)
    }

    fun clearResults() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}