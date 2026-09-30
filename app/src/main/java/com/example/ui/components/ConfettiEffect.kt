package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Particle(
    val xRatio: Float,
    val initialSpeedY: Float,
    val speedX: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2800, easing = LinearEasing)
        )
    }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF2A55), Color(0xFF00B0FF), Color(0xFF00E676),
            Color(0xFFFFD600), Color(0xFFA838FF), Color(0xFFFF6D00),
            Color(0xFFFF4081), Color(0xFFFFFFFF), Color(0xFF00E5FF)
        )
        val rng = Random(42)
        List(85) {
            Particle(
                xRatio = rng.nextFloat(),
                initialSpeedY = 0.7f + (rng.nextFloat() * 0.6f),
                speedX = (rng.nextFloat() - 0.5f) * 0.4f,
                size = 8f + (rng.nextFloat() * 12f),
                color = colors[rng.nextInt(colors.size)],
                rotationSpeed = (rng.nextFloat() - 0.5f) * 720f
            )
        }
    }

    val p = progress.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (item in particles) {
            val startY = -40f
            val currentY = startY + (p * h * item.initialSpeedY * 1.2f)
            val currentX = (item.xRatio * w) + (p * item.speedX * w)
            val currentRotation = p * item.rotationSpeed
            val alpha = (1f - (p * 0.8f)).coerceIn(0f, 1f)

            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                drawRect(
                    color = item.color.copy(alpha = alpha),
                    topLeft = Offset(currentX - (item.size / 2f), currentY - (item.size / 3f)),
                    size = Size(item.size, item.size * 0.65f)
                )
            }
        }
    }
}
