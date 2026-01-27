package com.example.habitflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Force Dark Mode Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = ElectricOrange,
    onPrimary = WhiteHighContrast,
    background = DeepMidnightBlue,
    onBackground = WhiteHighContrast,
    surface = DarkSurface,
    onSurface = WhiteHighContrast,
    secondary = ElectricOrange, // Re-use primary for secondary in this minimal scheme
    onSecondary = WhiteHighContrast,
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Keeping param but ignoring it logic-wise for forcing dark
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce our branding
    content: @Composable () -> Unit
) {
    // We enforce DarkColorScheme regardless of system setting for this specific aesthetic
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
