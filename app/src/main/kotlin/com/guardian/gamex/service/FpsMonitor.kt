package com.guardian.gamex.service

import android.view.FrameMetrics
import android.view.Window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FpsData(
    val currentFps: Float = 0f,
    val avgFps: Float = 0f,
    val minFps: Float = 0f,
    val maxFps: Float = 0f,
    val frameTimeP95: Float = 0f,
    val totalFrames: Int = 0,
    val droppedFrames: Int = 0
)

class FpsMonitor {
    private val _fpsData = MutableStateFlow(FpsData())
    val fpsData: StateFlow<FpsData> = _fpsData

    private val frameTimes = mutableListOf<Long>()
    private var totalFrames = 0
    private var droppedFrames = 0
    private var lastTimestamp = System.nanoTime()

    private val frameMetricsListener = Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
        val totalDuration = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
        processFrame(totalDuration)
    }

    fun attachToWindow(window: Window) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            window.addOnFrameMetricsAvailableListener(frameMetricsListener, null)
        }
    }

    fun detachFromWindow(window: Window) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            window.removeOnFrameMetricsAvailableListener(frameMetricsListener)
        }
    }

    private fun processFrame(frameTimeNanos: Long) {
        val frameTimeMs = frameTimeNanos / 1_000_000f

        frameTimes.add(frameTimeNanos)
        if (frameTimes.size > 120) {
            frameTimes.removeAt(0)
        }

        totalFrames++

        if (frameTimeMs > 33.33f) {
            droppedFrames++
        }

        val now = System.nanoTime()
        val elapsedSec = (now - lastTimestamp) / 1_000_000_000.0
        val currentFps = if (elapsedSec > 0) (1.0 / (frameTimeMs / 1000.0)).toFloat() else 0f
        lastTimestamp = now

        if (frameTimes.size >= 10) {
            val sorted = frameTimes.sorted()
            val avg = frameTimes.average().toFloat() / 1_000_000f
            val min = sorted.first().toFloat() / 1_000_000f
            val max = sorted.last().toFloat() / 1_000_000f
            val p95Index = (sorted.size * 0.95).toInt()
            val p95 = sorted[p95Index].toFloat() / 1_000_000f

            val avgFps = 1000f / avg
            val minFps = 1000f / max
            val maxFps = 1000f / min

            _fpsData.value = FpsData(
                currentFps = currentFps.coerceIn(0f, 240f),
                avgFps = avgFps.coerceIn(0f, 240f),
                minFps = minFps.coerceIn(0f, 240f),
                maxFps = maxFps.coerceIn(0f, 240f),
                frameTimeP95 = p95,
                totalFrames = totalFrames,
                droppedFrames = droppedFrames
            )
        }
    }

    fun reset() {
        frameTimes.clear()
        totalFrames = 0
        droppedFrames = 0
        _fpsData.value = FpsData()
    }
}