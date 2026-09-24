package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004F5C),
    onPrimaryContainer = Color(0xFF9CF0FF),
    secondary = NeonPurple,
    onSecondary = Color(0xFF4C007D),
    secondaryContainer = Color(0xFF6B1DA8),
    onSecondaryContainer = Color(0xFFF1DBFF),
    tertiary = NeonGold,
    onTertiary = Color(0xFF422C00),
    tertiaryContainer = Color(0xFF5F4100),
    onTertiaryContainer = Color(0xFFFFDEA3),
    background = CyberNavy,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = NeonCoral,
    onError = Color(0xFF680017)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
