package com.example.habitflow.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Premium Dark Mode Color Scheme
 * 
 * Key Features:
 * - Avoids pure black (#000000) for better shadow visibility
 * - Desaturated orange primary for reduced eye strain
 * - Strict surface hierarchy for elevation depth
 * - High contrast text for accessibility
 */
private val PremiumDarkColorScheme = darkColorScheme(
    // Primary Color (Desaturated Orange)
    primary = PremiumOrange,
    onPrimary = OnPrimaryColor,
    primaryContainer = PremiumOrangeDark,
    onPrimaryContainer = Color(0xFFFFE0B2),
    
    // Secondary Color (Cool Blue Accent)
    secondary = SecondaryColor,
    onSecondary = OnSecondaryColor,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = Color(0xFFBBDEFB),
    
    // Tertiary Color (Success Green)
    tertiary = TertiaryColor,
    onTertiary = OnTertiaryColor,
    tertiaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color(0xFFC8E6C9),
    
    // Background & Surface Hierarchy
    background = BackgroundDark,           // 0x0D0D0D - Deepest layer
    onBackground = OnBackgroundColor,
    
    surface = SurfaceDark,                 // 0x121212 - Base surface
    onSurface = OnSurfaceColor,
    
    surfaceVariant = SurfaceVariant,       // 0x1E1E1E - Elevated surface
    onSurfaceVariant = OnSurfaceVariant,
    
    surfaceContainer = SurfaceContainerLow,        // 0x1A1A1A
    surfaceContainerHigh = SurfaceContainerHigh,   // 0x252525
    surfaceContainerHighest = Color(0xFF2C2C2C),
    
    // Inverse Surface (for contrast elements like Snackbar)
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = PremiumOrangeDark,
    
    // Error Colors
    error = ErrorColor,
    onError = OnErrorColor,
    errorContainer = Color(0xFF5D1F1F),
    onErrorContainer = Color(0xFFFFCDD2),
    
    // Outline Colors
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    
    // Scrim (overlay for modals)
    scrim = ScrimColor,
    
    // Surface Tint (for elevation)
    surfaceTint = PremiumOrange
)

@Composable
fun HabitFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to maintain brand identity
    content: @Composable () -> Unit
) {
    // Always use Premium Dark Mode for consistent branding
    val colorScheme = PremiumDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Set status bar to background color for seamless experience
            window.statusBarColor = colorScheme.background.toArgb()
            
            // Set navigation bar to surface color
            window.navigationBarColor = colorScheme.surface.toArgb()
            
            // Ensure light icons on dark background
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

