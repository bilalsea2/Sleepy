package com.sleepy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography system optimized for reading
 * Generous line-height and spacing for comfort
 */
val GhibliTypography = androidx.compose.material3.Typography(
    // Display - for large sleep/prayer times
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 72.sp,
        lineHeight = 88.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 56.sp,
        lineHeight = 72.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),

    // Headline - for section titles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),

    // Title - for prayer names, labels
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),

    // Body - for quotes, descriptions
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 32.sp,  // Generous 1.78 line-height
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 28.sp,  // 1.75 line-height
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,  // 1.71 line-height
        letterSpacing = 0.4.sp
    ),

    // Label - for small labels, metadata
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Material 3 Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = GhibliLightColors.Primary,
    onPrimary = GhibliLightColors.Background,
    primaryContainer = GhibliLightColors.PrimaryVariant,
    onPrimaryContainer = GhibliLightColors.TextPrimary,

    secondary = GhibliLightColors.Secondary,
    onSecondary = GhibliLightColors.Background,
    secondaryContainer = GhibliLightColors.SecondaryVariant,
    onSecondaryContainer = GhibliLightColors.TextPrimary,

    tertiary = GhibliLightColors.Accent,
    onTertiary = GhibliLightColors.Background,

    background = GhibliLightColors.Background,
    onBackground = GhibliLightColors.TextPrimary,

    surface = GhibliLightColors.Surface,
    onSurface = GhibliLightColors.TextPrimary,
    surfaceVariant = GhibliLightColors.SurfaceVariant,
    onSurfaceVariant = GhibliLightColors.TextSecondary,

    error = GhibliLightColors.Error,
    onError = GhibliLightColors.Background,

    outline = GhibliLightColors.TextTertiary,
    outlineVariant = GhibliLightColors.TextTertiary.copy(alpha = 0.3f)
)

// Material 3 Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = GhibliDarkColors.Primary,
    onPrimary = GhibliDarkColors.Background,
    primaryContainer = GhibliDarkColors.PrimaryDark,
    onPrimaryContainer = GhibliDarkColors.TextPrimary,

    secondary = GhibliDarkColors.Secondary,
    onSecondary = GhibliDarkColors.Background,
    secondaryContainer = GhibliDarkColors.SecondaryVariant,
    onSecondaryContainer = GhibliDarkColors.TextPrimary,

    tertiary = GhibliDarkColors.Accent,
    onTertiary = GhibliDarkColors.Background,

    background = GhibliDarkColors.Background,
    onBackground = GhibliDarkColors.TextPrimary,

    surface = GhibliDarkColors.Surface,
    onSurface = GhibliDarkColors.TextPrimary,
    surfaceVariant = GhibliDarkColors.SurfaceVariant,
    onSurfaceVariant = GhibliDarkColors.TextSecondary,

    error = GhibliDarkColors.Error,
    onError = GhibliDarkColors.Background,

    outline = GhibliDarkColors.TextTertiary,
    outlineVariant = GhibliDarkColors.TextTertiary.copy(alpha = 0.3f)
)

/**
 * Custom theme data that extends Material 3
 * Provides access to Ghibli-specific colors and textures
 */
data class GhibliThemeColors(
    val fajrColor: androidx.compose.ui.graphics.Color,
    val ishaColor: androidx.compose.ui.graphics.Color,
    val sleepTimeColor: androidx.compose.ui.graphics.Color,
    val canvasTexture: androidx.compose.ui.graphics.Color,
    val paperGrain: androidx.compose.ui.graphics.Color,
    val textSecondary: androidx.compose.ui.graphics.Color,
    val textTertiary: androidx.compose.ui.graphics.Color
)

// Composition local for custom theme colors
val LocalGhibliColors = staticCompositionLocalOf {
    GhibliThemeColors(
        fajrColor = GhibliLightColors.Fajr,
        ishaColor = GhibliLightColors.Isha,
        sleepTimeColor = GhibliLightColors.SleepTime,
        canvasTexture = GhibliLightColors.CanvasTexture,
        paperGrain = GhibliLightColors.PaperGrain,
        textSecondary = GhibliLightColors.TextSecondary,
        textTertiary = GhibliLightColors.TextTertiary
    )
}

/**
 * Main theme composable
 * Wraps Material 3 theme with Ghibli customizations
 */
@Composable
fun GhibliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val ghibliColors = if (darkTheme) {
        GhibliThemeColors(
            fajrColor = GhibliDarkColors.Fajr,
            ishaColor = GhibliDarkColors.Isha,
            sleepTimeColor = GhibliDarkColors.SleepTime,
            canvasTexture = GhibliDarkColors.CanvasTexture,
            paperGrain = GhibliDarkColors.PaperGrain,
            textSecondary = GhibliDarkColors.TextSecondary,
            textTertiary = GhibliDarkColors.TextTertiary
        )
    } else {
        GhibliThemeColors(
            fajrColor = GhibliLightColors.Fajr,
            ishaColor = GhibliLightColors.Isha,
            sleepTimeColor = GhibliLightColors.SleepTime,
            canvasTexture = GhibliLightColors.CanvasTexture,
            paperGrain = GhibliLightColors.PaperGrain,
            textSecondary = GhibliLightColors.TextSecondary,
            textTertiary = GhibliLightColors.TextTertiary
        )
    }

    CompositionLocalProvider(LocalGhibliColors provides ghibliColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GhibliTypography,
            content = content
        )
    }
}

// Extension to access custom colors easily
object GhibliTheme {
    val colors: GhibliThemeColors
        @Composable
        get() = LocalGhibliColors.current
}
