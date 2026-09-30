package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundTheme
import kotlin.math.sin

@Composable
fun LoadingScreen(
    isBengali: Boolean,
    bgTheme: BackgroundTheme,
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )
        onLoadingComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val currentProgress = progress.value
    val percent = (currentProgress * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onLoadingComplete()
            }
            .background(bgTheme.getBackgroundBrush())
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Floating ambient glowing bubbles in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val bubbles = listOf(
                Triple(0.2f, 0.25f, 24f),
                Triple(0.8f, 0.35f, 32f),
                Triple(0.15f, 0.7f, 18f),
                Triple(0.85f, 0.65f, 28f),
                Triple(0.5f, 0.15f, 20f)
            )
            bubbles.forEachIndexed { i, (rx, ry, radius) ->
                val floatY = ry * h + (sin(wavePhase + i) * 12f)
                val floatX = rx * w + (sin(wavePhase * 0.7f + i) * 8f)
                drawCircle(
                    color = Color(0x1800E5FF),
                    radius = radius,
                    center = Offset(floatX, floatY)
                )
                drawCircle(
                    color = Color(0x30FFFFFF),
                    radius = radius,
                    center = Offset(floatX, floatY),
                    style = Stroke(width = 1.5f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Animated Glowing Test Tube Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x3500E5FF), Color(0x0500E5FF), Color.Transparent)
                        )
                    )
                    .border(2.dp, Color(0x4000E5FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Test Tube Drawing
                Canvas(modifier = Modifier.size(64.dp)) {
                    val tubeW = 28f
                    val tubeH = 52f
                    val left = (size.width - tubeW) / 2f
                    val top = 6f

                    // Liquid inside tube
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF2A55), Color(0xFF00E5FF))
                        ),
                        topLeft = Offset(left + 2f, top + 14f),
                        size = Size(tubeW - 4f, tubeH - 16f),
                        cornerRadius = CornerRadius((tubeW - 4f) / 2f, (tubeW - 4f) / 2f)
                    )

                    // Tube outline
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(left, top),
                        size = Size(tubeW, tubeH),
                        cornerRadius = CornerRadius(tubeW / 2f, tubeW / 2f),
                        style = Stroke(width = 3.5f)
                    )

                    // Top lip
                    drawLine(
                        color = Color.White,
                        start = Offset(left - 4f, top),
                        end = Offset(left + tubeW + 4f, top),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Game Title
            Text(
                text = "ALIF WATER SORT PRO",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                color = Color.White
            )

            Text(
                text = if (isBengali) "আলিফ ওয়াটার সর্ট প্রো" else "Premium Color Water Puzzle",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00E5FF)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress Bar Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0x30FFFFFF))
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(9.dp))
                    .padding(3.dp)
            ) {
                // Animated fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentProgress)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF2A55),
                                    Color(0xFFFFD600),
                                    Color(0xFF00E676),
                                    Color(0xFF00E5FF)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Percentage & Dynamic Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        percent < 35 -> if (isBengali) "সম্পদ প্রস্তুত করা হচ্ছে..." else "Preparing assets..."
                        percent < 75 -> if (isBengali) "রঙিন পাজল সাজানো হচ্ছে..." else "Mixing colors..."
                        percent < 98 -> if (isBengali) "গেম রেডি হচ্ছে..." else "Readying game..."
                        else -> if (isBengali) "প্রস্তুত!" else "Ready!"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xBBFFFFFF)
                )

                Text(
                    text = "$percent%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.testTag("loading_percent")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isBengali) "👉 যেকোনো স্থানে ট্যাপ করে দ্রুত শুরু করুন" else "👉 Tap anywhere to start immediately",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0x88FFFFFF)
            )
        }
    }
}
