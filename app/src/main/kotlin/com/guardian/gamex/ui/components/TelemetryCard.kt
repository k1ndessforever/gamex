package com.gamex.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamex.service.FpsData
import com.gamex.ui.theme.ElectricCyan
import com.gamex.ui.theme.NeonRed

@Composable
fun TelemetryCard(
    fpsData: FpsData,
    cpuUsage: Float,
    batteryTemp: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Real-Time Telemetry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryItem(
                    label = "FPS",
                    value = fpsData.currentFps.toInt().toString(),
                    color = if (fpsData.currentFps >= 60) ElectricCyan else NeonRed
                )
                TelemetryItem(
                    label = "AVG",
                    value = fpsData.avgFps.toInt().toString(),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TelemetryItem(
                    label = "CPU",
                    value = "${cpuUsage.toInt()}%",
                    color = MaterialTheme.colorScheme.onSurface
                )
                TelemetryItem(
                    label = "TEMP",
                    value = "${batteryTemp.toInt()}°C",
                    color = if (batteryTemp > 40) NeonRed else MaterialTheme.colorScheme.onSurface
                )
            }

            LinearProgressIndicator(
                progress = { (fpsData.currentFps / 120f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = ElectricCyan,
                trackColor = MaterialTheme.colorScheme.surface,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dropped: ${fpsData.droppedFrames}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ${fpsData.totalFrames}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TelemetryItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = value,
            label = "value_anim"
        ) { targetValue ->
            Text(
                text = targetValue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}