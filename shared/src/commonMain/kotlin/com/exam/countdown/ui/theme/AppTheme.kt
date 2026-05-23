package com.exam.countdown.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// iOS / One UI Hybrid Colors
private val SystemBlueLight = Color(0xFF007AFF)
private val SystemBlueDark = Color(0xFF0A84FF)

// Light Mode Colors
private val LightBackground = Color(0xFFF2F2F7) // iOS grouped background
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFE5E5EA)
private val LightOnBackground = Color(0xFF000000)
private val LightOnSurfaceVariant = Color(0xFF8E8E93)
private val LightError = Color(0xFFFF3B30)

// Dark Mode Colors
private val DarkBackground = Color(0xFF000000)
private val DarkSurface = Color(0xFF1C1C1E) // iOS elevated surface
private val DarkSurfaceVariant = Color(0xFF2C2C2E)
private val DarkOnBackground = Color(0xFFFFFFFF)
private val DarkOnSurfaceVariant = Color(0xFFEBEBF5).copy(alpha = 0.6f)
private val DarkError = Color(0xFFFF453A)

val DarkColorScheme = darkColorScheme(
    primary = SystemBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF004088),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFF34C759), // System Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF00521C),
    onSecondaryContainer = Color(0xFFB3F5C0),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    onError = Color.White,
)

val LightColorScheme = lightColorScheme(
    primary = SystemBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001A40),
    secondary = Color(0xFF34C759),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD4F7DD),
    onSecondaryContainer = Color(0xFF002107),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
    onError = Color.White,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
