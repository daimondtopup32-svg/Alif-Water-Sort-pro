package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PolicyDialog(
    show: Boolean,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    if (!show) return

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp)
                .testTag("policy_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBengali) "গোপনীয়তা নীতি ও শর্তাবলী" else "Privacy Policy & Terms",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isBengali) "আলিফ ওয়াটার সর্ট প্রো গেম" else "Alif Water Sort Pro Game",
                                fontSize = 11.sp,
                                color = Color(0xAAFFFFFF)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Introduction
                    PolicySectionCard(
                        icon = Icons.Default.Policy,
                        title = if (isBengali) "১. ভূমিকা ও পরিচিতি" else "1. Introduction",
                        content = if (isBengali) {
                            "আলিফ ওয়াটার সর্ট প্রো (\"Alif Water Sort Pro\") গেমটি ব্যবহার করার জন্য আপনাকে ধন্যবাদ। আমরা আমাদের ব্যবহারকারীদের ব্যক্তিগত গোপনীয়তা ও ডেটা সুরক্ষাকে সর্বোচ্চ অগ্রাধিকার দিই। এই নীতিমালায় ব্যাখ্যা করা হয়েছে কীভাবে আপনার গেমিং অভিজ্ঞতা নিরাপদ রাখা হয়।"
                        } else {
                            "Thank you for playing Alif Water Sort Pro. We are committed to protecting your privacy and ensuring a safe gaming experience. This policy explains how we handle player information and privacy."
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Information Collection
                    PolicySectionCard(
                        icon = Icons.Default.Security,
                        title = if (isBengali) "২. তথ্য সংগ্রহ ও ব্যবহার" else "2. Data Collection & Use",
                        content = if (isBengali) {
                            "• সংগৃহীত তথ্য: আপনার অ্যাকাউন্টের ইউজারনেম, নির্বাচিত অবতার, গেমের অগ্রগতি (লেভেল ও স্টার), এবং অর্জিত কয়েন।\n• পাসওয়ার্ড সুরক্ষা: আপনার পাসওয়ার্ড কোনো প্লেইন টেক্সট হিসেবে নয়, বরং আধুনিক SHA-256 ক্রিপ্টোগ্রাফিক অ্যালগরিদম দিয়ে ডিভাইসে নিরাপদভাবে হ্যাশ করে সংরক্ষণ করা হয়।\n• আমরা কোনো সংবেদনশীল তথ্য (যেমন: জাতীয় পরিচয়পত্র, ফোন নম্বর, ব্যাংক তথ্য বা ভৌগোলিক লাইভ অবস্থান) সংগ্রহ করি না।"
                        } else {
                            "• Collected Data: Username, chosen avatar, game progress (levels & stars), and earned coins.\n• Security: Passwords are encrypted using SHA-256 cryptographic hashing.\n• No sensitive personal data (e.g. phone numbers, banking details, or real-time GPS location) is collected."
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ads & Third Party
                    PolicySectionCard(
                        icon = Icons.Default.Lock,
                        title = if (isBengali) "৩. বিজ্ঞাপন ও থার্ড-পার্টি সার্ভিস" else "3. Advertising & AdMob",
                        content = if (isBengali) {
                            "• গেমটি বিনামূল্যে খেলার সুযোগ বজায় রাখতে আমরা Google AdMob বিজ্ঞাপন নেটওয়ার্ক ব্যবহার করি।\n• গুগল অ্যাডমব প্রাসঙ্গিক বিজ্ঞাপন পরিবেশন ও বিজ্ঞাপন প্রতারণা রোধের জন্য গুগলের অফিসিয়াল নীতি অনুযায়ী ডিভাইসের বিজ্ঞাপন আইডি (Advertising ID) নিরাপদে প্রসেস করতে পারে।"
                        } else {
                            "• We use Google AdMob to display banner and rewarded ads to keep the game free for all players.\n• Google may process anonymous device advertising identifiers in compliance with Google Play Developer policies."
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Children Privacy
                    PolicySectionCard(
                        icon = Icons.Default.Shield,
                        title = if (isBengali) "৪. শিশুদের গোপনীয়তা (Family Friendly)" else "4. Children's Privacy",
                        content = if (isBengali) {
                            "• আমাদের গেমটি সকল বয়সের মানুষের জন্য সম্পূর্ণ উপযুক্ত ও পারিবারিক (Family Safe)।\n• আমরা জেনেশুনে ১৩ বছরের কম বয়সী শিশুদের কাছ থেকে কোনো ব্যক্তিগত তথ্য সংগ্রহ করি না। গেমটিতে কোনো ক্ষতিকর বা অনুপযুক্ত কনটেন্ট নেই।"
                        } else {
                            "• Our game is designed for players of all ages and complies with Google Play Families Policy.\n• We do not knowingly collect personal information from children under 13."
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // User Rights & Deletion
                    PolicySectionCard(
                        icon = Icons.Default.Security,
                        title = if (isBengali) "৫. ডেটা নিয়ন্ত্রণ ও মুছে ফেলা" else "5. Data Control & Deletion",
                        content = if (isBengali) {
                            "যেহেতু সমস্ত গেম ডেটা ব্যবহারকারীর লোকাল ডিভাইসে সংরক্ষিত থাকে, তাই প্লেয়ার চাইলে যেকোনো সময় লগআউট করতে পারেন অথবা ডিভাইসের Settings > Apps > Alif Water Sort Pro থেকে \"Clear Data\" করে সমস্ত রেকর্ড সম্পূর্ণরূপে মুছে ফেলতে পারেন।"
                        } else {
                            "All progress is stored locally on your device. You can log out or completely erase your data anytime via Android Settings > Apps > Alif Water Sort Pro > Clear Storage."
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact
                    PolicySectionCard(
                        icon = Icons.Default.Email,
                        title = if (isBengali) "৬. যোগাযোগ ও সহায়তা" else "6. Contact Us",
                        content = if (isBengali) {
                            "এই গোপনীয়তা নীতি সম্পর্কে কোনো জিজ্ঞাসা বা সহায়তা প্রয়োজন হলে যোগাযোগ করুন:\n📧 ইমেইল: daimondtopup32@gmail.com\nসর্বশেষ হালনাগাদ: ২০২৬"
                        } else {
                            "If you have any questions or feedback regarding this policy, feel free to reach out:\n📧 Email: daimondtopup32@gmail.com\nLast Updated: 2026"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Close Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("policy_close_button")
                ) {
                    Text(
                        text = if (isBengali) "বুঝেছি, বন্ধ করুন" else "I Understand, Close",
                        color = Color(0xFF061E38),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0x1AFFFFFF),
        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF38BDF8)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xEEFFFFFF)
            )
        }
    }
}
