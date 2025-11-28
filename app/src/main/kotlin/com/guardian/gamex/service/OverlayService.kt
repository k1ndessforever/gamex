package com.guardian.gamex.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: CrosshairView? = null
    private var isShowing = false

    companion object {
        const val ACTION_SHOW = "com.guardian.gamex.SHOW_OVERLAY"
        const val ACTION_HIDE = "com.guardian.gamex.HIDE_OVERLAY"
        const val ACTION_UPDATE = "com.guardian.gamex.UPDATE_OVERLAY"
        const val EXTRA_STYLE = "style"
        const val EXTRA_SIZE = "size"
        const val EXTRA_COLOR = "color"
        const val EXTRA_OPACITY = "opacity"
        const val CHANNEL_ID = "overlay_service"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW -> showOverlay(intent)
            ACTION_HIDE -> hideOverlay()
            ACTION_UPDATE -> updateOverlay(intent)
        }
        return START_STICKY
    }

    private fun showOverlay(intent: Intent) {
        if (isShowing) return

        val style = intent.getStringExtra(EXTRA_STYLE) ?: "dot"
        val size = intent.getFloatExtra(EXTRA_SIZE, 10f)
        val color = intent.getIntExtra(EXTRA_COLOR, 0xFFFF0844.toInt())
        val opacity = intent.getFloatExtra(EXTRA_OPACITY, 0.8f)

        overlayView = CrosshairView(this, style, size, color, opacity)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager?.addView(overlayView, params)
        isShowing = true
    }

    private fun hideOverlay() {
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
            isShowing = false
        }
    }

    private fun updateOverlay(intent: Intent) {
        hideOverlay()
        showOverlay(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Crosshair Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps crosshair overlay active"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("GameX Overlay Active")
        .setContentText("Crosshair overlay is running")
        .setSmallIcon(android.R.drawable.ic_menu_view)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
    }
}

class CrosshairView(
    context: Context,
    private val style: String,
    private val size: Float,
    private val color: Int,
    private val opacity: Float
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        alpha = (opacity * 255).toInt()
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = size * 2

        when (style) {
            "dot" -> {
                paint.style = Paint.Style.FILL
                canvas.drawCircle(cx, cy, size / 2, paint)
            }
            "plus" -> {
                paint.style = Paint.Style.STROKE
                canvas.drawLine(cx - radius, cy, cx + radius, cy, paint)
                canvas.drawLine(cx, cy - radius, cx, cy + radius, paint)
            }
            "hollow" -> {
                paint.style = Paint.Style.STROKE
                canvas.drawCircle(cx, cy, radius, paint)
                canvas.drawCircle(cx, cy, 2f, paint)
            }
            "sniper" -> {
                paint.style = Paint.Style.STROKE
                canvas.drawLine(cx - radius, cy, cx - radius / 3, cy, paint)
                canvas.drawLine(cx + radius / 3, cy, cx + radius, cy, paint)
                canvas.drawLine(cx, cy - radius, cx, cy - radius / 3, paint)
                canvas.drawLine(cx, cy + radius / 3, cx, cy + radius, paint)
                canvas.drawCircle(cx, cy, 2f, paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dimension = (size * 6).toInt()
        setMeasuredDimension(dimension, dimension)
    }
}

