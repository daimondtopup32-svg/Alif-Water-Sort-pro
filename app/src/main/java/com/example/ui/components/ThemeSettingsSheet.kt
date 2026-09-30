package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundTheme
import com.example.model.TubeSkin
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(
    show: Boolean,
    isBengali: Boolean,
    coins: Int,
    unlockedSkins: Set<String>,
    unlockedBgThemes: Set<String>,
    shopMessage: String?,
    soundEnabled: Boolean,
    soundVolume: Float,
    vibrationEnabled: Boolean,
    currentTubeSkin: TubeSkin,
    currentBgTheme: BackgroundTheme,
    onToggleSound: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleVibration: () -> Unit,
    onToggleLanguage: () -> Unit,
    onBuyOrEquipSkin: (TubeSkin) -> Unit,
    onBuyOrEquipBgTheme: (BackgroundTheme) -> Unit,
    onClearShopMessage: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Auto-dismiss shop notification after 3 seconds
    LaunchedEffect(shopMessage) {
        if (shopMessage != null) {
            delay(3200)
            onClearShopMessage()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF10172A),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shop",
                        tint = Color(0xFFFFD600),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBengali) "কয়েন শপ ও সেটিংস" else "Coin Shop & Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_shop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xCCFFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Coins Balance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2E2600), Color(0xFF1E1E00))
                        )
                    )
                    .border(1.5.dp, Color(0xFFFFD600), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "আপনার কয়েন ব্যালেন্স" else "Your Coin Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFE082)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBengali) "লেভেল জিতে কয়েন বাড়ান ও স্কিন কিনুন!" else "Win levels to earn coins & unlock styles!",
                            fontSize = 11.sp,
                            color = Color(0xAAFFFFFF)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x35FFD600))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🪙 $coins",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD600)
                        )
                    }
                }
            }

            // Notification / Status Toast Banner
            AnimatedVisibility(
                visible = shopMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (shopMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF003822))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = shopMessage,
                            color = Color(0xFFB9F6CA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 1: TUBE SKINS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "🧪 গ্লাস ও টিউব স্টাইল" else "🧪 Tube Styles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF)
                )
                Text(
                    text = if (isBengali) "কয়েন দিয়ে আনলক" else "Unlock with coins",
                    fontSize = 11.sp,
                    color = Color(0x88FFFFFF)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid of 4 Tube Skins (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val skinList = TubeSkin.entries
                for (row in 0 until (skinList.size + 1) / 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..1) {
                            val idx = row * 2 + col
                            if (idx < skinList.size) {
                                val skin = skinList[idx]
                                val isEquipped = (skin == currentTubeSkin)
                                val isUnlocked = unlockedSkins.contains(skin.name) || skin.coinCost == 0

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            when {
                                                isEquipped -> Color(0xFF1B3A57)
                                                isUnlocked -> Color(0xFF1E2638)
                                                else -> Color(0xFF181B24)
                                            }
                                        )
                                        .border(
                                            width = if (isEquipped) 2.dp else 1.dp,
                                            color = when {
                                                isEquipped -> Color(0xFF00E5FF)
                                                isUnlocked -> Color(0x44FFFFFF)
                                                else -> Color(0x33FFD600)
                                            },
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onBuyOrEquipSkin(skin) }
                                        .padding(horizontal = 10.dp, vertical = 12.dp)
                                        .testTag("skin_${skin.name.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isBengali) skin.titleBn else skin.titleEn,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Status Pill Badge
                                        when {
                                            isEquipped -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0xFF00E676))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(0xFF003816),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = if (isBengali) "ব্যবহার হচ্ছে" else "Equipped",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF003816)
                                                    )
                                                }
                                            }
                                            isUnlocked -> {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0x3500E5FF))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (isBengali) "ব্যবহার করুন" else "Equip",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF80D8FF)
                                                    )
                                                }
                                            }
                                            else -> {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(Color(0x33FFD600))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lock,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFD600),
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "${skin.coinCost} 🪙",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFD600)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 2: BACKGROUND THEMES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "🌌 ব্যাকগ্রাউন্ড থিম" else "🌌 Background Themes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF)
                )
                Text(
                    text = if (isBengali) "কয়েন দিয়ে কিনুন" else "Buy with coins",
                    fontSize = 11.sp,
                    color = Color(0x88FFFFFF)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Background themes list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BackgroundTheme.entries.forEach { bg ->
                    val isEquipped = (bg == currentBgTheme)
                    val isUnlocked = unlockedBgThemes.contains(bg.name) || bg.coinCost == 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(listOf(bg.topColor, bg.bottomColor))
                            )
                            .border(
                                width = if (isEquipped) 2.dp else 1.dp,
                                color = when {
                                    isEquipped -> Color(0xFFFFD600)
                                    isUnlocked -> Color(0x44FFFFFF)
                                    else -> Color(0x33FFD600)
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onBuyOrEquipBgTheme(bg) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("bg_${bg.name.lowercase()}"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(bg.accentGlow)
                                    .border(1.dp, Color(0x66FFFFFF), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isBengali) bg.titleBn else bg.titleEn,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (isEquipped) FontWeight.Bold else FontWeight.Medium
                            )
                        }

                        // Right Status / Purchase Pill
                        when {
                            isEquipped -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFD600))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF261D00),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBengali) "সক্রিয়" else "Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF261D00)
                                    )
                                }
                            }
                            isUnlocked -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x3500E5FF))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isBengali) "ব্যবহার করুন" else "Equip",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF80D8FF)
                                    )
                                }
                            }
                            else -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x35FFD600))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD600),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${bg.coinCost} 🪙",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD600)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 3: AUDIO & SETTINGS
            Text(
                text = if (isBengali) "⚙️ সাউন্ড ও সেটিংস" else "⚙️ Sound & Settings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sound Toggle & Volume Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x18FFFFFF))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Sound",
                            tint = if (soundEnabled) Color(0xFF00E5FF) else Color(0x66FFFFFF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBengali) "শব্দ (Sound)" else "Game Sound",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E5FF),
                            checkedTrackColor = Color(0x6600E5FF)
                        )
                    )
                }

                if (soundEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "ভলিউম" else "Volume",
                            color = Color(0xAAFFFFFF),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Slider(
                            value = soundVolume,
                            onValueChange = onVolumeChange,
                            valueRange = 0.05f..1f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00E5FF),
                                activeTrackColor = Color(0xFF00E5FF)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vibration Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x18FFFFFF))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Vibration",
                        tint = if (vibrationEnabled) Color(0xFFFFD600) else Color(0x66FFFFFF)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBengali) "কম্পন (Vibration)" else "Vibration / Haptic",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = { onToggleVibration() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFFD600),
                        checkedTrackColor = Color(0x66FFD600)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Language Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x18FFFFFF))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(0xFF00E676)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBengali) "ভাষা: বাংলা" else "Language: English",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = if (isBengali) "English এ বদলান" else "বাংলা করুন",
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
