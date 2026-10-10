package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GameAestheticBackground
import com.example.ui.components.GlassTubeRenderer
import com.example.ui.components.VictoryDialog
import com.example.ui.components.WatchAdForTubeDialog
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WaterSortScreen(viewModel: GameViewModel) {
    val activity = LocalContext.current as? Activity
    val tubes by viewModel.tubes.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val movesCount by viewModel.movesCount.collectAsState()
    val selectedTubeId by viewModel.selectedTubeId.collectAsState()
    val isVictory by viewModel.isVictory.collectAsState()
    val activeHint by viewModel.activeHint.collectAsState()
    val tubeSkin by viewModel.tubeSkin.collectAsState()
    val bgTheme by viewModel.bgTheme.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val extraTubesStock by viewModel.extraTubesStock.collectAsState()

    var showExtraTubeDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateTo(ScreenState.START_MENU)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameAestheticBackground(theme = bgTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(ScreenState.START_MENU) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LEVEL $currentLevel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Moves: $movesCount",
                        fontSize = 12.sp,
                        color = Color(0xFF00E5FF)
                    )
                }

                IconButton(onClick = { viewModel.restartCurrentLevel() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tubes Layout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(28.dp),
                    maxItemsInEachRow = 4
                ) {
                    tubes.forEach { tube ->
                        val isSelected = tube.id == selectedTubeId
                        val isHint = tube.id == activeHint?.fromTubeId || tube.id == activeHint?.toTubeId

                        GlassTubeRenderer(
                            tube = tube,
                            isSelected = isSelected,
                            isHintTarget = isHint,
                            tubeSkin = tubeSkin,
                            onClick = { viewModel.onTubeClicked(tube.id) }
                        )
                    }
                }
            }

            // Bottom Action Bar (Undo, Hint, Extra Tube)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B263B)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { viewModel.undoMove() }) {
                            Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Undo", fontSize = 11.sp, color = Color.LightGray)
                }

                // Add Tube
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            if (extraTubesStock > 0) {
                                viewModel.addExtraTube()
                            } else {
                                showExtraTubeDialog = true
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Extra Tube", tint = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (extraTubesStock > 0) "+Bottle ($extraTubesStock)" else "+Bottle",
                        fontSize = 11.sp,
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Hint
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B263B)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { viewModel.requestHint() }) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint", tint = Color(0xFFFFD700))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Hint", fontSize = 11.sp, color = Color.LightGray)
                }
            }
        }

        // Victory Confetti
        ConfettiEffect(visible = isVictory)
    }

    if (showExtraTubeDialog) {
        WatchAdForTubeDialog(
            userCoins = currentUser?.coins ?: 0,
            onWatchAd = {
                activity?.let {
                    viewModel.addStockFromRewardedAd(it) {
                        showExtraTubeDialog = false
                    }
                }
            },
            onBuyWithCoins = {
                viewModel.buyTubeWithCoins {
                    showExtraTubeDialog = false
                }
            },
            onDismiss = { showExtraTubeDialog = false }
        )
    }

    if (isVictory) {
        VictoryDialog(
            level = currentLevel,
            earnedCoins = 25,
            moves = movesCount,
            onNextLevel = { viewModel.nextLevel(activity) },
            onReplay = { viewModel.restartCurrentLevel() }
        )
    }
}
