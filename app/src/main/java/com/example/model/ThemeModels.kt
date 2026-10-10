package com.example.model

enum class TubeSkin(val displayName: String, val cornerRadius: Float, val glassTint: Long) {
    CLASSIC("Classic Beaker", 28f, 0x1AFFFFFF),
    CYBERPUNK("Cyber Flacon", 12f, 0x2200E5FF),
    MAGIC_POTION("Potion Bottle", 36f, 0x22D500F9),
    CRYSTAL("Crystal Prism", 4f, 0x25FFFFFF)
}

enum class BackgroundTheme(val displayName: String, val primaryColor: Long, val secondaryColor: Long) {
    DEEP_NEBULA("Deep Nebula", 0xFF0D1B2A, 0xFF1B263B),
    MIDNIGHT_CYBER("Midnight Cyber", 0xFF0A0E17, 0xFF14213D),
    SUNSET_GLOW("Sunset Glow", 0xFF1A0B2E, 0xFF2D124D),
    EMERALD_FOREST("Emerald Deep", 0xFF051C14, 0xFF0B3828)
}
