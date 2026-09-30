package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.BackgroundTheme

@Composable
fun GameAestheticBackground(
    theme: BackgroundTheme,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.getBackgroundBrush())
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Top-Right Soft Ambient Glow Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(theme.orbColor, Color.Transparent),
                    center = Offset(w * 0.82f, h * 0.16f),
                    radius = (w * 0.65f) * pulse
                ),
                center = Offset(w * 0.82f, h * 0.16f),
                radius = (w * 0.65f) * pulse
            )

            // 2. Bottom-Left Soft Ambient Glow Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(theme.accentGlow.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(w * 0.18f, h * 0.82f),
                    radius = (w * 0.55f) * (2f - pulse)
                ),
                center = Offset(w * 0.18f, h * 0.82f),
                radius = (w * 0.55f) * (2f - pulse)
            )

            // 3. Subtle floating ambient bubbles/stars
            val particlePositions = listOf(
                Offset(w * 0.12f, h * 0.25f) to 2.2f,
                Offset(w * 0.88f, h * 0.40f) to 1.8f,
                Offset(w * 0.25f, h * 0.55f) to 1.5f,
                Offset(w * 0.78f, h * 0.68f) to 2.0f,
                Offset(w * 0.16f, h * 0.75f) to 1.6f,
                Offset(w * 0.82f, h * 0.85f) to 2.4f
            )

            for ((pos, r) in particlePositions) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.22f),
                    radius = r * pulse,
                    center = pos
                )
            }
        }

        content()
    }
}
