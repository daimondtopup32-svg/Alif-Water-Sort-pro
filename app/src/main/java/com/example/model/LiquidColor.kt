package com.example.model

import androidx.compose.ui.graphics.Color

enum class LiquidPalette(val titleEn: String, val titleBn: String) {
    CLASSIC("Classic Vibrant", "ক্লাসিক ভাইব্রেন্ট"),
    NEON("Neon Glow", "নিয়ন গ্লো"),
    PASTEL("Pastel Dream", "প্যাস্টেল ড্রিম"),
    GEMSTONE("Precious Gem", "জেমস্টোন")
}

data class LiquidColor(
    val id: Int,
    val nameEn: String,
    val nameBn: String,
    val primaryColor: Color,
    val gradientColor: Color,
    val bubbleColor: Color,
    val glowColor: Color
)

object ColorManager {
    // 10 distinct, highly distinguishable, beautiful liquid colors
    val ALL_COLORS = listOf(
        LiquidColor(
            id = 0,
            nameEn = "Crimson Red",
            nameBn = "লাল",
            primaryColor = Color(0xFFFF2A55),
            gradientColor = Color(0xFFC7002B),
            bubbleColor = Color(0xFFFF8DA3),
            glowColor = Color(0x66FF2A55)
        ),
        LiquidColor(
            id = 1,
            nameEn = "Sky Blue",
            nameBn = "নীল",
            primaryColor = Color(0xFF00B0FF),
            gradientColor = Color(0xFF0070BA),
            bubbleColor = Color(0xFF80D8FF),
            glowColor = Color(0x6600B0FF)
        ),
        LiquidColor(
            id = 2,
            nameEn = "Neon Green",
            nameBn = "সবুজ",
            primaryColor = Color(0xFF00E676),
            gradientColor = Color(0xFF009640),
            bubbleColor = Color(0xFFB9F6CA),
            glowColor = Color(0x6600E676)
        ),
        LiquidColor(
            id = 3,
            nameEn = "Amber Yellow",
            nameBn = "হলুদ",
            primaryColor = Color(0xFFFFD600),
            gradientColor = Color(0xFFFF9100),
            bubbleColor = Color(0xFFFFF9C4),
            glowColor = Color(0x66FFD600)
        ),
        LiquidColor(
            id = 4,
            nameEn = "Electric Violet",
            nameBn = "বেগুনী",
            primaryColor = Color(0xFFA838FF),
            gradientColor = Color(0xFF6A00C9),
            bubbleColor = Color(0xFFE1BEE7),
            glowColor = Color(0x66A838FF)
        ),
        LiquidColor(
            id = 5,
            nameEn = "Tangerine Orange",
            nameBn = "কমলা",
            primaryColor = Color(0xFFFF6D00),
            gradientColor = Color(0xFFD84315),
            bubbleColor = Color(0xFFFFCC80),
            glowColor = Color(0x66FF6D00)
        ),
        LiquidColor(
            id = 6,
            nameEn = "Deep Cyan",
            nameBn = "আসমানী",
            primaryColor = Color(0xFF00E5FF),
            gradientColor = Color(0xFF0097A7),
            bubbleColor = Color(0xFF84FFFF),
            glowColor = Color(0x6600E5FF)
        ),
        LiquidColor(
            id = 7,
            nameEn = "Hot Pink",
            nameBn = "গোলাপী",
            primaryColor = Color(0xFFFF4081),
            gradientColor = Color(0xFFC51162),
            bubbleColor = Color(0xFFFF80AB),
            glowColor = Color(0x66FF4081)
        ),
        LiquidColor(
            id = 8,
            nameEn = "Emerald Teal",
            nameBn = "পান্না সবুজ",
            primaryColor = Color(0xFF00BFA5),
            gradientColor = Color(0xFF00695C),
            bubbleColor = Color(0xFFA7FFEB),
            glowColor = Color(0x6600BFA5)
        ),
        LiquidColor(
            id = 9,
            nameEn = "Coffee Brown",
            nameBn = "বাদামী",
            primaryColor = Color(0xFF8D6E63),
            gradientColor = Color(0xFF4E342E),
            bubbleColor = Color(0xFFD7CCC8),
            glowColor = Color(0x668D6E63)
        )
    )

    fun getColorById(id: Int): LiquidColor {
        return ALL_COLORS.getOrNull(id) ?: ALL_COLORS[0]
    }
}
