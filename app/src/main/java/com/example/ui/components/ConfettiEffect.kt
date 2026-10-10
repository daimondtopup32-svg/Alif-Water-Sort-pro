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
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(visible: Boolean, modifier: Modifier = Modifier) {
    if (!visible) return

    val progress = remember { Animatable(0f) }
    val particles = remember {
        val colors = listOf(
            Color(0xFFFF1744), Color(0xFF00E5FF), Color(0xFFFFD700),
            Color(0xFF00E676), Color(0xFFFF4081), Color(0xFFD500F9)
        )
        List(60) {
            Particle(
                xRatio = Random.nextFloat(),
                speed = Random.nextFloat() * 0.6f + 0.7f,
                size = Random.nextFloat() * 12f + 8f,
                color = colors.random(),
                rotationSpeed = Random.nextFloat() * 720f - 360f
            )
        }
    }

    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEach { p ->
            val y = (progress.value * h * p.speed) % h
            val x = (p.xRatio * w) + kotlin.math.sin(progress.value * 6f + p.xRatio * 10f) * 30f
            val rotation = progress.value * p.rotationSpeed

            rotate(degrees = rotation, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset(x, y),
                    size = Size(p.size, p.size * 0.6f)
                )
            }
        }
    }
}
