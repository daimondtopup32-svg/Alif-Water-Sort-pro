package com.example.model

data class Tube(
    val id: Int,
    val liquids: List<LiquidColor> = emptyList(),
    val capacity: Int = 4,
    val isExtraTube: Boolean = false
) {
    val isFull: Boolean get() = liquids.size >= capacity
    val isEmpty: Boolean get() = liquids.isEmpty()
    val freeSpace: Int get() = (capacity - liquids.size).coerceAtLeast(0)

    val topColor: LiquidColor? get() = liquids.lastOrNull()

    /**
     * How many contiguous liquid blocks of the same color are on top of this tube.
     */
    val topColorCount: Int
        get() {
            if (liquids.isEmpty()) return 0
            val top = liquids.last()
            var count = 0
            for (i in liquids.indices.reversed()) {
                if (liquids[i] == top) {
                    count++
                } else {
                    break
                }
            }
            return count
        }

    /**
     * Check if this tube is fully completed (filled to capacity with identical color).
     */
    val isCompleted: Boolean
        get() = isFull && liquids.isNotEmpty() && liquids.all { it.id == liquids.first().id }

    val hasSingleColor: Boolean
        get() = liquids.isNotEmpty() && liquids.all { it.id == liquids.first().id }

    fun canAccept(color: LiquidColor): Boolean {
        return !isFull && (isEmpty || topColor?.id == color.id)
    }

    /**
     * Creates a copy after popping up to [count] liquid units from top.
     */
    fun popTop(count: Int): Pair<Tube, List<LiquidColor>> {
        val actualCount = count.coerceAtMost(liquids.size)
        val remaining = liquids.dropLast(actualCount)
        val popped = liquids.takeLast(actualCount)
        return copy(liquids = remaining) to popped
    }

    /**
     * Creates a copy after pushing liquid units to the top.
     */
    fun push(newLiquids: List<LiquidColor>): Tube {
        val available = freeSpace
        val toAdd = newLiquids.take(available)
        return copy(liquids = liquids + toAdd)
    }
}
