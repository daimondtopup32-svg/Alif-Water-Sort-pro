package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun WatchAdForTubeDialog(
    show: Boolean,
    isBengali: Boolean,
    adsWatched: Int,
    targetAds: Int = 3,
    coins: Int,
    coinCost: Int = 15,
    isAdLoading: Boolean,
    errorMessage: String? = null,
    onWatchAd: () -> Unit,
    onBuyWithCoins: () -> Unit,
    onClaimTube: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val isCompleted = adsWatched >= targetAds
    val canAffordCoins = coins >= coinCost

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF131D38),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("watch_ad_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "🧪 +১ অতিরিক্ত খালি গ্লাস" else "🧪 +1 Extra Empty Glass",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xCCFFFFFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Icon / Illustration
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) Color(0x3500E676) else Color(0x3500E5FF))
                        .border(
                            2.dp,
                            if (isCompleted) Color(0xFF00E676) else Color(0xFF00E5FF),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.OndemandVideo,
                        contentDescription = "Video Ad",
                        tint = if (isCompleted) Color(0xFF00E676) else Color(0xFFFFD600),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & explanation
                Text(
                    text = if (isCompleted) {
                        if (isBengali) "🎉 অভিনন্দন! ৩টি ভিডিও দেখা সম্পন্ন হয়েছে।"
                        else "🎉 Awesome! 3 video ads completed."
                    } else {
                        if (isBengali) "১টি অতিরিক্ত খালি গ্লাস পেতে ৩টি ভিডিও দেখুন অথবা কয়েন দিয়ে যোগ করুন!"
                        else "Watch 3 video ads to get 1 extra empty glass, or unlock with coins!"
                    },
                    fontSize = 13.5.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xEEFFFFFF),
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar: 3 video ads per glass
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x22FFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBengali) "ভিডিও প্রগ্রেস:" else "Ad Progress:",
                                color = Color(0xCCFFFFFF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$adsWatched / $targetAds",
                                color = Color(0xFF00E5FF),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = (adsWatched.toFloat() / targetAds.toFloat()).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF00E676),
                            trackColor = Color(0x33FFFFFF)
                        )
                    }
                }

                // Error Message if any
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = Color(0xFFFF8A80),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Video Action Button
                if (isCompleted) {
                    Button(
                        onClick = onClaimTube,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("claim_free_tube_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                    ) {
                        Text(
                            text = if (isBengali) "🎉 ১টি খালি গ্লাস নিন" else "🎉 Claim 1 Empty Tube",
                            color = Color(0xFF062A14),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onWatchAd,
                        enabled = !isAdLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("watch_ad_action_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600))
                    ) {
                        if (isAdLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "বিজ্ঞাপন লোড হচ্ছে..." else "Loading Video Ad...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "ভিডিও দেখুন (${adsWatched + 1}/$targetAds)" else "Watch Video (${adsWatched + 1}/$targetAds)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Divider: "অথবা কয়েন দিয়ে নিন"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x33FFFFFF))
                    Text(
                        text = if (isBengali) " অথবা কয়েন দিয়ে " else " OR WITH COINS ",
                        color = Color(0x88FFFFFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x33FFFFFF))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Option B: Buy with coins
                Button(
                    onClick = onBuyWithCoins,
                    enabled = canAffordCoins,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("buy_tube_with_coins_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        disabledContainerColor = Color(0x22FFFFFF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAffordCoins) Color(0xFF00373F) else Color(0x55FFFFFF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (canAffordCoins) {
                            if (isBengali) "$coinCost কয়েন দিয়ে ১টি গ্লাস নিন (আছে: $coins)" else "Buy 1 Glass for $coinCost Coins (Have: $coins)"
                        } else {
                            if (isBengali) "কয়েন অপর্যাপ্ত ($coinCost প্রয়োজন, আছে: $coins)" else "Need $coinCost Coins (Have: $coins)"
                        },
                        color = if (canAffordCoins) Color(0xFF00373F) else Color(0x55FFFFFF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
