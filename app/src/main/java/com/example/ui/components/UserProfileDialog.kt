package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.UserAccount

@Composable
fun UserProfileDialog(
    show: Boolean,
    user: UserAccount?,
    isBengali: Boolean,
    coins: Int,
    currentLevel: Int,
    onSelectAvatar: (String) -> Unit,
    onLogout: () -> Unit,
    onOpenAuth: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val avatars = listOf("💧", "🧪", "👑", "⚡", "🌟", "🧙", "🐯", "🚀", "🎮", "💎")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF131D38),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("user_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "👤 প্লেয়ার প্রোফাইল" else "👤 Player Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Display
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0x3300E5FF))
                        .border(2.dp, Color(0xFF00E5FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.avatarEmoji ?: "💧",
                        fontSize = 42.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Username & Guest Tag
                Text(
                    text = user?.username ?: (if (isBengali) "অতিথি প্লেয়ার" else "Guest Player"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (user?.isGuest == true) {
                    Text(
                        text = if (isBengali) "⚠️ গেস্ট অ্যাকাউন্ট (প্রগ্রেস সেভ করতে লগইন করুন)" else "⚠️ Guest Account (Login to save progress)",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards (Level & Coins)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x22FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD600))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "লেভেল" else "Level",
                                    fontSize = 11.sp,
                                    color = Color(0xAAFFFFFF)
                                )
                                Text(
                                    text = "$currentLevel",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x22FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD600))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "কয়েন" else "Coins",
                                    fontSize = 11.sp,
                                    color = Color(0xAAFFFFFF)
                                )
                                Text(
                                    text = "$coins 🪙",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD600)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Change
                Text(
                    text = if (isBengali) "অবতার পরিবর্তন করুন:" else "Change Avatar:",
                    fontSize = 12.sp,
                    color = Color(0xCCFFFFFF),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { emoji ->
                        val isSelected = emoji == (user?.avatarEmoji ?: "💧")
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0x22FFFFFF)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color(0x33FFFFFF),
                                    shape = CircleShape
                                )
                                .clickable { onSelectAvatar(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // If Guest, button to Register/Login
                if (user?.isGuest == true) {
                    Button(
                        onClick = onOpenAuth,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = if (isBengali) "🔑 লগইন / রেজিস্ট্রেশন করুন" else "🔑 Login / Register",
                            color = Color(0xFF061E38),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Logout Button
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "লগ আউট / একাউন্ট পরিবর্তন" else "Logout / Switch Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
