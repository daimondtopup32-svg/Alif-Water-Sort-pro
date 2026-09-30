package com.example.logic

import com.example.model.Tube
import java.util.ArrayDeque

data class Move(val fromIndex: Int, val toIndex: Int)

object GameSolver {

    /**
     * Checks if the given tubes are completely sorted.
     */
    fun isWon(tubes: List<Tube>): Boolean {
        return tubes.all { it.isEmpty || it.isCompleted }
    }

    /**
     * Returns true if there are ANY valid moves possible from the current board.
     */
    fun hasValidMoves(tubes: List<Tube>): Boolean {
        for (i in tubes.indices) {
            val src = tubes[i]
            if (src.isEmpty || src.isCompleted) continue
            val topColor = src.topColor ?: continue

            for (j in tubes.indices) {
                if (i == j) continue
                val dst = tubes[j]
                if (dst.canAccept(topColor)) {
                    // Avoid pouring from pure tube to empty tube
                    if (dst.isEmpty && src.liquids.all { it.id == topColor.id }) continue
                    return true
                }
            }
        }
        return false
    }

    /**
     * Finds the next best move (hint) using BFS.
     * Returns null if solved or no solution found within search limit.
     */
    fun findHint(tubes: List<Tube>): Move? {
        if (isWon(tubes)) return null

        data class StateNode(
            val tubes: List<Tube>,
            val firstMove: Move?,
            val depth: Int
        )

        val queue = ArrayDeque<StateNode>()
        val visited = HashSet<String>()

        fun encodeState(t: List<Tube>): String {
            // Canonical string representation
            return t.map { tube ->
                tube.liquids.joinToString(",") { it.id.toString() }
            }.sorted().joinToString("|")
        }

        queue.add(StateNode(tubes, null, 0))
        visited.add(encodeState(tubes))

        val maxDepth = 12
        var searchBudget = 1500 // Max iterations to keep UI silky smooth

        var fallbackMove: Move? = null

        while (queue.isNotEmpty() && searchBudget-- > 0) {
            val current = queue.poll() ?: break

            if (isWon(current.tubes) && current.firstMove != null) {
                return current.firstMove
            }

            if (current.depth >= maxDepth) continue

            // Generate next moves
            for (i in current.tubes.indices) {
                val src = current.tubes[i]
                if (src.isEmpty || src.isCompleted) continue
                val topColor = src.topColor ?: continue

                for (j in current.tubes.indices) {
                    if (i == j) continue
                    val dst = current.tubes[j]

                    if (!dst.canAccept(topColor)) continue

                    // Heuristic prune: don't move from a tube where all items are same color to an empty tube
                    if (dst.isEmpty && src.liquids.all { it.id == topColor.id }) continue

                    val move = Move(i, j)
                    if (fallbackMove == null) {
                        fallbackMove = current.firstMove ?: move
                    }

                    // Apply move
                    val countToMove = src.topColorCount.coerceAtMost(dst.freeSpace)
                    if (countToMove <= 0) continue

                    val (newSrc, popped) = src.popTop(countToMove)
                    val newDst = dst.push(popped)

                    val nextTubes = current.tubes.toMutableList().apply {
                        this[i] = newSrc
                        this[j] = newDst
                    }

                    val stateKey = encodeState(nextTubes)
                    if (!visited.contains(stateKey)) {
                        visited.add(stateKey)
                        val initialMove = current.firstMove ?: move
                        if (isWon(nextTubes)) {
                            return initialMove
                        }
                        queue.add(StateNode(nextTubes, initialMove, current.depth + 1))
                    }
                }
            }
        }

        return fallbackMove
    }
}
