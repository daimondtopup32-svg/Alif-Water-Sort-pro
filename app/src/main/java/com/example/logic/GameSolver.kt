package com.example.logic

import com.example.model.Tube

data class MoveHint(val fromTubeId: Int, val toTubeId: Int)

class GameSolver {
    fun findNextMove(tubes: List<Tube>): MoveHint? {
        // Priority 1: Move to a tube that already contains the matching color to complete it
        for (i in tubes.indices) {
            val from = tubes[i]
            if (from.isEmpty || from.isCompleted) continue

            for (j in tubes.indices) {
                if (i == j) continue
                val to = tubes[j]
                if (!to.isEmpty && !to.isFull && to.topColor == from.topColor) {
                    return MoveHint(from.id, to.id)
                }
            }
        }

        // Priority 2: Move to an empty tube if it exposes a different color underneath
        for (i in tubes.indices) {
            val from = tubes[i]
            if (from.isEmpty || from.isCompleted) continue
            // Only move to empty if the tube has more than 1 distinct color
            val hasMultipleColors = from.segments.distinct().size > 1
            if (!hasMultipleColors) continue

            for (j in tubes.indices) {
                if (i == j) continue
                val to = tubes[j]
                if (to.isEmpty) {
                    return MoveHint(from.id, to.id)
                }
            }
        }
        return null
    }
}
