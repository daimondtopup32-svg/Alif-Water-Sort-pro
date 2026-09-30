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
}
