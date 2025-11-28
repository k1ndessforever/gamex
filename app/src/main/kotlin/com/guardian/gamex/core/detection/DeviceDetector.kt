package com.guardian.gamex.core.detection

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager

data class DeviceCapabilities(
    val model: String,
    val androidVersion: String,
    val maxRefreshRate: Float,
    val hasGameMode: Boolean
)

class DeviceDetector(private val context: Context) {

    fun detectCapabilities(): DeviceCapabilities {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display ?: windowManager.defaultDisplay
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            display.mode.refreshRate
        } else {
            @Suppress("DEPRECATION")
            display.refreshRate
        }

        val hasGameMode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        return DeviceCapabilities(
            model = "${Build.MANUFACTURER} ${Build.MODEL}",
            androidVersion = Build.VERSION.RELEASE,
            maxRefreshRate = refreshRate,
            hasGameMode = hasGameMode
        )
    }
}