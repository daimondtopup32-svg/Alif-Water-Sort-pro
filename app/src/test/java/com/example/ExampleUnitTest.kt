package com.example

import com.example.logic.GameSolver
import com.example.model.ColorManager
import com.example.model.LevelGenerator
import com.example.model.Tube
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val C = ColorManager.ALL_COLORS

    @Test
    fun tube_capacityAndTopColor_correct() {
        val tube = Tube(id = 0, liquids = listOf(C[0], C[0], C[1]))
        assertEquals(3, tube.liquids.size)
        assertEquals(4, tube.capacity)
        assertEquals(1, tube.freeSpace)
        assertEquals(C[1], tube.topColor)
        assertEquals(1, tube.topColorCount)
        assertFalse(tube.isFull)
        assertFalse(tube.isEmpty)
        assertFalse(tube.isCompleted)
    }

    @Test
    fun tube_isCompleted_whenFullWithSingleColor() {
        val completedTube = Tube(id = 1, liquids = listOf(C[2], C[2], C[2], C[2]))
        assertTrue(completedTube.isFull)
        assertTrue(completedTube.isCompleted)
        assertEquals(4, completedTube.topColorCount)
    }

    @Test
    fun tube_canAccept_validation() {
        val emptyTube = Tube(id = 2, liquids = emptyList())
        assertTrue(emptyTube.canAccept(C[0]))

        val partialTube = Tube(id = 3, liquids = listOf(C[0], C[0]))
        assertTrue(partialTube.canAccept(C[0]))
        assertFalse(partialTube.canAccept(C[1]))

        val fullTube = Tube(id = 4, liquids = listOf(C[0], C[0], C[0], C[0]))
        assertFalse(fullTube.canAccept(C[0]))
    }

    @Test
    fun levelGenerator_generatesSolvableLevel1() {
        val level1 = LevelGenerator.getLevel(1)
        assertEquals(1, level1.levelNumber)
        assertEquals(4, level1.tubes.size)
        assertTrue(level1.tubes.any { it.isEmpty })

        // Check solver finds hint for level 1
        val hint = GameSolver.findHint(level1.tubes)
        assertNotNull(hint)
    }

    @Test
    fun gameSolver_detectsWinCondition() {
        val wonTubes = listOf(
            Tube(0, listOf(C[0], C[0], C[0], C[0])),
            Tube(1, listOf(C[1], C[1], C[1], C[1])),
            Tube(2, emptyList())
        )
        assertTrue(GameSolver.isWon(wonTubes))

        val notWonTubes = listOf(
            Tube(0, listOf(C[0], C[1], C[0], C[1])),
            Tube(1, listOf(C[1], C[0], C[1], C[0])),
            Tube(2, emptyList())
        )
        assertFalse(GameSolver.isWon(notWonTubes))
    }

    @Test
    fun levelGenerator_level11_isUltraHardWithDistinctTopsAndSingleEmptyTube() {
        val level11 = LevelGenerator.getLevel(11)
        assertEquals(11, level11.levelNumber)
        // 7 colors + 1 empty = 8 tubes total
        assertEquals(8, level11.tubes.size)
        val emptyTubes = level11.tubes.filter { it.isEmpty }
        assertEquals(1, emptyTubes.size)

        val fullTubes = level11.tubes.filter { it.isFull }
        assertEquals(7, fullTubes.size)

        // All 7 full tubes have distinct top colors
        val topColors = fullTubes.map { it.topColor }.toSet()
        assertEquals(7, topColors.size)
    }

    @Test
    fun levelGenerator_level11_withExtraTubesAllowsMultipleVideoGrants() {
        val level11 = LevelGenerator.getLevel(11)
        var tubes = level11.tubes
        // Simulate watching 4 video ads to grant 4 extra empty tubes
        for (i in 1..4) {
            val newId = tubes.size
            tubes = tubes + Tube(id = newId, liquids = emptyList(), isExtraTube = true)
        }
        // Original 8 + 4 extra = 12 tubes
        assertEquals(12, tubes.size)
        val emptyTubes = tubes.filter { it.isEmpty }
        assertEquals(5, emptyTubes.size)
    }

    @Test
    fun levelGenerator_level2_escalatesDifficultyRightAfterLevel1() {
        val level1 = LevelGenerator.getLevel(1)
        val level2 = LevelGenerator.getLevel(2)

        // Level 1 has 2 empty tubes
        assertEquals(2, level1.tubes.count { it.isEmpty })

        // Level 2 immediately gets harder with only 1 empty tube
        assertEquals(1, level2.tubes.count { it.isEmpty })
        assertEquals(3, level2.tubes.count { it.isFull })
    }

    @Test
    fun levelGenerator_afterLevel20_hasZeroEmptyTubes_allTubesHaveColor() {
        val level21 = LevelGenerator.getLevel(21)
        assertEquals(21, level21.levelNumber)

        // As requested: "২০ লেভেলের পর একটা গ্লাসও খালি থাকবে না সবগুলোতে রং থাকবে"
        val emptyCount = level21.tubes.count { it.isEmpty }
        assertEquals(0, emptyCount)

        // All tubes contain colored liquids
        assertTrue(level21.tubes.all { it.liquids.isNotEmpty() })

        // Because all tubes are full with distinct tops and 0 empty tubes, valid moves initially = 0
        assertFalse(GameSolver.hasValidMoves(level21.tubes))

        // Watching rewarded video ads to add 3-4 extra tubes creates buffer space
        var playableTubes = level21.tubes
        for (i in 1..4) {
            playableTubes = playableTubes + Tube(id = playableTubes.size, liquids = emptyList(), isExtraTube = true)
        }
        assertEquals(4, playableTubes.count { it.isEmpty })
        assertTrue(GameSolver.hasValidMoves(playableTubes))
    }
}
