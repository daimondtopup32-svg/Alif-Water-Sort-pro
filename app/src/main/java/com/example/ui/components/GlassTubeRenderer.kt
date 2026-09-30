package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.LiquidColor
import com.example.model.Tube
import com.example.model.TubeSkin
import kotlin.math.sin

@Composable
fun GlassTubeItem(
    tube: Tube,
    isSelected: Boolean,
    isShaking: Boolean,
    isHintSource: Boolean,
    isHintTarget: Boolean,
    tubeSkin: TubeSkin,
    tubeWidth: Dp = 70.dp,
    tubeHeight: Dp = 210.dp,
    tiltAngle: Float = 0f,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    isPouringSource: Boolean = false,
    isPouringTarget: Boolean = false,
    pouringColor: LiquidColor? = null,
    pouringUnits: Int = 0,
    pourProgress: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Lift animation on selection
    val liftAnim = remember { Animatable(0f) }
    LaunchedEffect(isSelected) {
        liftAnim.animateTo(
            targetValue = if (isSelected) -28f else 0f,
            animationSpec = tween(durationMillis = 180, easing = LinearEasing)
        )
    }

    // Shake animation
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isShaking) {
        if (isShaking) {
            shakeAnim.animateTo(-12f, tween(50))
            shakeAnim.animateTo(12f, tween(50))
            shakeAnim.animateTo(-8f, tween(50))
            shakeAnim.animateTo(8f, tween(50))
            shakeAnim.animateTo(0f, tween(50))
        }
    }

    // Pulse animation for hints
    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Bubble floating transition
    val bubblePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble"
    )

    val currentLift = liftAnim.value
    val currentShake = shakeAnim.value

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        label = "pressScale"
    )

    Box(
        modifier = modifier
            .width(tubeWidth)
            .height(tubeHeight)
            .offset {
                IntOffset(
                    x = (offsetX.toPx() + currentShake).toInt(),
                    y = (offsetY.toPx() + currentLift).toInt()
                )
            }
            .graphicsLayer {
                rotationZ = tiltAngle
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.08f)
                val baseScale = if (isHintSource || isHintTarget) pulseScale else 1f
                scaleX = baseScale * pressScale
                scaleY = baseScale * pressScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTube(
                tube = tube,
                tubeSkin = tubeSkin,
                isSelected = isSelected,
                isHintSource = isHintSource,
                isHintTarget = isHintTarget,
                isPouringSource = isPouringSource,
                isPouringTarget = isPouringTarget,
                pouringColor = pouringColor,
                pouringUnits = pouringUnits,
                pourProgress = pourProgress,
                bubblePhase = bubblePhase
            )
        }
    }
}

private fun DrawScope.drawTube(
    tube: Tube,
    tubeSkin: TubeSkin,
    isSelected: Boolean,
    isHintSource: Boolean,
    isHintTarget: Boolean,
    isPouringSource: Boolean,
    isPouringTarget: Boolean,
    pouringColor: LiquidColor?,
    pouringUnits: Int,
    pourProgress: Float,
    bubblePhase: Float
) {
    val w = size.width
    val h = size.height

    val paddingX = 7f
    val tubeWidth = w - (paddingX * 2)
    val tubeLeft = paddingX
    val tubeRight = tubeLeft + tubeWidth
    val tubeTop = 18f
    val tubeBottom = h - 10f
    val cornerRadius = tubeWidth / 2f

    // Tube container Path
    val tubePath = Path()
    when (tubeSkin) {
        TubeSkin.CLASSIC, TubeSkin.NEON -> {
            // Round bottom test tube
            tubePath.moveTo(tubeLeft, tubeTop)
            tubePath.lineTo(tubeLeft, tubeBottom - cornerRadius)
            tubePath.arcTo(
                rect = Rect(tubeLeft, tubeBottom - (cornerRadius * 2), tubeRight, tubeBottom),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            tubePath.lineTo(tubeRight, tubeTop)
            tubePath.close()
        }
        TubeSkin.BEAKER -> {
            // Beaker with slightly rounded corners at bottom
            val beakerRadius = 12f
            tubePath.moveTo(tubeLeft, tubeTop)
            tubePath.lineTo(tubeLeft, tubeBottom - beakerRadius)
            tubePath.quadraticTo(tubeLeft, tubeBottom, tubeLeft + beakerRadius, tubeBottom)
            tubePath.lineTo(tubeRight - beakerRadius, tubeBottom)
            tubePath.quadraticTo(tubeRight, tubeBottom, tubeRight, tubeBottom - beakerRadius)
            tubePath.lineTo(tubeRight, tubeTop)
            tubePath.close()
        }
        TubeSkin.FLASK -> {
            // Flask: narrower top neck, triangular flared bottom
            val neckLeft = tubeLeft + (tubeWidth * 0.22f)
            val neckRight = tubeRight - (tubeWidth * 0.22f)
            val neckBottom = tubeTop + (h * 0.28f)
            val baseRadius = 16f

            tubePath.moveTo(neckLeft, tubeTop)
            tubePath.lineTo(neckLeft, neckBottom)
            tubePath.lineTo(tubeLeft, tubeBottom - baseRadius)
            tubePath.quadraticTo(tubeLeft, tubeBottom, tubeLeft + baseRadius, tubeBottom)
            tubePath.lineTo(tubeRight - baseRadius, tubeBottom)
            tubePath.quadraticTo(tubeRight, tubeBottom, tubeRight, tubeBottom - baseRadius)
            tubePath.lineTo(neckRight, neckBottom)
            tubePath.lineTo(neckRight, tubeTop)
            tubePath.close()
        }
    }

    // 1. Draw subtle background glow if selected or hinted
    if (isSelected || isHintSource) {
        drawRoundRect(
            color = Color(0x6640C4FF),
            topLeft = Offset(tubeLeft - 6f, tubeTop - 6f),
            size = Size(tubeWidth + 12f, (tubeBottom - tubeTop) + 12f),
            cornerRadius = CornerRadius(cornerRadius + 6f, cornerRadius + 6f)
        )
    } else if (isHintTarget) {
        drawRoundRect(
            color = Color(0x6600E676),
            topLeft = Offset(tubeLeft - 6f, tubeTop - 6f),
            size = Size(tubeWidth + 12f, (tubeBottom - tubeTop) + 12f),
            cornerRadius = CornerRadius(cornerRadius + 6f, cornerRadius + 6f)
        )
    }

    // 2. Draw back glass body
    drawPath(
        path = tubePath,
        color = Color(0x18FFFFFF)
    )

    // 3. Draw liquids inside clipped tube path
    clipPath(tubePath) {
        val totalCapacity = tube.capacity.toFloat()
        val totalUsableHeight = tubeBottom - tubeTop

        // Calculate liquid heights
        // Base liquids
        val liquids = tube.liquids
        val count = liquids.size

        // If this tube is source pouring: top units diminish by fluidFlowProgress
        // If this tube is target pouring: top units rise by fluidFlowProgress
        val fluidFlowProgress = when {
            pourProgress < 0.20f -> 0f
            pourProgress > 0.78f -> 1f
            else -> (pourProgress - 0.20f) / 0.58f
        }

        var visualLiquidHeightRatio = count / totalCapacity

        if (isPouringSource && pouringUnits > 0) {
            visualLiquidHeightRatio = (count - (pouringUnits * fluidFlowProgress)) / totalCapacity
        } else if (isPouringTarget && pouringUnits > 0) {
            visualLiquidHeightRatio = (count + (pouringUnits * fluidFlowProgress)) / totalCapacity
        }

        val slotHeight = totalUsableHeight / totalCapacity

        // Group contiguous slots of identical color so they render as a single continuous liquid body!
        // When all 4 slots are the same color, this completely eliminates internal dividing lines ("দাঁতগুলা")!
        data class ContiguousBlock(
            val color: LiquidColor,
            val startIndex: Int,
            val count: Int
        )

        val blocks = mutableListOf<ContiguousBlock>()
        if (liquids.isNotEmpty()) {
            var curColor = liquids[0]
            var startIdx = 0
            var countInBlock = 1
            for (i in 1 until liquids.size) {
                if (liquids[i].id == curColor.id) {
                    countInBlock++
                } else {
                    blocks.add(ContiguousBlock(curColor, startIdx, countInBlock))
                    curColor = liquids[i]
                    startIdx = i
                    countInBlock = 1
                }
            }
            blocks.add(ContiguousBlock(curColor, startIdx, countInBlock))
        }

        // Draw each colored block from bottom to top as a unified, seamless liquid column
        for (b in blocks.indices) {
            val block = blocks[b]
            val isTopBlock = (b == blocks.lastIndex)

            // Calculate bottom and top Y for this entire continuous block
            val blockBottomY = tubeBottom - (block.startIndex * slotHeight)
            var blockTopY = tubeBottom - ((block.startIndex + block.count) * slotHeight)

            if (isPouringSource && isTopBlock && pouringUnits > 0) {
                // Shrinking top layer smoothly
                val remainingUnits = (block.count - (pouringUnits * fluidFlowProgress)).coerceAtLeast(0f)
                blockTopY = blockBottomY - (remainingUnits * slotHeight)
            }

            if (blockTopY < blockBottomY) {
                val blockH = blockBottomY - blockTopY
                // Single unified gradient across the entire unified liquid column
                // When 4 slots are of the same color, this spans the entire height with ZERO internal lines or seams!
                val brush = Brush.verticalGradient(
                    colors = listOf(block.color.primaryColor, block.color.gradientColor),
                    startY = blockTopY,
                    endY = blockBottomY
                )

                drawRect(
                    brush = brush,
                    topLeft = Offset(tubeLeft - 2f, blockTopY),
                    size = Size(tubeWidth + 4f, blockH + 1f)
                )

                // Soft meniscus wave highlight at the top of the fluid surface
                if (isTopBlock) {
                    val waveCenter = (tubeLeft + tubeRight) / 2f
                    val wavePath = Path().apply {
                        moveTo(tubeLeft, blockTopY)
                        quadraticTo(waveCenter, blockTopY + 4f, tubeRight, blockTopY)
                        lineTo(tubeRight, blockTopY + 2f)
                        quadraticTo(waveCenter, blockTopY + 6f, tubeLeft, blockTopY + 2f)
                        close()
                    }
                    drawPath(wavePath, Color(0x55FFFFFF))
                }

                // Effervescent bubbles smoothly rising through the fluid
                val bubbleCount = (block.count * 2).coerceAtLeast(2)
                for (k in 0 until bubbleCount) {
                    val phaseOffset = k.toFloat() / bubbleCount
                    val bY = blockBottomY - (((bubblePhase + phaseOffset) * blockH) % blockH)
                    val xRatio = 0.30f + (k % 3) * 0.20f
                    val bX = tubeLeft + (tubeWidth * xRatio) + (sin((bubblePhase + k) * 6f) * 3f)
                    val bRadius = if (k % 2 == 0) 2.2f else 1.6f
                    val bAlpha = if (k % 2 == 0) 0.55f else 0.40f

                    drawCircle(
                        color = block.color.bubbleColor.copy(alpha = bAlpha),
                        radius = bRadius,
                        center = Offset(bX, bY)
                    )
                }
            }
        }

        // If target pouring: draw the incoming liquid layer rising!
        if (isPouringTarget && pouringColor != null && pouringUnits > 0 && fluidFlowProgress > 0f) {
            val risingUnits = pouringUnits * fluidFlowProgress
            val startY = tubeBottom - (count * slotHeight)
            val currentTopY = startY - (risingUnits * slotHeight)

            val incomingBrush = Brush.verticalGradient(
                colors = listOf(pouringColor.primaryColor, pouringColor.gradientColor),
                startY = currentTopY,
                endY = startY
            )
            drawRect(
                brush = incomingBrush,
                topLeft = Offset(tubeLeft - 2f, currentTopY),
                size = Size(tubeWidth + 4f, (startY - currentTopY) + 1f)
            )

            // Splash ripples at the surface
            drawCircle(
                color = pouringColor.bubbleColor.copy(alpha = 0.7f),
                radius = 4f + (sin(pourProgress * 15f) * 2f),
                center = Offset((tubeLeft + tubeRight) / 2f, currentTopY)
            )
        }

        // Inner vertical specular glass gloss reflections (left side & right side)
        val glossBrushLeft = Brush.horizontalGradient(
            colors = listOf(Color(0x50FFFFFF), Color(0x10FFFFFF), Color.Transparent),
            startX = tubeLeft,
            endX = tubeLeft + (tubeWidth * 0.25f)
        )
        drawRect(
            brush = glossBrushLeft,
            topLeft = Offset(tubeLeft, tubeTop),
            size = Size(tubeWidth * 0.25f, totalUsableHeight)
        )

        val glossBrushRight = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color(0x15FFFFFF), Color(0x35FFFFFF)),
            startX = tubeRight - (tubeWidth * 0.2f),
            endX = tubeRight
        )
        drawRect(
            brush = glossBrushRight,
            topLeft = Offset(tubeRight - (tubeWidth * 0.2f), tubeTop),
            size = Size(tubeWidth * 0.2f, totalUsableHeight)
        )
    }

    // 4. Outer Glass Wall Outline & Rim
    val strokeWidth = if (tubeSkin == TubeSkin.NEON) 4.5f else 3.5f
    val outlineColor = when {
        tube.isCompleted -> Color(0xFFFFD54F)
        tubeSkin == TubeSkin.NEON -> Color(0xFF00E5FF)
        else -> Color.White
    }

    drawPath(
        path = tubePath,
        color = outlineColor,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Top Rim/Lip of the Test Tube (Crisp rim)
    val lipHeight = 7f
    val lipExtend = 5f
    val lipRect = RoundRect(
        rect = Rect(
            left = tubeLeft - lipExtend,
            top = tubeTop - (lipHeight / 2f),
            right = tubeRight + lipExtend,
            bottom = tubeTop + (lipHeight / 2f)
        ),
        cornerRadius = CornerRadius(3.5f, 3.5f)
    )
    val lipPath = Path().apply { addRoundRect(lipRect) }
    drawPath(lipPath, color = outlineColor)

    // Completed tube cork / gold seal: seals the top when all 4 units are the same color and tube is finished!
    if (tube.isCompleted) {
        val corkTop = tubeTop - 12f
        val corkBottom = tubeTop + 2f
        val corkWidth = tubeWidth * 0.76f
        val corkLeft = (tubeLeft + tubeRight - corkWidth) / 2f
        val corkRight = corkLeft + corkWidth

        val corkPath = Path().apply {
            moveTo(corkLeft + 2f, corkBottom)
            lineTo(corkLeft - 2f, corkTop)
            lineTo(corkRight + 2f, corkTop)
            lineTo(corkRight - 2f, corkBottom)
            close()
        }
        val corkBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300)),
            startY = corkTop,
            endY = corkBottom
        )
        drawPath(corkPath, brush = corkBrush)
        drawPath(corkPath, color = Color(0xFFFFECB3), style = Stroke(width = 1.5f))
    }

    // Beaker volume tick lines:
    // When the tube is full of one color or completed, those tick lines ("দাঁতগুলা") are completely hidden!
    val isOneColorFull = (tube.liquids.size == tube.capacity && tube.hasSingleColor) || tube.isCompleted
    if (tubeSkin == TubeSkin.BEAKER && !isOneColorFull) {
        val totalUsableHeight = tubeBottom - tubeTop
        val tickCount = 4
        for (t in 1..tickCount) {
            val tickY = tubeBottom - (t * (totalUsableHeight / (tube.capacity + 0.5f)))
            drawLine(
                color = Color(0x88FFFFFF),
                start = Offset(tubeLeft + 4f, tickY),
                end = Offset(tubeLeft + 16f, tickY),
                strokeWidth = 2f
            )
        }
    }
}
