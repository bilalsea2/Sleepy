package com.sleepy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs
import kotlin.random.Random

/**
 * Optimized paper grain effect for Ghibli aesthetic
 * Uses sparse random points instead of nested loops for performance
 */
@Composable
fun PaperGrainEffect(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    // Pre-generate random grain points (memoized)
    val grainPoints = remember {
        generateGrainPoints(density = 0.001f) // 0.1% density for subtle effect
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw vignette (darker edges)
        drawVignette(isDarkTheme)

        // Draw paper grain
        drawPaperGrain(grainPoints, isDarkTheme)

        // Draw subtle texture streaks (paper fibers)
        drawPaperFibers(isDarkTheme)
    }
}

/**
 * Generate random grain points once (performance optimization)
 */
private fun generateGrainPoints(density: Float): List<GrainPoint> {
    val points = mutableListOf<GrainPoint>()
    val seed = 42 // Fixed seed for consistent grain pattern
    val random = Random(seed)

    // Generate sparse random points
    repeat(2000) { // Fixed number of grain points
        points.add(
            GrainPoint(
                x = random.nextFloat(),
                y = random.nextFloat(),
                intensity = random.nextFloat() * 0.4f // 0-40% opacity
            )
        )
    }

    return points
}

private data class GrainPoint(
    val x: Float, // 0-1 normalized position
    val y: Float, // 0-1 normalized position
    val intensity: Float // 0-1 opacity
)

/**
 * Draw vignette effect (darker edges, lighter center)
 */
private fun DrawScope.drawVignette(isDarkTheme: Boolean) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = maxOf(size.width, size.height) * 0.7f

    val vignetteColor = if (isDarkTheme) {
        Color.Black.copy(alpha = 0.35f)
    } else {
        Color(0xFF8B7E70).copy(alpha = 0.15f)
    }

    // Draw radial gradient manually (outer edge darker)
    val steps = 100
    for (i in 0 until steps) {
        val progress = i.toFloat() / steps
        val currentRadius = radius * (1f - progress)
        val alpha = vignetteColor.alpha * progress * progress // Quadratic falloff

        drawCircle(
            color = vignetteColor.copy(alpha = alpha),
            radius = size.width,
            center = Offset(centerX, centerY),
            blendMode = BlendMode.Multiply
        )
    }
}

/**
 * Draw paper grain using pre-generated points
 */
private fun DrawScope.drawPaperGrain(points: List<GrainPoint>, isDarkTheme: Boolean) {
    val grainColor = if (isDarkTheme) {
        Color(0xFFE8DCC8) // Light grain on dark
    } else {
        Color(0xFF5A4F45) // Dark grain on light
    }

    points.forEach { point ->
        val x = point.x * size.width
        val y = point.y * size.height

        // Draw small grain dots (1-2px)
        drawCircle(
            color = grainColor.copy(alpha = point.intensity * 0.3f),
            radius = 1.5f,
            center = Offset(x, y),
            blendMode = if (isDarkTheme) BlendMode.Screen else BlendMode.Multiply
        )
    }
}

/**
 * Draw subtle paper fiber streaks (horizontal texture)
 */
private fun DrawScope.drawPaperFibers(isDarkTheme: Boolean) {
    val fiberColor = if (isDarkTheme) {
        Color(0xFFE8DCC8).copy(alpha = 0.03f)
    } else {
        Color(0xFF8B7E70).copy(alpha = 0.05f)
    }

    val random = Random(123) // Fixed seed
    val fiberCount = 50 // Sparse fibers

    repeat(fiberCount) {
        val y = random.nextFloat() * size.height
        val startX = random.nextFloat() * size.width * 0.3f
        val endX = startX + (random.nextFloat() * size.width * 0.7f)

        // Draw thin horizontal line (paper fiber)
        drawLine(
            color = fiberColor,
            start = Offset(startX, y),
            end = Offset(endX, y),
            strokeWidth = 0.5f
        )
    }
}

/**
 * Alternate: Noise-based paper texture (even more optimized)
 */
@Composable
fun NoiseBasedPaperTexture(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val noiseStrength = if (isDarkTheme) 0.08f else 0.06f

        // Simple Perlin-like noise using sine waves
        for (y in 0 until size.height.toInt() step 4) {
            for (x in 0 until size.width.toInt() step 4) {
                val noise = simpleNoise(x.toFloat(), y.toFloat())
                val alpha = abs(noise) * noiseStrength

                val color = if (isDarkTheme) {
                    Color.White.copy(alpha = alpha)
                } else {
                    Color.Black.copy(alpha = alpha)
                }

                drawCircle(
                    color = color,
                    radius = 2f,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }
}

/**
 * Simple noise function (fast approximation)
 */
private fun simpleNoise(x: Float, y: Float): Float {
    val n = kotlin.math.sin(x * 0.01f) * kotlin.math.cos(y * 0.01f) +
            kotlin.math.sin(x * 0.02f + y * 0.03f) * 0.5f
    return n.coerceIn(-1f, 1f)
}
