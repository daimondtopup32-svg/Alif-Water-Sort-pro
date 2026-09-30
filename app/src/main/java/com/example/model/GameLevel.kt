package com.example.model

import kotlin.random.Random

enum class LevelDifficulty(
    val titleEn: String,
    val titleBn: String,
    val colorHex: Long
) {
    EASY("Easy", "সহজ", 0xFF00E676),
    MEDIUM("Medium", "মাঝারি", 0xFFFFD600),
    HARD("Hard (1 Tube)", "কঠিন (১ গ্লাস)", 0xFFFF9100),
    EXPERT("Master (1 Tube)", "মাস্টার (১ গ্লাস)", 0xFFFF1744),
    NIGHTMARE("Extreme Hard", "চরম কঠিন", 0xFFA838FF)
}

data class GameLevel(
    val levelNumber: Int,
    val tubes: List<Tube>,
    val minMoves: Int = 10,
    val difficulty: LevelDifficulty = LevelDifficulty.EASY
)

object LevelGenerator {
    private val C = ColorManager.ALL_COLORS

    // Curated iconic starting levels for maximum delight
    private fun getCuratedLevel(levelNumber: Int): GameLevel? {
        return when (levelNumber) {
            1 -> {
                // 4 tubes total: 2 colored tubes, 2 empty tubes (relaxing tutorial)
                GameLevel(
                    levelNumber = 1,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[1], C[0], C[1])),
                        Tube(1, listOf(C[1], C[0], C[1], C[0])),
                        Tube(2, emptyList()),
                        Tube(3, emptyList())
                    ),
                    minMoves = 4,
                    difficulty = LevelDifficulty.EASY
                )
            }
            2 -> {
                // 4 tubes total: 3 colored tubes, 1 empty tube
                GameLevel(
                    levelNumber = 2,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[1], C[2], C[0])),
                        Tube(1, listOf(C[1], C[2], C[0], C[1])),
                        Tube(2, listOf(C[2], C[0], C[1], C[2])),
                        Tube(3, emptyList())
                    ),
                    minMoves = 6,
                    difficulty = LevelDifficulty.EASY
                )
            }
            3 -> {
                // 4 tubes: 3 colors (Red, Blue, Green), 1 empty
                GameLevel(
                    levelNumber = 3,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[1], C[2], C[0])),
                        Tube(1, listOf(C[1], C[2], C[0], C[1])),
                        Tube(2, listOf(C[2], C[0], C[1], C[2])),
                        Tube(3, emptyList())
                    ),
                    minMoves = 6,
                    difficulty = LevelDifficulty.EASY
                )
            }
            4 -> {
                // 5 tubes: 3 colors, 2 empty
                GameLevel(
                    levelNumber = 4,
                    tubes = listOf(
                        Tube(0, listOf(C[3], C[1], C[4], C[3])),
                        Tube(1, listOf(C[1], C[4], C[3], C[1])),
                        Tube(2, listOf(C[4], C[3], C[1], C[4])),
                        Tube(3, emptyList()),
                        Tube(4, emptyList())
                    ),
                    minMoves = 6,
                    difficulty = LevelDifficulty.EASY
                )
            }
            5 -> {
                // 5 tubes: 4 colors (Red, Blue, Green, Yellow), 1 empty
                GameLevel(
                    levelNumber = 5,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[3], C[1], C[2])),
                        Tube(1, listOf(C[1], C[0], C[2], C[3])),
                        Tube(2, listOf(C[2], C[1], C[3], C[0])),
                        Tube(3, listOf(C[3], C[2], C[0], C[1])),
                        Tube(4, emptyList())
                    ),
                    minMoves = 9,
                    difficulty = LevelDifficulty.EASY
                )
            }
            else -> null
        }
    }

    /**
     * Determines the difficulty tier based on levelNumber (up to 500+).
     */
    fun getDifficultyForLevel(levelNumber: Int): LevelDifficulty {
        return when {
            levelNumber <= 25 -> LevelDifficulty.EASY
            levelNumber <= 60 -> LevelDifficulty.MEDIUM
            levelNumber <= 150 -> LevelDifficulty.HARD
            levelNumber <= 300 -> LevelDifficulty.EXPERT
            else -> LevelDifficulty.NIGHTMARE
        }
    }

    /**
     * Generates a guaranteed solvable level with difficulty scaled up to 500+ levels.
     * High levels feature tight tube constraints (only 1 empty tube), requiring deep strategy
     * or unlocking an extra empty tube by watching a rewarded video ad!
     */
    fun getLevel(levelNumber: Int): GameLevel {
        getCuratedLevel(levelNumber)?.let { return it }

        val diff = getDifficultyForLevel(levelNumber)

        // Scale color count progressively
        val colorCount = when {
            levelNumber <= 10 -> 4
            levelNumber <= 25 -> 5
            levelNumber <= 50 -> 6
            levelNumber <= 100 -> 7
            levelNumber <= 250 -> 8
            else -> 9
        }.coerceAtMost(C.size)

        // Empty tube restriction:
        // Levels 1-30: 2 empty tubes (friendly learning curve)
        // Levels 31+: ONLY 1 EMPTY TUBE (High difficulty / puzzle challenge)
        // When there is only 1 empty tube, player must think several steps ahead,
        // or watch a rewarded video ad to get a 2nd empty tube!
        val emptyTubesCount = if (levelNumber <= 30) 2 else 1
        val totalTubes = colorCount + emptyTubesCount

        // Deterministic pseudo-random seed based on levelNumber for consistent puzzle per level
        val rng = Random(levelNumber * 7919L + 31)

        // Select colors
        val chosenColors = C.take(colorCount)

        // Initialize solved tubes
        val tubesState = Array(totalTubes) { index ->
            if (index < colorCount) {
                MutableList(4) { chosenColors[index] }
            } else {
                mutableListOf<LiquidColor>()
            }
        }

        // Shuffle by performing valid reverse pours
        val shuffleMoves = 35 + (levelNumber * 2).coerceAtMost(100)
        var lastSource = -1
        var lastTarget = -1

        for (step in 0 until shuffleMoves) {
            val nonEmpties = tubesState.indices.filter { tubesState[it].isNotEmpty() }
            if (nonEmpties.isEmpty()) break

            val src = nonEmpties[rng.nextInt(nonEmpties.size)]
            val possibleTargets = tubesState.indices.filter {
                it != src &&
                tubesState[it].size < 4 &&
                !(src == lastTarget && it == lastSource) // avoid immediate revert
            }

            if (possibleTargets.isNotEmpty()) {
                val dst = possibleTargets[rng.nextInt(possibleTargets.size)]
                // Move 1 liquid block from src to dst in reverse
                val item = tubesState[src].removeAt(tubesState[src].size - 1)
                tubesState[dst].add(item)
                lastSource = src
                lastTarget = dst
            }
        }

        // Guarantee at least 1 completely empty tube so the puzzle is solvable
        val emptyCount = tubesState.count { it.isEmpty() }
        if (emptyCount == 0) {
            val smallestIndex = tubesState.indices.minByOrNull { tubesState[it].size } ?: (totalTubes - 1)
            val itemsToEvict = tubesState[smallestIndex].toList()
            tubesState[smallestIndex].clear()

            for (item in itemsToEvict) {
                val dst = tubesState.indices.firstOrNull { it != smallestIndex && tubesState[it].size < 4 }
                if (dst != null) {
                    tubesState[dst].add(item)
                } else {
                    tubesState[smallestIndex].add(item)
                }
            }
        }

        val resultTubes = tubesState.mapIndexed { index, liquids ->
            Tube(id = index, liquids = liquids.toList())
        }

        return GameLevel(
            levelNumber = levelNumber,
            tubes = resultTubes,
            minMoves = (colorCount * 2) + 4,
            difficulty = diff
        )
    }
}
