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
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
    targetAds: Int = 1,
    isAdLoading: Boolean,
    errorMessage: String? = null,
    onWatchAd: () -> Unit,
    onClaimTube: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val isCompleted = adsWatched >= targetAds

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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "🧪 ভিডিও দেখে খালি গ্লাস নিন" else "🧪 Watch Ad for Free Glass",
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

                Spacer(modifier = Modifier.height(14.dp))

                // Icon / Illustration
                Box(
                    modifier = Modifier
                        .size(68.dp)
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
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & explanation
                Text(
                    text = if (isCompleted) {
                        if (isBengali) "🎉 দারুণ! বিজ্ঞাপন দেখা সম্পন্ন হয়েছে।"
                        else "🎉 Awesome! Video ad completed."
                    } else {
                        if (isBengali) "লেভেলটি কঠিন লাগছে? ১টি ছোট ভিডিও বিজ্ঞাপন দেখুন এবং সাথে সাথে একটি অতিরিক্ত খালি গ্লাস নিয়ে খেলুন!"
                        else "Level feels challenging? Watch 1 quick video ad to get an extra empty glass and solve the level!"
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xEEFFFFFF),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x22FFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.OndemandVideo,
                            contentDescription = null,
                            tint = if (isCompleted) Color(0xFF00E676) else Color(0xFFFFD600),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "১টি ভিডিও = ১টি নতুন খালি টেস্ট টিউব" else "1 Video Ad = 1 New Empty Tube",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Error Message if any
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = Color(0xFFFF8A80),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button
                if (isCompleted) {
                    Button(
                        onClick = onClaimTube,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("claim_free_tube_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676)
                        )
                    ) {
                        Text(
                            text = if (isBengali) "🎉 অতিরিক্ত গ্লাস যোগ করুন" else "🎉 Add Extra Tube Now",
                            color = Color(0xFF062A14),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onWatchAd,
                        enabled = !isAdLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("watch_ad_action_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD600)
                        )
                    ) {
                        if (isAdLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.Black,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "বিজ্ঞাপন লোড হচ্ছে..." else "Loading Video Ad...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "ভিডিও দেখুন ও গ্লাস নিন" else "Watch Video & Get Glass",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
