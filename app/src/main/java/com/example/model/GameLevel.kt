package com.example.model

data class GameLevel(
    val levelNumber: Int,
    val initialTubes: List<Tube>,
    val targetMoves: Int = 20,
    val difficulty: String = "Normal"
) {
    companion object {
        fun generateLevel(levelNum: Int): GameLevel {
            val numColors = when {
                levelNum <= 3 -> 3
                levelNum <= 10 -> 4
                levelNum <= 25 -> 5
                levelNum <= 50 -> 6
                levelNum <= 80 -> 7
                else -> 8
            }.coerceAtMost(LiquidColor.entries.size)

            val emptyTubesCount = 2
            val totalTubes = numColors + emptyTubesCount
            val availableColors = LiquidColor.entries.take(numColors)

            // 4 units per color
            val segmentsList = mutableListOf<LiquidColor>()
            availableColors.forEach { color ->
                repeat(4) { segmentsList.add(color) }
            }

            // Pseudo-random shuffle seeded by level number
            val random = java.util.Random(levelNum.toLong() * 9973L)
            segmentsList.shuffle(random)

            val tubes = mutableListOf<Tube>()
            for (i in 0 until numColors) {
                val sub = segmentsList.subList(i * 4, (i + 1) * 4).toList()
                tubes.add(Tube(id = i, segments = sub))
            }
            for (i in numColors until totalTubes) {
                tubes.add(Tube(id = i, segments = emptyList()))
            }

            val diff = when {
                numColors <= 4 -> "Easy"
                numColors <= 6 -> "Medium"
                else -> "Hard"
            }

            return GameLevel(
                levelNumber = levelNum,
                initialTubes = tubes,
                targetMoves = numColors * 6,
                difficulty = diff
            )
        }
    }
}
