package com.example.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class TubeSkin(val titleEn: String, val titleBn: String, val coinCost: Int) {
    CLASSIC("Classic Tube", "ক্লাসিক টিউব", 0),
    BEAKER("Beaker", "বিকার", 5),
    FLASK("Flask", "ফ্লাস্ক", 10),
    NEON("Neon Cyber", "নিয়ন সাইবার", 15)
}

enum class BackgroundTheme(
    val titleEn: String,
    val titleBn: String,
    val topColor: Color,
    val bottomColor: Color,
    val accentGlow: Color,
    val orbColor: Color,
    val coinCost: Int
) {
    AURORA_NIGHT(
        titleEn = "Aurora Sky",
        titleBn = "অরোরা আকাশ",
        topColor = Color(0xFF16203B),     // rich vibrant night sky
        bottomColor = Color(0xFF090D1A),  // deep modern edge
        accentGlow = Color(0xFF00E5FF),
        orbColor = Color(0x4438BDF8),
        coinCost = 0
    ),
    OCEAN_LAGOON(
        titleEn = "Ocean Lagoon",
        titleBn = "সাগর নীল",
        topColor = Color(0xFF0E3D60),     // sparkling tropical deep ocean
        bottomColor = Color(0xFF051929),
        accentGlow = Color(0xFF00F5D4),
        orbColor = Color(0x4000E5FF),
        coinCost = 4
    ),
    SUNSET_TWILIGHT(
        titleEn = "Sunset Twilight",
        titleBn = "সানসেট গোধূলি",
        topColor = Color(0xFF3F1653),     // warm magical evening twilight
        bottomColor = Color(0xFF170622),
        accentGlow = Color(0xFFFF4081),
        orbColor = Color(0x40E040FB),
        coinCost = 8
    ),
    EMERALD_ZEN(
        titleEn = "Emerald Forest",
        titleBn = "সবুজ অরণ্য",
        topColor = Color(0xFF0E4330),     // peaceful deep emerald jade
        bottomColor = Color(0xFF051A13),
        accentGlow = Color(0xFF00E676),
        orbColor = Color(0x4069F0AE),
        coinCost = 12
    ),
    COSMIC_DREAM(
        titleEn = "Cosmic Purple",
        titleBn = "কসমিক পার্পল",
        topColor = Color(0xFF2C0F50),     // rich galactic starlight violet
        bottomColor = Color(0xFF0F041D),
        accentGlow = Color(0xFFB388FF),
        orbColor = Color(0x40EA80FC),
        coinCost = 15
    );

    fun getBackgroundBrush(): Brush {
        return Brush.verticalGradient(
            colors = listOf(topColor, bottomColor)
        )
    }
}
