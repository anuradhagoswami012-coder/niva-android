package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkSage,
    onPrimary = Color(0xFF152619),
    primaryContainer = Color(0xFF2C3E30),
    onPrimaryContainer = Color(0xFFCCE4D1),
    secondary = DarkTerracotta,
    onSecondary = Color(0xFF421B13),
    secondaryContainer = Color(0xFF5A2C21),
    onSecondaryContainer = Color(0xFFFBD7CE),
    tertiary = SparkleGold,
    background = DarkBg,
    onBackground = Color(0xFFEDE9E3),
    surface = DarkSurface,
    onSurface = Color(0xFFEDE9E3),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4CDC7),
    outline = Color(0xFF4F5953)
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = OnSageContainer,
    secondary = TerracottaAccent,
    onSecondary = Color.White,
    secondaryContainer = TerracottaContainer,
    onSecondaryContainer = OnTerracottaContainer,
    tertiary = SparkleGold,
    tertiaryContainer = SparkleContainer,
    background = CreamBackground,
    onBackground = CharcoalDark,
    surface = WarmSurface,
    onSurface = CharcoalDark,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = CharcoalMedium,
    outline = DividerWarm
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our bespoke calm palette by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

