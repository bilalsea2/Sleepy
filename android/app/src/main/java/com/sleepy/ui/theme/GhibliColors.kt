package com.sleepy.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Studio Ghibli-inspired color palette with Solarized influences
 * Designed for eye comfort and aesthetic beauty
 */

// Light Theme - "Ghibli Meadow"
// Inspired by sunny fields, clear skies, and warm afternoons
// Enhanced contrast for better readability
object GhibliLightColors {
    // Base colors (Solarized-inspired with more contrast)
    val Background = Color(0xFFFDF8E8)      // Warmer cream with more yellow
    val Surface = Color(0xFFFFF9E5)         // Lighter, more pronounced
    val SurfaceVariant = Color(0xFFFFF4D6)  // Golden highlight

    // Primary colors (Vibrant Ghibli greens)
    val Primary = Color(0xFF6B9B47)         // More saturated grass green
    val PrimaryVariant = Color(0xFF8FB86E)  // Brighter sage
    val PrimaryDark = Color(0xFF4A7A2F)     // Deep forest with contrast

    // Secondary colors (Brighter sky and accents)
    val Secondary = Color(0xFF5FA8D3)       // More vibrant sky blue
    val SecondaryVariant = Color(0xFF8FC5E8) // Lighter bright sky
    val Accent = Color(0xFFE89A5D)          // Stronger sunset orange

    // Text colors (Higher contrast)
    val TextPrimary = Color(0xFF2A2520)     // Darker, more readable
    val TextSecondary = Color(0xFF5A4F45)   // Deeper medium gray
    val TextTertiary = Color(0xFF8B7E70)    // Visible light gray

    // Prayer time specific
    val Fajr = Color(0xFF88B3D0)            // Morning sky blue
    val Isha = Color(0xFF7BA05B)            // Evening green
    val SleepTime = Color(0xFFE8A87C)       // Warm sunset glow

    // Functional colors
    val Error = Color(0xFFD95B43)           // Soft red, not harsh
    val Success = Color(0xFF7BA05B)         // Same as primary
    val Warning = Color(0xFFE8A87C)         // Warm amber

    // Canvas texture overlay (applied with very low opacity)
    val CanvasTexture = Color(0xFF9B8B76)   // Earthy beige for texture
    val PaperGrain = Color(0xFF6B6560)      // For subtle grain effect
}

// Dark Theme - "Ghibli Twilight"
// Inspired by moonlit forests, starry skies, and cozy nights
// Enhanced contrast for better nighttime readability
object GhibliDarkColors {
    // Base colors (Richer dark tones)
    val Background = Color(0xFF15191A)      // Deep night sky (almost black but warm)
    val Surface = Color(0xFF1E2425)         // Lighter surface with more contrast
    val SurfaceVariant = Color(0xFF2A3133)  // Clear elevation difference

    // Primary colors (Glowing moonlit nature)
    val Primary = Color(0xFFA5D07E)         // Brighter moonlit green
    val PrimaryVariant = Color(0xFFC5E8A5)  // Very light, glowing green
    val PrimaryDark = Color(0xFF7BA05B)     // Standard green

    // Secondary colors (Twilight and starlight)
    val Secondary = Color(0xFF6BA3D3)       // Brighter twilight blue
    val SecondaryVariant = Color(0xFF95C4E8) // Lighter starlight blue
    val Accent = Color(0xFFFFBE7D)          // Warm golden glow

    // Text colors (Higher contrast for dark mode)
    val TextPrimary = Color(0xFFFFFBF0)     // Near-white warm cream
    val TextSecondary = Color(0xFFE8DCC8)   // Clear medium cream
    val TextTertiary = Color(0xFFC5B8A5)    // Visible muted gray

    // Prayer time specific (glowing at night)
    val Fajr = Color(0xFF95B0CA)            // Pre-dawn blue
    val Isha = Color(0xFF9CB380)            // Evening prayer green
    val SleepTime = Color(0xFFE8B896)       // Warm moonlight

    // Functional colors (softer than typical dark themes)
    val Error = Color(0xFFE88573)           // Gentle red glow
    val Success = Color(0xFF9CB380)         // Same as primary
    val Warning = Color(0xFFE8B896)         // Warm glow

    // Canvas texture overlay
    val CanvasTexture = Color(0xFF3C3632)   // Dark canvas for texture
    val PaperGrain = Color(0xFF9B9490)      // Light grain for contrast
}

/**
 * Gradient colors for watercolor-style accents
 */
object GhibliGradients {
    // Light theme gradients
    val LightSkyGradient = listOf(
        Color(0xFFFDF6E3),
        Color(0xFFE8D5C4),
        Color(0xFFD4C9B8)
    )

    val LightFieldGradient = listOf(
        Color(0xFFF5EDD1),
        Color(0xFFDFE4C8),
        Color(0xFFC9D7BE)
    )

    // Dark theme gradients
    val DarkSkyGradient = listOf(
        Color(0xFF1A1F1E),
        Color(0xFF222E3C),
        Color(0xFF2A3D4A)
    )

    val DarkForestGradient = listOf(
        Color(0xFF1A1F1E),
        Color(0xFF1E2926),
        Color(0xFF2A3632)
    )
}
