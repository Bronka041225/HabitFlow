package com.example.habitflow.ui.theme

import androidx.compose.ui.graphics.Color

// ========================================
// Premium Dark Mode Color Palette
// ========================================

// Primary Colors (Desaturated Orange for reduced eye strain)
val PremiumOrange = Color(0xFFFFB74D)        // Softer, desaturated orange
val PremiumOrangeDark = Color(0xFFFF9800)    // Slightly more saturated variant
val OnPrimaryColor = Color(0xFF000000)       // Black text on orange

// Background & Surface Hierarchy (Critical for elevation)
val BackgroundDark = Color(0xFF0D0D0D)       // Deepest layer
val SurfaceDark = Color(0xFF121212)          // Base surface (Material Design standard)
val SurfaceVariant = Color(0xFF1E1E1E)       // Elevated surface
val SurfaceContainerLow = Color(0xFF1A1A1A)  // Between background and surface
val SurfaceContainerHigh = Color(0xFF252525) // Highest elevation

// Inverse Surface (for contrast elements)
val InverseSurface = Color(0xFFE4E4E4)       // Light grey for inverse
val InverseOnSurface = Color(0xFF2C2C2C)     // Dark text on inverse

// Text Colors (High contrast for readability)
val OnSurfaceColor = Color(0xFFE8E8E8)       // Primary text
val OnSurfaceVariant = Color(0xFFB0B0B0)     // Secondary text
val OnBackgroundColor = Color(0xFFE8E8E8)    // Text on background

// Accent & Semantic Colors
val SecondaryColor = Color(0xFF64B5F6)       // Cool blue accent
val OnSecondaryColor = Color(0xFF000000)     // Black text on secondary

val TertiaryColor = Color(0xFF81C784)        // Success green
val OnTertiaryColor = Color(0xFF000000)      // Black text on tertiary

val ErrorColor = Color(0xFFEF5350)           // Error red
val OnErrorColor = Color(0xFF000000)         // Black text on error

// Additional UI Elements
val OutlineColor = Color(0xFF3D3D3D)         // Border color
val OutlineVariant = Color(0xFF2C2C2C)       // Subtle outline

// Scrim (for modals/overlays)
val ScrimColor = Color(0xB3000000)           // 70% black

// ========================================
// Legacy Colors (for gradual migration)
// ========================================
val ElectricOrange = Color(0xFFFF6D00)       // Original high-contrast orange
val DeepMidnightBlue = Color(0xFF121212)     // Same as SurfaceDark
val DarkSurface = Color(0xFF1E1E1E)          // Same as SurfaceVariant
val LightSurface = Color(0xFF2C2C2C)         // Slightly lighter
val WhiteHighContrast = Color(0xFFFFFFFF)    // Pure white
val WhiteSemiTransparent = Color(0xB3FFFFFF) // 70% opacity
val GlassBorder = Color(0x1AFFFFFF)          // 10% opacity

