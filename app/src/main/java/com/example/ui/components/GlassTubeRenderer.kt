package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.Tube
import com.example.model.TubeSkin

@Composable
fun GlassTubeRenderer(
    tube: Tube,
    isSelected: Boolean,
    isHintTarget: Boolean,
    tubeSkin: TubeSkin,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val liftAnim = remember { Animatable(0f) }
    val tiltAnim = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            liftAnim.animateTo(-45f, tween(180, easing = FastOutSlowInEasing))
        } else {
            liftAnim.animateTo(0f, tween(180, easing = FastOutSlowInEasing))
            tiltAnim.animateTo(0f, tween(120))
        }
    }

    Box(
        modifier = modifier
            .width(62.dp)
            .height(180.dp)
            .offset { IntOffset(0, liftAnim.value.dp.roundToPx()) }
            .graphicsLayer {
                rotationZ = tiltAnim.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag("tube_${tube.id}"),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 6.dp)) {
            val tubeWidth = size.width
            val tubeHeight = size.height
            val cornerRadius = tubeSkin.cornerRadius.dp.toPx()

            val tubePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(Offset.Zero, Size(tubeWidth, tubeHeight)),
                        bottomLeft = CornerRadius(cornerRadius, cornerRadius),
                        bottomRight = CornerRadius(cornerRadius, cornerRadius),
                        topLeft = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                        topRight = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                )
            }

            // Draw Glass Background Tint
            drawPath(
                path = tubePath,
                color = Color(tubeSkin.glassTint)
            )

            // Draw Liquid Segments
            val capacity = tube.capacity
            val segmentHeight = (tubeHeight - 14.dp.toPx()) / capacity
            val bottomPadding = 8.dp.toPx()

            tube.segments.forEachIndexed { index, liquidColor ->
                val topY = tubeHeight - bottomPadding - ((index + 1) * segmentHeight)
                val isBottom = index == 0
                val isTop = index == tube.segments.size - 1

                val segPath = Path().apply {
                    if (isBottom) {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset(3.dp.toPx(), topY), Size(tubeWidth - 6.dp.toPx(), segmentHeight)),
                                bottomLeft = CornerRadius(cornerRadius - 2.dp.toPx(), cornerRadius - 2.dp.toPx()),
                                bottomRight = CornerRadius(cornerRadius - 2.dp.toPx(), cornerRadius - 2.dp.toPx()),
                                topLeft = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                topRight = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        )
                    } else {
                        addRect(Rect(Offset(3.dp.toPx(), topY), Size(tubeWidth - 6.dp.toPx(), segmentHeight)))
                    }
                }

                // Liquid fill with specular gradient
                val liquidBrush = Brush.verticalGradient(
                    colors = listOf(
                        liquidColor.color.copy(alpha = 0.95f),
                        liquidColor.color.copy(alpha = 0.75f)
                    ),
                    startY = topY,
                    endY = topY + segmentHeight
                )
                drawPath(path = segPath, brush = liquidBrush)

                // Liquid surface sheen
                if (isTop) {
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(tubeWidth / 2f, topY + 2.dp.toPx()),
                            radius = tubeWidth / 3f
                        ),
                        topLeft = Offset(4.dp.toPx(), topY - 2.dp.toPx()),
                        size = Size(tubeWidth - 8.dp.toPx(), 6.dp.toPx())
                    )
                }
            }

            // Tube rim
            drawLine(
                color = Color.White.copy(alpha = 0.7f),
                start = Offset(0f, 2.dp.toPx()),
                end = Offset(tubeWidth, 2.dp.toPx()),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Outer Glass Stroke
            val strokeColor = when {
                isSelected -> Color(0xFF00E5FF)
                isHintTarget -> Color(0xFFFFD700)
                tube.isCompleted -> Color(0xFF00E676)
                else -> Color.White.copy(alpha = 0.35f)
            }
            val strokeWidth = if (isSelected || isHintTarget) 3.5.dp.toPx() else 2.dp.toPx()

            drawPath(
                path = tubePath,
                color = strokeColor,
                style = Stroke(width = strokeWidth)
            )

            // Glass Reflection Highlight Line
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                    startY = 10f,
                    endY = tubeHeight * 0.75f
                ),
                start = Offset(8.dp.toPx(), 10.dp.toPx()),
                end = Offset(8.dp.toPx(), tubeHeight * 0.75f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
