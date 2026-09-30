package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.LevelGenerator
import kotlinx.coroutines.launch

private data class LevelRange(
    val titleBn: String,
    val titleEn: String,
    val startLevel: Int,
    val endLevel: Int,
    val tagColor: Color
)

@Composable
fun LevelSelectDialog(
    show: Boolean,
    currentLevel: Int,
    maxUnlockedLevel: Int,
    isBengali: Boolean,
    getStarsForLevel: (Int) -> Int,
    onSelectLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val totalLevels = 500
    val ranges = remember {
        listOf(
            LevelRange("১-৫০ (সহজ)", "1-50 (Easy)", 1, 50, Color(0xFF00E676)),
            LevelRange("৫১-১০০ (মাঝারি)", "51-100 (Medium)", 51, 100, Color(0xFFFFD600)),
            LevelRange("১০১-১৫০ (চ্যালেঞ্জ)", "101-150 (Challenge)", 101, 150, Color(0xFFFF9100)),
            LevelRange("১৫১-২০০ (কঠিন 🔥)", "151-200 (Hard 🔥)", 151, 200, Color(0xFFFF5722)),
            LevelRange("২০১-২৫০ (এক্সপার্ট)", "201-250 (Expert)", 201, 250, Color(0xFFFF1744)),
            LevelRange("২৫১-৩০০ (মাস্টার 👑)", "251-300 (Master 👑)", 251, 300, Color(0xFFE040FB)),
            LevelRange("৩০১-৩৫০ (গ্র্যান্ডমাস্টার)", "301-350 (Grandmaster)", 301, 350, Color(0xFF7C4DFF)),
            LevelRange("৩৫১-৪০০ (লিজেন্ড ⚡)", "351-400 (Legend ⚡)", 351, 400, Color(0xFF00E5FF)),
            LevelRange("৪০১-৪৫০ (চ্যাম্পিয়ন)", "401-450 (Champion)", 401, 450, Color(0xFF69F0AE)),
            LevelRange("৪৫১-৫০০ (আল্টিমেট 🏆)", "451-500 (Ultimate 🏆)", 451, 500, Color(0xFFFFD700))
        )
    }

    // Determine default selected tab based on currentLevel
    val initialTabIndex = ranges.indexOfFirst { currentLevel in it.startLevel..it.endLevel }.coerceAtLeast(0)
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.86f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF16203B), Color(0xFF0B1124))
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color(0x4400E5FF),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBengali) "লেভেল নির্বাচন করুন (১-৫০০)" else "Select Level (1-500)",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBengali) "মোট ৫০০টি পাজল লেভেল প্রস্তুত" else "500 Puzzle Levels Ready",
                            fontSize = 11.sp,
                            color = Color(0xAAFFFFFF)
                        )
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

                Spacer(modifier = Modifier.height(10.dp))

                // Stage Filter Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = ranges[selectedTabIndex].tagColor,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    ranges.forEachIndexed { index, range ->
                        val isSelected = (selectedTabIndex == index)
                        Tab(
                            selected = isSelected,
                            onClick = {
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    gridState.scrollToItem(0)
                                }
                            },
                            text = {
                                Text(
                                    text = if (isBengali) range.titleBn else range.titleEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) range.tagColor else Color(0x99FFFFFF)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val activeRange = ranges.getOrNull(selectedTabIndex) ?: ranges[0]
                val levelsInRange = (activeRange.startLevel..activeRange.endLevel).toList()

                // Levels Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    state = gridState,
                    contentPadding = PaddingValues(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(levelsInRange.size) { i ->
                        val levelNum = levelsInRange[i]
                        val isUnlocked = levelNum <= maxUnlockedLevel
                        val isCurrent = levelNum == currentLevel
                        val stars = getStarsForLevel(levelNum)
                        val diff = LevelGenerator.getDifficultyForLevel(levelNum)

                        Box(
                            modifier = Modifier
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when {
                                        isCurrent -> Color(0xFF00E5FF)
                                        isUnlocked -> Color(0xFF22325E)
                                        else -> Color(0x18FFFFFF)
                                    }
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = when {
                                        isCurrent -> Color.White
                                        isUnlocked -> Color(diff.colorHex).copy(alpha = 0.6f)
                                        else -> Color(0x15FFFFFF)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = isUnlocked) {
                                    onSelectLevel(levelNum)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0x55FFFFFF),
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$levelNum",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isCurrent) Color(0xFF0D1B2A) else Color.White
                                    )

                                    if (stars > 0) {
                                        Row(
                                            modifier = Modifier.padding(top = 1.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            for (s in 1..stars) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (isCurrent) Color(0xFFD84315) else Color(0xFFFFD600),
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = if (isBengali) diff.titleBn else diff.titleEn,
                                            fontSize = 8.sp,
                                            color = if (isCurrent) Color(0xFF102A43) else Color(diff.colorHex),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
