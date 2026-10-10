package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GameAestheticBackground
import com.example.ui.components.LevelSelectDialog
import com.example.ui.components.PolicyDialog
import com.example.ui.components.ThemeSettingsSheet
import com.example.ui.components.UserProfileDialog
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenState
import kotlinx.coroutines.launch

@Composable
fun StartMenuScreen(viewModel: GameViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val bgTheme by viewModel.bgTheme.collectAsState()
    val tubeSkin by viewModel.tubeSkin.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val volume by viewModel.volume.collectAsState()

    var showSettings by remember { mutableStateOf(false) }
    var showLevels by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    var showPolicy by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        GameAestheticBackground(theme = bgTheme)

        // Top bar buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1B263B))
            ) {
                IconButton(onClick = {
                    if (currentUser == null) {
                        viewModel.navigateTo(ScreenState.AUTH)
                    } else {
                        showProfile = true
                    }
                }) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF00E5FF))
                }
            }

            Row {
                IconButton(onClick = { showPolicy = true }) {
                    Icon(imageVector = Icons.Default.Policy, contentDescription = "Policy", tint = Color.LightGray)
                }
                IconButton(onClick = { showSettings = true }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        }

        // Center Title & Actions
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ALIF WATER SORT",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "PRO PUZZLE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Play Button
            Button(
                onClick = { viewModel.startLevel(currentLevel) },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PLAY (LVL $currentLevel)", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Select Level Button
            OutlinedButton(
                onClick = { showLevels = true },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.GridView, contentDescription = "Levels", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SELECT LEVEL", color = Color.White, fontWeight = FontWeight.Bold)
            }

            if (currentUser == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch { viewModel.userRepository.loginAsGuest() }
                    },
                    modifier = Modifier.fillMaxWidth(0.75f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B263B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("PLAY AS GUEST", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSettings) {
        ThemeSettingsSheet(
            soundEnabled = soundEnabled,
            hapticEnabled = hapticEnabled,
            volume = volume,
            currentSkin = tubeSkin,
            currentTheme = bgTheme,
            onSoundToggle = { scope.launch { viewModel.preferences.setSoundEnabled(it) } },
            onHapticToggle = { scope.launch { viewModel.preferences.setHapticEnabled(it) } },
            onVolumeChange = { scope.launch { viewModel.preferences.setVolume(it) } },
            onSelectSkin = { scope.launch { viewModel.preferences.setTubeSkin(it) } },
            onSelectTheme = { scope.launch { viewModel.preferences.setBgTheme(it) } },
            onDismiss = { showSettings = false }
        )
    }

    if (showLevels) {
        LevelSelectDialog(
            highestUnlockedLevel = currentUser?.highestUnlockedLevel ?: currentLevel,
            currentLevel = currentLevel,
            onSelectLevel = { lvl -> viewModel.startLevel(lvl) },
            onDismiss = { showLevels = false }
        )
    }

    if (showProfile) {
        UserProfileDialog(
            user = currentUser,
            onLogout = {
                scope.launch { viewModel.userRepository.logout() }
                showProfile = false
            },
            onDismiss = { showProfile = false }
        )
    }

    if (showPolicy) {
        PolicyDialog(onDismiss = { showPolicy = false })
    }
}
