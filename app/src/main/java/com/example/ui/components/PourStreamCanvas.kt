package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.LiquidColor
import kotlin.math.sin

@Composable
fun PourStreamOverlay(
    isPouring: Boolean,
    streamCenterX: Float?,
    streamTopY: Float?,
    streamBottomY: Float?,
    color: LiquidColor?,
    progress: Float,
    modifier: Modifier = Modifier
) {
    if (!isPouring || streamCenterX == null || streamTopY == null || streamBottomY == null || color == null) return
    // Show stream while the tilted tube is pouring (from 20% to 78% of animation)
    if (progress < 0.20f || progress > 0.78f) return

    val pourPhase = ((progress - 0.20f) / 0.58f).coerceIn(0f, 1f)
    val streamAlpha = when {
        pourPhase < 0.10f -> (pourPhase / 0.10f)
        pourPhase > 0.90f -> ((1f - pourPhase) / 0.10f)
        else -> 1f
    }.coerceIn(0f, 1f)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Stream width matching the reference image (~10dp / 28px)
        val streamWidth = 26f * streamAlpha
        val height = (streamBottomY - streamTopY).coerceAtLeast(8f)

        // 1. Subtle soft glow around stream
        drawRect(
            color = color.glowColor.copy(alpha = 0.5f * streamAlpha),
            topLeft = Offset(streamCenterX - (streamWidth / 2f) - 3f, streamTopY),
            size = Size(streamWidth + 6f, height)
        )

        // 2. Solid vertical fluid column (just like in the reference image)
        val streamBrush = Brush.verticalGradient(
            colors = listOf(color.primaryColor, color.gradientColor),
            startY = streamTopY,
            endY = streamBottomY
        )
        drawRect(
            brush = streamBrush,
            topLeft = Offset(streamCenterX - (streamWidth / 2f), streamTopY),
            size = Size(streamWidth, height)
        )

        // 3. Central specular fluid gloss highlight
        drawRect(
            color = Color.White.copy(alpha = 0.55f * streamAlpha),
            topLeft = Offset(streamCenterX - 2f, streamTopY),
            size = Size(4f, height)
        )

        // 4. Wavy splash meniscus at the liquid surface
        val splashWing = 22f
        val splashPeak = 5f + sin(pourPhase * 16f) * 2.5f
        val wavePath = Path().apply {
            moveTo(streamCenterX - splashWing, streamBottomY)
            quadraticTo(
                streamCenterX - (splashWing * 0.5f),
                streamBottomY - splashPeak,
                streamCenterX - (streamWidth / 2f),
                streamBottomY
            )
            lineTo(streamCenterX + (streamWidth / 2f), streamBottomY)
            quadraticTo(
                streamCenterX + (splashWing * 0.5f),
                streamBottomY - splashPeak,
                streamCenterX + splashWing,
                streamBottomY
            )
            close()
        }
        drawPath(wavePath, color = color.primaryColor.copy(alpha = 0.95f * streamAlpha))

        // 5. Distinct circular splash droplets floating inside the tube (matching images (16)_1.jpeg)
        val dropletOffsets = listOf(
            Offset(-14f, -16f) to 3.2f,
            Offset(15f, -22f) to 2.8f,
            Offset(-8f, -32f) to 3.5f,
            Offset(10f, -14f) to 2.5f,
            Offset(18f, -28f) to 3.0f
        )

        dropletOffsets.forEachIndexed { i, (baseOffset, radius) ->
            val bobbing = sin(pourPhase * 20f + i) * 3f
            val dropCenter = Offset(
                streamCenterX + baseOffset.x,
                streamBottomY + baseOffset.y + bobbing
            )

            // Droplet
            drawCircle(
                color = color.primaryColor.copy(alpha = 0.9f * streamAlpha),
                radius = radius,
                center = dropCenter
            )
            // Tiny gloss glint
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * streamAlpha),
                radius = radius * 0.4f,
                center = Offset(dropCenter.x - (radius * 0.3f), dropCenter.y - (radius * 0.3f))
            )
        }
    }
}
