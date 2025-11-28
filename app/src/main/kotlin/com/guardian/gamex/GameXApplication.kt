package com.guardian.gamex

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.guardian.gamex.service.OverlayService
import com.guardian.gamex.service.PerformanceService

class GameXApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    OverlayService.CHANNEL_ID,
                    "Crosshair Overlay",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Keeps crosshair overlay active"
                },
                NotificationChannel(
                    PerformanceService.CHANNEL_ID,
                    "Performance Mode",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Maintains foreground priority for optimization"
                }
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }
}