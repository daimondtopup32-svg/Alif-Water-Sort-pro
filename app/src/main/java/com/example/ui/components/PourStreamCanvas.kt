package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.LiquidColor

@Composable
fun PourStreamCanvas(
    isPouring: Boolean,
    pourColor: LiquidColor?,
    fromOffset: Offset,
    toOffset: Offset,
    modifier: Modifier = Modifier
) {
    if (!isPouring || pourColor == null) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val streamPath = Path().apply {
            moveTo(fromOffset.x, fromOffset.y)
            val controlX = (fromOffset.x + toOffset.x) / 2f
            val controlY = kotlin.math.min(fromOffset.y, toOffset.y) - 60f
            quadraticTo(controlX, controlY, toOffset.x, toOffset.y)
        }

        val brush = Brush.linearGradient(
            colors = listOf(pourColor.color, pourColor.color.copy(alpha = 0.8f)),
            start = fromOffset,
            end = toOffset
        )

        drawPath(
            path = streamPath,
            brush = brush,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )

        // Center bright highlight
        drawPath(
            path = streamPath,
            color = Color.White.copy(alpha = 0.4f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
