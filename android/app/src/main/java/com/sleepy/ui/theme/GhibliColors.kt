package com.sleepy.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Studio Ghibli-inspired color palette with Solarized influences
 * Designed for eye comfort and aesthetic beauty
 */

// Light Theme - "Ghibli Meadow"
// Inspired by sunny fields, clear skies, and warm afternoons
object GhibliLightColors {
    // Base colors (Solarized-inspired)
    val Background = Color(0xFFFDF6E3)      // Warm cream, like aged paper
    val Surface = Color(0xFFFAF3DD)         // Slightly warmer surface
    val SurfaceVariant = Color(0xFFF5EDD1)  // Even warmer for emphasis

    // Primary colors (Ghibli greens - nature, life, growth)
    val Primary = Color(0xFF7BA05B)         // Soft sage green (grass in sunlight)
    val PrimaryVariant = Color(0xFF9CB380)  // Lighter sage
    val PrimaryDark = Color(0xFF5A7A45)     // Deeper forest

    // Secondary colors (Sky and water)
    val Secondary = Color(0xFF88B3D0)       // Gentle sky blue
    val SecondaryVariant = Color(0xFFA8C9DC) // Lighter sky
    val Accent = Color(0xFFE8A87C)          // Warm peach/sunset

    // Text colors
    val TextPrimary = Color(0xFF3C3632)     // Warm dark charcoal
    val TextSecondary = Color(0xFF6B6560)   // Medium warm gray
    val TextTertiary = Color(0xFF9B9490)    // Light warm gray

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
object GhibliDarkColors {
    // Base colors (Solarized dark-inspired)
    val Background = Color(0xFF1A1F1E)      // Deep forest at night (warmer than pure black)
    val Surface = Color(0xFF222827)         // Slightly lighter surface
    val SurfaceVariant = Color(0xFF2A302E)  // Even lighter for emphasis

    // Primary colors (Moonlit nature)
    val Primary = Color(0xFF9CB380)         // Soft moonlit green
    val PrimaryVariant = Color(0xFFB8CFA0)  // Lighter, glowing green
    val PrimaryDark = Color(0xFF7BA05B)     // Standard green

    // Secondary colors (Night sky and twilight)
    val Secondary = Color(0xFF7A9BB8)       // Twilight blue
    val SecondaryVariant = Color(0xFF95B0CA) // Lighter twilight
    val Accent = Color(0xFFE8B896)          // Warm candlelight/amber

    // Text colors (soft, not harsh white)
    val TextPrimary = Color(0xFFF5EDD1)     // Warm cream (gentle on eyes)
    val TextSecondary = Color(0xFFD4C9B8)   // Medium cream
    val TextTertiary = Color(0xFFA89F8F)    // Muted warm gray

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
