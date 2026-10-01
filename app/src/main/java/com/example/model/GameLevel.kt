package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

enum class LevelDifficulty(
    val titleBn: String,
    val titleEn: String,
    val colorHex: Long
) {
    EASY("সহজ", "Easy", 0xFF00E676),
    MEDIUM("মাঝারি", "Medium", 0xFFFFD600),
    HARD("কঠিন 🔥", "Hard 🔥", 0xFFFF9100),
    EXPERT("এক্সপার্ট", "Expert", 0xFFFF1744),
    NIGHTMARE("চরম কঠিন 💀", "Nightmare 💀", 0xFFE040FB)
}

data class GameLevel(
    val levelNumber: Int,
    val tubes: List<Tube>,
    val minMoves: Int,
    val difficulty: LevelDifficulty
)

object LevelGenerator {

    private val C = ColorManager.ALL_COLORS

    // Curated iconic starting levels (Levels 1-5)
    // Level 1 is the tutorial. From Level 2 onward, the game immediately gets harder with only 1 empty tube!
    private fun getCuratedLevel(levelNumber: Int): GameLevel? {
        return when (levelNumber) {
            1 -> {
                // Tutorial level: 2 colored tubes, 2 empty tubes (easy 4 tubes)
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
                // Immediately gets hard! 3 colored tubes, only 1 empty tube
                GameLevel(
                    levelNumber = 2,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[1], C[2], C[0])),
                        Tube(1, listOf(C[1], C[2], C[0], C[1])),
                        Tube(2, listOf(C[2], C[0], C[1], C[2])),
                        Tube(3, emptyList())
                    ),
                    minMoves = 6,
                    difficulty = LevelDifficulty.MEDIUM
                )
            }
            3 -> {
                // 3 colors, only 1 empty tube with tighter scramble
                GameLevel(
                    levelNumber = 3,
                    tubes = listOf(
                        Tube(0, listOf(C[2], C[1], C[0], C[2])),
                        Tube(1, listOf(C[0], C[2], C[1], C[0])),
                        Tube(2, listOf(C[1], C[0], C[2], C[1])),
                        Tube(3, emptyList())
                    ),
                    minMoves = 7,
                    difficulty = LevelDifficulty.MEDIUM
                )
            }
            4 -> {
                // 4 colors (Red, Blue, Green, Yellow), only 1 empty tube
                GameLevel(
                    levelNumber = 4,
                    tubes = listOf(
                        Tube(0, listOf(C[0], C[3], C[1], C[2])),
                        Tube(1, listOf(C[1], C[2], C[3], C[0])),
                        Tube(2, listOf(C[2], C[0], C[1], C[3])),
                        Tube(3, listOf(C[3], C[1], C[2], C[0])),
                        Tube(4, emptyList())
                    ),
                    minMoves = 8,
                    difficulty = LevelDifficulty.HARD
                )
            }
            5 -> {
                // 5 tubes: 4 colors, deeply interleaved, only 1 empty tube
                GameLevel(
                    levelNumber = 5,
                    tubes = listOf(
                        Tube(0, listOf(C[3], C[0], C[2], C[1])),
                        Tube(1, listOf(C[2], C[3], C[1], C[0])),
                        Tube(2, listOf(C[1], C[2], C[0], C[3])),
                        Tube(3, listOf(C[0], C[1], C[3], C[2])),
                        Tube(4, emptyList())
                    ),
                    minMoves = 10,
                    difficulty = LevelDifficulty.HARD
                )
            }
            else -> null
        }
    }

    /**
     * Determines the difficulty tier based on levelNumber.
     */
    fun getDifficultyForLevel(levelNumber: Int): LevelDifficulty {
        return when {
            levelNumber == 1 -> LevelDifficulty.EASY
            levelNumber <= 5 -> LevelDifficulty.MEDIUM
            levelNumber <= 10 -> LevelDifficulty.HARD
            levelNumber <= 20 -> LevelDifficulty.EXPERT
            else -> LevelDifficulty.NIGHTMARE
        }
    }

    /**
     * Generates levels with custom progression according to user specifications:
     * 1. "১ লেভেল করা শেষ হবে আর গেমটা কঠিন হবে":
     *    Level 1 is easy onboarding. Level 2+ immediately becomes hard with only 1 empty tube.
     * 2. Levels 11-20: Ultra-hard bottleneck puzzles (distinct tops, quick deadlock after 1 move).
     * 3. "২০ লেভেলের পর একটা গ্লাসও খালি থাকবে না সবগুলোতে রং থাকবে":
     *    After level 20 (Level 21+), ZERO tubes are empty! Every single tube is filled with colors!
     *    The player MUST watch rewarded video ads to add 3 to 4 empty glasses in order to play and win!
     */
    fun getLevel(levelNumber: Int): GameLevel {
        getCuratedLevel(levelNumber)?.let { return it }

        val diff = getDifficultyForLevel(levelNumber)

        // =========================================================================
        // AFTER LEVEL 20: 0 EMPTY TUBES! ALL TUBES HAVE COLORED LIQUIDS!
        // As requested: "২০ লেভেলের পর একটা গ্লাসও খালি থাকবে না সবগুলোতে রং থাকবে"
        // =========================================================================
        if (levelNumber > 20) {
            val colorCount = when {
                levelNumber <= 50 -> 8
                else -> 9
            }.coerceAtMost(C.size)

            val rng = Random(levelNumber * 104729L + 73)
            val chosenColors = C.take(colorCount)

            // ALL tubes have colors! Total tubes = colorCount. 0 empty tubes!
            val totalTubes = colorCount
            val tubesState = Array(totalTubes) { mutableListOf<LiquidColor>() }

            val topColors = chosenColors.toList()

            // 3 remaining units of each color to scramble in lower slots (0, 1, 2)
            val lowerPool = mutableListOf<LiquidColor>()
            for (c in chosenColors) {
                repeat(3) { lowerPool.add(c) }
            }
            lowerPool.shuffle(rng)

            for (slot in 0..2) {
                for (tubeIdx in 0 until colorCount) {
                    var pickIdx = lowerPool.indices.firstOrNull { idx ->
                        if (slot == 2) lowerPool[idx] != topColors[tubeIdx] else true
                    }
                    if (pickIdx == null) pickIdx = 0
                    val color = lowerPool.removeAt(pickIdx)
                    tubesState[tubeIdx].add(color)
                }
            }

            // Top color of each tube
            for (tubeIdx in 0 until colorCount) {
                tubesState[tubeIdx].add(topColors[tubeIdx])
            }

            // Notice: every tube has 4 liquids! ZERO empty tubes!
            val resultTubes = tubesState.mapIndexed { index, liquids ->
                Tube(id = index, liquids = liquids.toList())
            }

            return GameLevel(
                levelNumber = levelNumber,
                tubes = resultTubes,
                minMoves = (colorCount * 3) + 4,
                difficulty = diff
            )
        }

        // =========================================================================
        // LEVELS 11 to 20: ULTRA HARD BOTTLENECK (Only 1 empty tube, quick deadlock)
        // =========================================================================
        if (levelNumber >= 11) {
            val colorCount = 7.coerceAtMost(C.size)
            val rng = Random(levelNumber * 89119L + 47)
            val chosenColors = C.take(colorCount)

            val totalTubes = colorCount + 1 // exactly 1 empty tube
            val tubesState = Array(totalTubes) { mutableListOf<LiquidColor>() }

            val topColors = chosenColors.toList()
            val lowerPool = mutableListOf<LiquidColor>()
            for (c in chosenColors) {
                repeat(3) { lowerPool.add(c) }
            }
            lowerPool.shuffle(rng)

            for (slot in 0..2) {
                for (tubeIdx in 0 until colorCount) {
                    var pickIdx = lowerPool.indices.firstOrNull { idx ->
                        if (slot == 2) lowerPool[idx] != topColors[tubeIdx] else true
                    }
                    if (pickIdx == null) pickIdx = 0
                    val color = lowerPool.removeAt(pickIdx)
                    tubesState[tubeIdx].add(color)
                }
            }

            for (tubeIdx in 0 until colorCount) {
                tubesState[tubeIdx].add(topColors[tubeIdx])
            }

            val resultTubes = tubesState.mapIndexed { index, liquids ->
                Tube(id = index, liquids = liquids.toList())
            }

            return GameLevel(
                levelNumber = levelNumber,
                tubes = resultTubes,
                minMoves = (colorCount * 3) + 4,
                difficulty = diff
            )
        }

        // =========================================================================
        // LEVELS 6 to 10: "১ লেভেল করা শেষ হবে আর গেমটা কঠিন হবে"
        // 5 colors, only 1 empty tube (intricate, challenging)
        // =========================================================================
        val colorCount = 5.coerceAtMost(C.size)
        val emptyTubesCount = 1 // Only 1 empty tube for high challenge right from early levels!
        val totalTubes = colorCount + emptyTubesCount
        val rng = Random(levelNumber * 7919L + 31)
        val chosenColors = C.take(colorCount)

        val tubesState = Array(totalTubes) { index ->
            if (index < colorCount) MutableList(4) { chosenColors[index] }
            else mutableListOf<LiquidColor>()
        }

        val shuffleMoves = 30 + levelNumber * 2
        var lastSrc = -1
        var lastDst = -1
        for (step in 0 until shuffleMoves) {
            val nonEmpties = tubesState.indices.filter { tubesState[it].isNotEmpty() }
            if (nonEmpties.isEmpty()) break
            val src = nonEmpties[rng.nextInt(nonEmpties.size)]
            val possibleTargets = tubesState.indices.filter {
                it != src && tubesState[it].size < 4 && !(src == lastDst && it == lastSrc)
            }
            if (possibleTargets.isNotEmpty()) {
                val dst = possibleTargets[rng.nextInt(possibleTargets.size)]
                val item = tubesState[src].removeAt(tubesState[src].size - 1)
                tubesState[dst].add(item)
                lastSrc = src
                lastDst = dst
            }
        }

        // Ensure 1 empty tube for levels 6-10
        if (tubesState.count { it.isEmpty() } == 0) {
            val smallest = tubesState.indices.minByOrNull { tubesState[it].size } ?: (totalTubes - 1)
            val evicted = tubesState[smallest].toList()
            tubesState[smallest].clear()
            for (item in evicted) {
                val dst = tubesState.indices.firstOrNull { it != smallest && tubesState[it].size < 4 }
                if (dst != null) tubesState[dst].add(item) else tubesState[smallest].add(item)
            }
        }

        return GameLevel(
            levelNumber = levelNumber,
            tubes = tubesState.mapIndexed { index, liquids -> Tube(index, liquids.toList()) },
            minMoves = colorCount * 2 + 4,
            difficulty = diff
        )
    }
}
