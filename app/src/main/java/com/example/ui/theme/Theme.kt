package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = NeonDarkBg,
    primaryContainer = NeonDarkSurfaceVariant,
    onPrimaryContainer = NeonCyan,
    secondary = NeonViolet,
    onSecondary = NeonDarkBg,
    secondaryContainer = NeonDarkSurfaceVariant,
    onSecondaryContainer = NeonViolet,
    tertiary = NeonGold,
    background = NeonDarkBg,
    onBackground = NeonTextPrimary,
    surface = NeonDarkSurface,
    onSurface = NeonTextPrimary,
    surfaceVariant = NeonDarkSurfaceVariant,
    onSurfaceVariant = NeonTextSecondary,
    outline = NeonDarkBorder,
    outlineVariant = NeonDarkBorderGlow
)

private val CrispLightColorScheme = lightColorScheme(
    primary = LightCyanX,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = LightCyanX,
    secondary = LightVioletO,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF),
    onSecondaryContainer = LightVioletO,
    tertiary = Color(0xFFD97706),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFF94A3B8)
)

@Composable
fun OrvixTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NeonDarkColorScheme else CrispLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
