package com.example.model

import androidx.compose.ui.graphics.Color

enum class LiquidColor(val color: Color, val displayName: String, val hexCode: String) {
    RED(Color(0xFFFF1744), "Ruby Red", "#FF1744"),
    BLUE(Color(0xFF2979FF), "Ocean Blue", "#2979FF"),
    GREEN(Color(0xFF00E676), "Emerald Green", "#00E676"),
    YELLOW(Color(0xFFFFEA00), "Neon Yellow", "#FFEA00"),
    PURPLE(Color(0xFFD500F9), "Electric Violet", "#D500F9"),
    ORANGE(Color(0xFFFF9100), "Sunset Orange", "#FF9100"),
    CYAN(Color(0xFF00E5FF), "Cyber Cyan", "#00E5FF"),
    PINK(Color(0xFFFF4081), "Hot Pink", "#FF4081"),
    TEAL(Color(0xFF1DE9B6), "Mint Teal", "#1DE9B6"),
    LIME(Color(0xFFAEEA00), "Acid Lime", "#AEEA00"),
    INDIGO(Color(0xFF3D5AFE), "Royal Indigo", "#3D5AFE"),
    MAGENTA(Color(0xFFF50057), "Vivid Magenta", "#F50057");

    companion object {
        fun fromName(name: String): LiquidColor {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: RED
        }
    }
}
