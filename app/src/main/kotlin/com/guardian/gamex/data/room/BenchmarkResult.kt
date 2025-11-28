package com.guardian.gamex.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benchmark_results")
data class BenchmarkResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val durationMs: Long,
    val avgFps: Float,
    val minFps: Float,
    val maxFps: Float,
    val frameTimeP50: Float,
    val frameTimeP95: Float,
    val frameTimeP99: Float,
    val totalFrames: Int,
    val droppedFrames: Int,
    val cpuUsagePercent: Float,
    val batteryTempCelsius: Float,
    val profileUsed: String
)
