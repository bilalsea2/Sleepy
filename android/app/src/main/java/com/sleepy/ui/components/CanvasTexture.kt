package com.sleepy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

/**
 * Generates subtle canvas/paper texture
 * Combines multiple noise layers for organic, painted feel
 */
@Composable
fun CanvasTextureLayer(
    modifier: Modifier = Modifier,
    textureColor: Color,
    intensity: Float = 0.03f, // Very subtle by default
    seed: Int = 42
) {
    val density = LocalDensity.current

    // Pre-generate noise pattern (cached for performance)
    val noisePattern = remember(seed) {
        generateNoisePattern(seed)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Layer 1: Canvas weave texture
        // Creates subtle crosshatch pattern like linen canvas
        for (x in 0 until width.toInt() step 4) {
            for (y in 0 until height.toInt() step 4) {
                val noise = noisePattern[(x / 4 + y / 4 * (width.toInt() / 4)) % noisePattern.size]
                val alpha = (noise * intensity).coerceIn(0f, 1f)

                if (alpha > 0.001f) {
                    drawCircle(
                        color = textureColor.copy(alpha = alpha),
                        radius = 0.5f,
                        center = Offset(x.toFloat(), y.toFloat())
                    )
                }
            }
        }

        // Layer 2: Paper grain (finer detail)
        // Adds micro-level variation
        for (x in 0 until width.toInt() step 8) {
            for (y in 0 until height.toInt() step 8) {
                val index = (x / 8 + y / 8 * (width.toInt() / 8)) % noisePattern.size
                val grain = noisePattern[index]
                val alpha = (grain * intensity * 0.5f).coerceIn(0f, 1f)

                if (alpha > 0.001f) {
                    drawCircle(
                        color = textureColor.copy(alpha = alpha),
                        radius = 0.3f,
                        center = Offset(x.toFloat(), y.toFloat())
                    )
                }
            }
        }
    }
}

/**
 * Perlin-like noise pattern generator
 * Creates organic, non-repeating noise
 */
private fun generateNoisePattern(seed: Int): FloatArray {
    val random = Random(seed)
    val size = 1024 // Pattern size
    val noise = FloatArray(size)

    // Generate base random values
    for (i in 0 until size) {
        noise[i] = random.nextFloat()
    }

    // Smooth the noise (simple blur)
    val smoothed = FloatArray(size)
    for (i in 0 until size) {
        val prev = noise[(i - 1 + size) % size]
        val curr = noise[i]
        val next = noise[(i + 1) % size]
        smoothed[i] = (prev + curr + next) / 3f
    }

    return smoothed
}

/**
 * Watercolor-style gradient overlay
 * Adds subtle color variation like paint wash
 */
@Composable
fun WatercolorOverlay(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    intensity: Float = 0.05f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Create organic gradient using sine waves
        for (x in 0 until width.toInt() step 6) {
            val progress = x / width
            val waveOffset = sin(progress * 3.14f * 4) * 0.1f
            val adjustedProgress = (progress + waveOffset).coerceIn(0f, 1f)

            // Interpolate between gradient colors
            val colorIndex = (adjustedProgress * (gradientColors.size - 1)).toInt()
            val nextIndex = (colorIndex + 1).coerceAtMost(gradientColors.size - 1)
            val localProgress = (adjustedProgress * (gradientColors.size - 1)) - colorIndex

            val color = lerp Color(
                gradientColors[colorIndex],
                gradientColors[nextIndex],
                localProgress
            )

            drawLine(
                color = color.copy(alpha = intensity),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 6f
            )
        }
    }
}

/**
 * Organic divider with hand-drawn feel
 * Slight waviness and imperfection
 */
@Composable
fun OrganicDivider(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Float = 1f,
    waviness: Float = 2f
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val path = androidx.compose.ui.graphics.Path()
        path.moveTo(0f, centerY)

        // Create wavy line
        var x = 0f
        while (x < width) {
            val waveOffset = sin((x / width) * 20f) * waviness
            path.lineTo(x, centerY + waveOffset)
            x += 4f
        }

        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = thickness)
        )
    }
}

/**
 * Simple color interpolation
 */
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
