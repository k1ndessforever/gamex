package com.guardian.gamex.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = TextPrimary,
    primaryContainer = NeonRedDim,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricCyan,
    onSecondary = CharcoalBlack,
    secondaryContainer = CyanDim,
    onSecondaryContainer = TextPrimary,
    tertiary = ElectricCyan,
    onTertiary = CharcoalBlack,
    background = CharcoalBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = DarkRed,
    onPrimary = TextPrimary,
    primaryContainer = NeonRedDim,
    onPrimaryContainer = LightTextPrimary,
    secondary = DarkCyan,
    onSecondary = TextPrimary,
    secondaryContainer = CyanDim,
    onSecondaryContainer = LightTextPrimary,
    tertiary = DarkCyan,
    onTertiary = TextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
    outline = LightTextSecondary
)

@Composable
fun GameXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}