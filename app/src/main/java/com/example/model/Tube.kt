package com.example.model

data class Tube(
    val id: Int,
    val segments: List<LiquidColor> = emptyList(),
    val capacity: Int = 4,
    val isExtraTube: Boolean = false
) {
    val isFull: Boolean get() = segments.size >= capacity
    val isEmpty: Boolean get() = segments.isEmpty()
    val availableSpace: Int get() = capacity - segments.size
    val topColor: LiquidColor? get() = segments.lastOrNull()

    val isCompleted: Boolean
        get() = isFull && segments.all { it == segments.first() }

    fun topColorCount(): Int {
        if (isEmpty) return 0
        val target = topColor ?: return 0
        var count = 0
        for (i in segments.indices.reversed()) {
            if (segments[i] == target) count++ else break
        }
        return count
    }

    fun canAccept(color: LiquidColor): Boolean {
        if (isFull) return false
        if (isEmpty) return true
        return topColor == color
    }
}
