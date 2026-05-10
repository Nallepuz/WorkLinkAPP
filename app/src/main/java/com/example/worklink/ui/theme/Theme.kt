package com.example.worklink.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Paleta Índigo Moderno ──────────────────────────────
val Indigo900 = Color(0xFF1A237E)
val Indigo800 = Color(0xFF283593)
val Indigo700 = Color(0xFF303F9F)
val Indigo500 = Color(0xFF3F51B5)
val Indigo300 = Color(0xFF7986CB)
val Indigo100 = Color(0xFFC5CAE9)
val Indigo50  = Color(0xFFE8EAF6)

val Amber700  = Color(0xFFF57C00)
val Amber500  = Color(0xFFFF9800)
val Amber100  = Color(0xFFFFE0B2)

// ── Modo claro ─────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary            = Indigo800,
    onPrimary          = Color.White,
    primaryContainer   = Indigo50,
    onPrimaryContainer = Indigo900,
    secondary          = Indigo500,
    onSecondary        = Color.White,
    secondaryContainer = Indigo100,
    onSecondaryContainer = Indigo900,
    tertiary           = Amber700,
    onTertiary         = Color.White,
    tertiaryContainer  = Amber100,
    onTertiaryContainer = Color(0xFF4A2800),
    background         = Color(0xFFF3F4F8),
    onBackground       = Color(0xFF1A1C3A),
    surface            = Color.White,
    onSurface          = Color(0xFF1A1C3A),
    surfaceVariant     = Indigo50,
    onSurfaceVariant   = Indigo700,
    outline            = Indigo100,
    error              = Color(0xFFB00020),
    onError            = Color.White,
)

// ── Modo oscuro ────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Indigo300,
    onPrimary          = Indigo900,
    primaryContainer   = Indigo800,
    onPrimaryContainer = Indigo50,
    secondary          = Indigo300,
    onSecondary        = Indigo900,
    secondaryContainer = Indigo700,
    onSecondaryContainer = Indigo50,
    tertiary           = Amber500,
    onTertiary         = Color(0xFF4A2800),
    tertiaryContainer  = Color(0xFF6D3F00),
    onTertiaryContainer = Amber100,
    background         = Color(0xFF121218),
    onBackground       = Color(0xFFE4E1FF),
    surface            = Color(0xFF1E1E2E),
    onSurface          = Color(0xFFE4E1FF),
    surfaceVariant     = Color(0xFF2A2A3E),
    onSurfaceVariant   = Indigo100,
    outline            = Color(0xFF494980),
    error              = Color(0xFFCF6679),
    onError            = Color(0xFF640019),
)

@Composable
fun WorkLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}