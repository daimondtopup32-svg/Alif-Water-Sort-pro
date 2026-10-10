package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.BackgroundTheme

@Composable
fun GameAestheticBackground(theme: BackgroundTheme, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val primary = Color(theme.primaryColor)
        val secondary = Color(theme.secondaryColor)

        // Radial backdrop glow
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(secondary, primary),
                center = Offset(size.width * 0.5f, size.height * 0.35f),
                radius = size.width * 0.9f
            )
        )

        // Subtle ambient bubbles
        drawCircle(
            color = Color.White.copy(alpha = 0.03f),
            radius = 120f,
            center = Offset(size.width * 0.2f, size.height * 0.25f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.02f),
            radius = 180f,
            center = Offset(size.width * 0.8f, size.height * 0.7f)
        )
    }
}
