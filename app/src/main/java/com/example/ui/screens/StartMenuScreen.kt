package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ads.BannerAdView
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import com.example.model.UserAccount
import com.example.ui.components.GameAestheticBackground

@Composable
fun StartMenuScreen(
    currentLevel: Int,
    coins: Int,
    isBengali: Boolean,
    bgTheme: BackgroundTheme,
    currentUser: UserAccount?,
    onStartGame: () -> Unit,
    onOpenLevelSelect: () -> Unit,
    onOpenThemes: () -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLogin: () -> Unit = {},
    onOpenPolicy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btnPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnPulse"
    )

    GameAestheticBackground(
        theme = bgTheme,
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Header Row: Profile, Coins & Settings / Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, Color(0x3038BDF8), RoundedCornerShape(20.dp))
                        .clickable { onOpenProfile() }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("menu_profile_btn"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentUser?.avatarEmoji ?: "👤", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = currentUser?.username ?: if (isBengali) "প্রোফাইল" else "Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                // Coins Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, Color(0x30FFD600), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = Color(0xFFFFD600),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$coins",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Language & Themes Quick Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x25FFFFFF))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("menu_lang_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "বাংলা" else "EN",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Settings Button
                    IconButton(
                        onClick = onOpenThemes,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x25FFFFFF))
                            .testTag("menu_settings_btn")
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

            // 2. Center Hero Brand: Animated Tubes Art & Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tubes Visual Emblem
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0x4000E5FF), Color(0x1000E5FF), Color.Transparent)
                            )
                        )
                        .border(2.dp, Color(0x4000E5FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(80.dp)) {
                        val w = size.width
                        val h = size.height
                        val tubeW = 22f
                        val tubeH = 55f

                        // Tube 1 (Left)
                        val t1Left = w * 0.30f - (tubeW / 2f)
                        val t1Top = h * 0.20f
                        // Liquid
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFFFFD600), Color(0xFFFF2A55))),
                            topLeft = Offset(t1Left + 2f, t1Top + 14f),
                            size = Size(tubeW - 4f, tubeH - 16f),
                            cornerRadius = CornerRadius((tubeW - 4f) / 2f, (tubeW - 4f) / 2f)
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(t1Left, t1Top),
                            size = Size(tubeW, tubeH),
                            cornerRadius = CornerRadius(tubeW / 2f, tubeW / 2f),
                            style = Stroke(width = 3f)
                        )

                        // Tube 2 (Right)
                        val t2Left = w * 0.70f - (tubeW / 2f)
                        val t2Top = h * 0.20f
                        drawRoundRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF00E676), Color(0xFF00E5FF))),
                            topLeft = Offset(t2Left + 2f, t2Top + 18f),
                            size = Size(tubeW - 4f, tubeH - 20f),
                            cornerRadius = CornerRadius((tubeW - 4f) / 2f, (tubeW - 4f) / 2f)
                        )
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(t2Left, t2Top),
                            size = Size(tubeW, tubeH),
                            cornerRadius = CornerRadius(tubeW / 2f, tubeW / 2f),
                            style = Stroke(width = 3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ALIF WATER SORT PRO",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = Color.White
                )

                Text(
                    text = if (isBengali) "আলিফ ওয়াটার সর্ট প্রো" else "Premium Color Water Puzzle",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00E5FF)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Current Level Indicator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x4000E5FF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "লেভেল $currentLevel" else "Level $currentLevel",
                        color = Color(0xFFFFD600),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // 3. Bottom Action Buttons: START GAME + Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BIG VIBRANT START GAME BUTTON
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .scale(pulseScale)
                        .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF00E676))
                        .testTag("start_game_button"),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color(0xFF003816),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "গেম শুরু করুন" else "START GAME",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF003816)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary row buttons: Login Page & Themes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Login Page Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onOpenLogin() }
                            .testTag("menu_login_btn"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0x35FFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4000E5FF))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Login Page",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "লগইন পেজ" else "Login Page",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Themes & Skins Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onOpenThemes() }
                            .testTag("menu_themes_btn"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0x35FFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x30FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Shop and Themes",
                                tint = Color(0xFFFFD600),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "শপ ও থিম 🪙" else "Shop & Themes 🪙",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Footer Links: Login Page & Policy Page
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Login Link
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenLogin() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("bottom_login_link"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xCC00E5FF),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "লগইন পেজ" else "Login",
                            color = Color(0xDDFFFFFF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = " • ",
                        color = Color(0x55FFFFFF),
                        fontSize = 12.sp
                    )

                    // Policy Link
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpenPolicy() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("bottom_policy_link"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF69F0AE),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "গোপনীয়তা নীতি (Policy)" else "Privacy Policy",
                            color = Color(0xFF69F0AE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // AdMob Banner Ad at bottom
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}
