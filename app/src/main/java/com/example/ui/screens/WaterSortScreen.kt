package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ads.AdManager
import com.example.ads.BannerAdView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.Tube
import com.example.ui.components.ConfettiCelebration
import com.example.ui.components.GameAestheticBackground
import com.example.ui.components.GlassTubeItem
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.PourStreamOverlay
import com.example.ui.components.ThemeSettingsSheet
import com.example.ui.components.VictoryDialog
import com.example.ui.components.WatchAdForTubeDialog
import com.example.viewmodel.GameViewModel

@Composable
fun WaterSortScreen(
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToMenu() }

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var showLevelSelect by remember { mutableStateOf(false) }
    var showThemeSettings by remember { mutableStateOf(false) }

    // Stable static tube slot coordinates
    val tubeBoundsMap = remember { mutableStateMapOf<Int, Offset>() }

    // Pouring animated progress (0f -> 1f) - Fast, fluid 850ms speed
    val pourAnim = remember { Animatable(0f) }
    LaunchedEffect(state.isPouring) {
        if (state.isPouring) {
            pourAnim.snapTo(0f)
            pourAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = LinearEasing)
            )
        } else {
            pourAnim.snapTo(0f)
        }
    }

    val currentPourProgress = pourAnim.value

    // Dynamic large tube size according to tube count for prominent, beautiful view
    val tubeWidth = when {
        state.tubes.size <= 4 -> 66.dp
        state.tubes.size <= 6 -> 62.dp
        state.tubes.size <= 10 -> 54.dp
        state.tubes.size <= 13 -> 48.dp
        else -> 42.dp
    }
    val tubeHeight = when {
        state.tubes.size <= 4 -> 205.dp
        state.tubes.size <= 6 -> 195.dp
        state.tubes.size <= 10 -> 175.dp
        state.tubes.size <= 13 -> 150.dp
        else -> 135.dp
    }

    GameAestheticBackground(
        theme = state.bgTheme,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Header Bar
            TopGameBar(
                currentLevel = state.currentLevel,
                coins = state.coins,
                moves = state.movesCount,
                isBengali = state.isBengali,
                onBackToMenu = onBackToMenu,
                onOpenLevelSelect = { showLevelSelect = true },
                onOpenSettings = { showThemeSettings = true }
            )

            // 2. Stuck warning banner if no valid moves remain
            AnimatedVisibility(
                visible = state.isStuck,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                StuckWarningBanner(
                    isBengali = state.isBengali,
                    hasEmptyTube = state.tubes.any { it.isEmpty },
                    onRestart = { viewModel.restartLevel() },
                    onAddTube = { viewModel.addExtraTube() }
                )
            }

            Spacer(modifier = Modifier.weight(0.12f))

            // 3. Central Game Board (Large Glass Tubes)
            TubesGameBoard(
                tubes = state.tubes,
                selectedTubeIndex = state.selectedTubeIndex,
                shakingTubeIndex = state.shakingTubeIndex,
                hintSourceIndex = state.hintSourceIndex,
                hintTargetIndex = state.hintTargetIndex,
                isPouring = state.isPouring,
                pouringSourceIndex = state.pouringSourceIndex,
                pouringTargetIndex = state.pouringTargetIndex,
                pouringColor = state.pouringColor,
                pouringUnits = state.pouringUnits,
                pourProgress = currentPourProgress,
                tubeSkin = state.tubeSkin,
                tubeWidth = tubeWidth,
                tubeHeight = tubeHeight,
                tubeBoundsMap = tubeBoundsMap,
                onTubeClicked = { viewModel.onTubeClicked(it) },
                onTubePositioned = { id, offset -> tubeBoundsMap[id] = offset },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(0.18f))

            // 4. Bottom Controls Bar
            BottomActionBar(
                coins = state.coins,
                extraTubesCount = state.extraTubesCount,
                isBengali = state.isBengali,
                onRestart = { viewModel.restartLevel() },
                onHint = { viewModel.requestHint() },
                onAddTube = { viewModel.addExtraTube() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Google AdMob Banner Ad View
            BannerAdView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }

        // Overlay: Solid vertical fluid stream pouring directly into destination glass
        // Only rendered during active pour so it never captures touches while idle
        if (state.isPouring) {
            val srcPos = state.pouringSourceIndex?.let { tubeBoundsMap[it] }
            val dstPos = state.pouringTargetIndex?.let { tubeBoundsMap[it] }
            if (srcPos != null && dstPos != null) {
                val density = LocalDensity.current
                val halfHeightPx = with(density) { (tubeHeight / 2f).toPx() }
                val dstRimY = dstPos.y - halfHeightPx + 18f
                // Starts exactly at the lower lip of the high hovering tube (70dp above rim, never touches other tubes)
                val streamTopY = dstRimY - with(density) { 70.dp.toPx() }

                // Calculate current rising liquid surface inside destination tube
                val dstTube = state.tubes.getOrNull(state.pouringTargetIndex ?: -1)
                val baseCount = dstTube?.liquids?.size ?: 0
                val pourPhase = ((currentPourProgress - 0.20f) / 0.58f).coerceIn(0f, 1f)
                val currentUnits = (baseCount + (state.pouringUnits * pourPhase)).coerceAtMost(4f)

                val usableHeight = (with(density) { tubeHeight.toPx() } - 10f) - 18f
                val unitHeight = usableHeight / 4f
                val tubeBottomY = dstPos.y + halfHeightPx - 10f
                val streamBottomY = tubeBottomY - (currentUnits * unitHeight)

                PourStreamOverlay(
                    isPouring = true,
                    streamCenterX = dstPos.x,
                    streamTopY = streamTopY,
                    streamBottomY = streamBottomY,
                    color = state.pouringColor,
                    progress = currentPourProgress,
                    modifier = Modifier.zIndex(300f)
                )
            }
        }

        // Celebration Confetti
        ConfettiCelebration(isActive = state.isWon)

        // Victory Dialog
        VictoryDialog(
            show = state.showVictoryDialog,
            levelNumber = state.currentLevel,
            stars = state.starsEarned,
            coinsEarned = state.coinsEarnedThisLevel,
            moves = state.movesCount,
            isBengali = state.isBengali,
            onNextLevel = {
                if (activity != null && AdManager.isInterstitialAvailable()) {
                    AdManager.showInterstitialAd(activity) {
                        viewModel.nextLevel()
                    }
                } else {
                    viewModel.nextLevel()
                }
            },
            onReplay = { viewModel.restartLevel() },
            onDismiss = { viewModel.dismissVictoryDialog() }
        )

        // Level Select Dialog
        LevelSelectDialog(
            show = showLevelSelect,
            currentLevel = state.currentLevel,
            maxUnlockedLevel = state.maxUnlockedLevel,
            isBengali = state.isBengali,
            getStarsForLevel = { viewModel.preferences.getLevelStars(it) },
            onSelectLevel = { viewModel.loadLevel(it) },
            onDismiss = { showLevelSelect = false }
        )

        // Theme and Settings Sheet
        ThemeSettingsSheet(
            show = showThemeSettings,
            isBengali = state.isBengali,
            coins = state.coins,
            unlockedSkins = state.unlockedSkins,
            unlockedBgThemes = state.unlockedBgThemes,
            shopMessage = state.shopMessage,
            soundEnabled = state.soundEnabled,
            soundVolume = state.soundVolume,
            vibrationEnabled = state.vibrationEnabled,
            currentTubeSkin = state.tubeSkin,
            currentBgTheme = state.bgTheme,
            onToggleSound = { viewModel.toggleSound() },
            onVolumeChange = { viewModel.setSoundVolume(it) },
            onToggleVibration = { viewModel.toggleVibration() },
            onToggleLanguage = { viewModel.toggleLanguage() },
            onBuyOrEquipSkin = { viewModel.buyOrEquipSkin(it) },
            onBuyOrEquipBgTheme = { viewModel.buyOrEquipBgTheme(it) },
            onClearShopMessage = { viewModel.clearShopMessage() },
            onDismiss = { showThemeSettings = false }
        )

        // Rewarded Ads & Coins Dialog for +1 Extra Tube (3 Videos or 15 Coins)
        WatchAdForTubeDialog(
            show = state.showWatchAdDialog,
            isBengali = state.isBengali,
            adsWatched = state.adsWatchedForTube,
            targetAds = 3,
            coins = state.coins,
            coinCost = 15,
            isAdLoading = state.isAdLoading,
            errorMessage = state.adErrorMessage,
            onWatchAd = { viewModel.watchAdForTube(activity) },
            onBuyWithCoins = { viewModel.buyExtraTubeWithCoins(15) },
            onClaimTube = { viewModel.grantExtraTube() },
            onDismiss = { viewModel.dismissWatchAdDialog() }
        )
    }
}

@Composable
private fun TopGameBar(
    currentLevel: Int,
    coins: Int,
    moves: Int,
    isBengali: Boolean,
    onBackToMenu: () -> Unit,
    onOpenLevelSelect: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackToMenu,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x25FFFFFF))
                    .testTag("home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Level Indicator Pill (clickable for level select)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(20.dp))
                    .clickable { onOpenLevelSelect() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("level_select_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "Levels",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBengali) "লেভেল $currentLevel" else "Level $currentLevel",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (currentLevel >= 31) {
                    val diff = com.example.model.LevelGenerator.getDifficultyForLevel(currentLevel)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) diff.titleBn else diff.titleEn,
                        fontSize = 9.sp,
                        color = Color(diff.colorHex),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(diff.colorHex).copy(alpha = 0.22f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Moves Indicator
        Text(
            text = if (isBengali) "চাল: $moves" else "Moves: $moves",
            color = Color(0xBBFFFFFF),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

        // Right side: Coins pill & Settings button
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33FFFFFF))
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    tint = Color(0xFFFFD600),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x25FFFFFF))
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun StuckWarningBanner(
    isBengali: Boolean,
    hasEmptyTube: Boolean,
    onRestart: () -> Unit,
    onAddTube: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF2B71C1C),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF5252)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Stuck",
                    tint = Color(0xFFFFD600),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (!hasEmptyTube) {
                            if (isBengali) "💀 সব গ্লাস ভর্তি—কোনো গ্লাস খালি নেই!" else "All glasses filled—zero empty glasses!"
                        } else {
                            if (isBengali) "⛔ কোনো চাল নেই! খেলা আটকে গেছে" else "No moves left! Puzzle is locked"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (!hasEmptyTube) {
                            if (isBengali) "ভিডিও দেখে ৩-৪টি খালি গ্লাস নিন এবং খেলা শুরু করুন!" else "Watch video ads to add 3-4 empty glasses to play!"
                        } else {
                            if (isBengali) "ভিডিও বিজ্ঞাপন দেখে ৩-৪টি খালি গ্লাস নিন!" else "Watch video ads to add 3-4 extra empty glasses!"
                        },
                        color = Color(0xEEFFFFFF),
                        fontSize = 10.5.sp
                    )
                }
            }

            // Free Video Tube Button
            Button(
                onClick = onAddTube,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = null,
                    tint = Color(0xFF062A14),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBengali) "+১ গ্লাস (ভিডিও)" else "+1 Tube (Ad)",
                    color = Color(0xFF062A14),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TubesGameBoard(
    tubes: List<Tube>,
    selectedTubeIndex: Int?,
    shakingTubeIndex: Int?,
    hintSourceIndex: Int?,
    hintTargetIndex: Int?,
    isPouring: Boolean,
    pouringSourceIndex: Int?,
    pouringTargetIndex: Int?,
    pouringColor: com.example.model.LiquidColor?,
    pouringUnits: Int,
    pourProgress: Float,
    tubeSkin: com.example.model.TubeSkin,
    tubeWidth: Dp,
    tubeHeight: Dp,
    tubeBoundsMap: Map<Int, Offset>,
    onTubeClicked: (Int) -> Unit,
    onTubePositioned: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val total = tubes.size
    // Enforce strictly maximum 4 glasses per line/row as requested: "গ্লাস এক লাইনে চারটার বেশি হবে না"
    val rows = when {
        total <= 4 -> listOf(tubes.indices.toList())
        total in 5..8 -> {
            val topCount = (total + 1) / 2
            listOf(
                (0 until topCount).toList(),
                (topCount until total).toList()
            )
        }
        else -> {
            tubes.indices.chunked(4)
        }
    }

    val verticalRowSpacing = when {
        total <= 6 -> 28.dp
        total <= 10 -> 18.dp
        else -> 10.dp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(verticalRowSpacing)
    ) {
        rows.forEach { rowIndices ->
            val rowSpacing = when {
                rowIndices.size <= 3 -> 18.dp
                rowIndices.size <= 4 -> 14.dp
                rowIndices.size <= 5 -> 10.dp
                else -> 6.dp
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(rowSpacing),
                verticalAlignment = Alignment.Bottom
            ) {
                rowIndices.forEach { idx ->
                    val tube = tubes.getOrNull(idx) ?: return@forEach

                    val isSelected = (selectedTubeIndex == idx)
                    val isShaking = (shakingTubeIndex == idx)
                    val isHintSrc = (hintSourceIndex == idx)
                    val isHintDst = (hintTargetIndex == idx)
                    val isSourcePouring = (isPouring && pouringSourceIndex == idx)
                    val isTargetPouring = (isPouring && pouringTargetIndex == idx)

                    // Calculate flight trajectory towards destination glass
                    var tiltAngle = 0f
                    var pourLiftY = 0.dp
                    var pourShiftX = 0.dp

                    if (isSourcePouring && pouringTargetIndex != null) {
                        val srcPos = tubeBoundsMap[idx]
                        val dstPos = tubeBoundsMap[pouringTargetIndex]
                        val isSrcToLeft = if (srcPos != null && dstPos != null) srcPos.x <= dstPos.x else (idx < pouringTargetIndex)

                        // 3-Phase motion:
                        // 0.00..0.20: Gracefully flies directly above destination tube & tilts
                        // 0.20..0.78: Stably hovers above destination tube while water pours down calmly
                        // 0.78..1.00: Smoothly tilts upright and glides back to original slot
                        val flightProgress = when {
                            pourProgress < 0.20f -> {
                                val t = pourProgress / 0.20f
                                (kotlin.math.sin((t - 0.5f) * kotlin.math.PI.toFloat()) + 1f) / 2f
                            }
                            pourProgress <= 0.78f -> 1.0f
                            else -> {
                                val t = 1.0f - ((pourProgress - 0.78f) / 0.22f)
                                (kotlin.math.sin((t - 0.5f) * kotlin.math.PI.toFloat()) + 1f) / 2f
                            }
                        }

                        if (srcPos != null && dstPos != null) {
                            val density = LocalDensity.current
                            val halfHPx = with(density) { (tubeHeight / 2f).toPx() }
                            val tubeTopOffsetPx = with(density) { (tubeHeight * 0.08f).toPx() }
                            val halfWPx = with(density) { (tubeWidth / 2f).toPx() }

                            val dstRimY = dstPos.y - halfHPx + 18f
                            val srcPivotX = srcPos.x
                            val srcPivotY = srcPos.y - halfHPx + tubeTopOffsetPx

                            val tiltDeg = 65f
                            val tiltRad = Math.toRadians(tiltDeg.toDouble()).toFloat()
                            val cosTilt = kotlin.math.cos(tiltRad)
                            val sinTilt = kotlin.math.sin(tiltRad)

                            // Generous vertical clearance above the destination rim (70dp, matching reference image)
                            // This ensures the tilted tube floats high in the air and NEVER touches any of the 3 standing tubes!
                            val hoverAboveRimPx = with(density) { 70.dp.toPx() }
                            val targetLipX = dstPos.x
                            val targetLipY = dstRimY - hoverAboveRimPx

                            val lipOffsetFromPivotX = if (isSrcToLeft) (halfWPx * cosTilt) else (-halfWPx * cosTilt)
                            val lipOffsetFromPivotY = halfWPx * sinTilt

                            val desiredPivotX = targetLipX - lipOffsetFromPivotX
                            val desiredPivotY = targetLipY - lipOffsetFromPivotY

                            val targetShiftX = with(density) { (desiredPivotX - srcPivotX).toDp() }
                            val targetShiftY = with(density) { (desiredPivotY - srcPivotY).toDp() }

                            pourShiftX = targetShiftX * flightProgress
                            pourLiftY = targetShiftY * flightProgress
                        } else {
                            val shiftDir = if (isSrcToLeft) 1f else -1f
                            pourShiftX = (shiftDir * 75f * flightProgress).dp
                            pourLiftY = (-100f * flightProgress).dp
                        }

                        val maxTilt = if (isSrcToLeft) 65f else -65f
                        tiltAngle = maxTilt * flightProgress
                    }

                    // Generous clickable touch container (width + 12dp, height + 45dp) so touch is always captured
                    Box(
                        modifier = Modifier
                            .width(tubeWidth + 12.dp)
                            .height(tubeHeight + 45.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onTubeClicked(idx) }
                            )
                            .onGloballyPositioned { coordinates ->
                                val bounds = coordinates.boundsInRoot()
                                onTubePositioned(idx, bounds.center)
                            }
                            .testTag("tube_slot_$idx"),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        GlassTubeItem(
                            tube = tube,
                            isSelected = isSelected,
                            isShaking = isShaking,
                            isHintSource = isHintSrc,
                            isHintTarget = isHintDst,
                            tubeSkin = tubeSkin,
                            tubeWidth = tubeWidth,
                            tubeHeight = tubeHeight,
                            tiltAngle = tiltAngle,
                            offsetX = pourShiftX,
                            offsetY = pourLiftY,
                            isPouringSource = isSourcePouring,
                            isPouringTarget = isTargetPouring,
                            pouringColor = pouringColor,
                            pouringUnits = pouringUnits,
                            pourProgress = pourProgress,
                            onClick = { onTubeClicked(idx) },
                            modifier = Modifier
                                .zIndex(if (isSourcePouring) 200f else if (isSelected) 10f else 1f)
                                .testTag("tube_item_$idx")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    coins: Int,
    extraTubesCount: Int,
    isBengali: Boolean,
    onRestart: () -> Unit,
    onHint: () -> Unit,
    onAddTube: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0x35FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x25FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Restart Button
            ActionButtonItem(
                icon = Icons.Default.Refresh,
                label = if (isBengali) "রিস্টার্ট" else "Restart",
                enabled = true,
                badge = null,
                iconTint = Color(0xFFFF9100),
                testTag = "restart_button",
                onClick = onRestart
            )

            // 2. Hint Button (Smart Solver)
            ActionButtonItem(
                icon = Icons.Default.Lightbulb,
                label = if (isBengali) "ইঙ্গিত" else "Hint",
                enabled = true,
                badge = null,
                iconTint = Color(0xFFFFD600),
                testTag = "hint_button",
                onClick = onHint
            )

            // 3. Add Extra Tube (+1 Bottle Booster via 3 Video Ads or Coins)
            val canAddMore = extraTubesCount < 5
            ActionButtonItem(
                icon = Icons.Default.Add,
                label = if (isBengali) "+১ গ্লাস" else "+1 Glass",
                enabled = canAddMore,
                badge = if (canAddMore) "ভিডিও/কয়েন" else "পূর্ণ",
                iconTint = if (canAddMore) Color(0xFF00E676) else Color(0x55FFFFFF),
                testTag = "add_tube_button",
                onClick = onAddTube
            )
        }
    }
}

@Composable
private fun ActionButtonItem(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    badge: String?,
    iconTint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (enabled) Color(0x25FFFFFF) else Color(0x10FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) Color.White else Color(0x66FFFFFF)
        )

        if (badge != null) {
            Text(
                text = badge,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD600)
            )
        }
    }
}
