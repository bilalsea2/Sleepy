package com.sleepy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.sleepy.ui.theme.GhibliTheme
import kotlin.math.sin

/**
 * Main textured background component with optimized paper grain effect
 * Ghibli-inspired aesthetic with performance optimization
 */
@Composable
fun TexturedBackground(
    modifier: Modifier = Modifier,
    enablePaperGrain: Boolean = true,
    content: @Composable () -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Layer 1: Base background color (solid Ghibli color)

        // Layer 2: Optimized paper grain effect with vignette
        if (enablePaperGrain) {
            PaperGrainEffect(
                modifier = Modifier.fillMaxSize(),
                isDarkTheme = isDarkTheme
            )
        }

        // Layer 3: Content on top
        content()
    }
}

/**
 * Get gradient colors based on theme
 */
@Composable
private fun getGradientColors(baseColor: Color): List<Color> {
    // Create subtle color variations
    return listOf(
        baseColor,
        adjustColorBrightness(baseColor, 0.95f),
        adjustColorBrightness(baseColor, 0.9f)
    )
}

/**
 * Adjust color brightness
 */
private fun adjustColorBrightness(color: Color, factor: Float): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

/**
 * Draw subtle vignette effect (darker edges)
 * Makes it feel like looking at a framed painting
 */
private fun drawVignette(drawScope: DrawScope) {
    with(drawScope) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        val maxRadius = maxOf(width, height) / 2

        // Radial gradient from center to edges
        val vignetteGradient = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.05f),
                Color.Black.copy(alpha = 0.1f)
            ),
            center = Offset(centerX, centerY),
            radius = maxRadius
        )

        drawRect(
            brush = vignetteGradient,
            size = size
        )
    }
}

/**
 * Custom text with subtle shadow for depth
 * Like paint with slight dimensionality
 */
@Composable
fun PaintedText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    shadowIntensity: Float = 0.15f
) {
    Box(modifier = modifier) {
        // Shadow layer (slightly offset)
        androidx.compose.material3.Text(
            text = text,
            style = style,
            color = Color.Black.copy(alpha = shadowIntensity),
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    // Offset shadow slightly
                    drawContext.transform.translate(2f, 2f)
                }
        )

        // Main text
        androidx.compose.material3.Text(
            text = text,
            style = style,
            color = color
        )
    }
}

/**
 * Organic section separator
 * Hand-drawn style divider with slight irregularity
 */
@Composable
fun OrganicSectionDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    thickness: Float = 1.5f
) {
    OrganicDivider(
        modifier = modifier,
        color = color,
        thickness = thickness,
        waviness = 3f
    )
}
