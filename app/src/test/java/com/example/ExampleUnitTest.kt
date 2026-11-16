package com.example

import com.example.domain.level.LevelGenerator
import com.example.domain.model.BallColor
import com.example.domain.model.Tube
import com.example.domain.solver.PuzzleSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLevelGenerationDeterministic() {
        val testLevels = listOf(1, 10, 100, 1_000, 10_000, 100_000, 500_000, 999_999, 1_000_000)
        for (lvl in testLevels) {
            val puzzleA = LevelGenerator.generateLevel(lvl)
            val puzzleB = LevelGenerator.generateLevel(lvl)

            assertEquals("Level number must match", lvl, puzzleA.levelNumber)
            assertEquals("Tube counts must match", puzzleA.tubes.size, puzzleB.tubes.size)
            for (i in puzzleA.tubes.indices) {
                assertEquals(
                    "Tubes must match for level $lvl",
                    puzzleA.tubes[i].balls,
                    puzzleB.tubes[i].balls
                )
            }
        }
    }

    @Test
    fun testLevelOneMillionGrandFinal() {
        val finalPuzzle = LevelGenerator.generateLevel(1_000_000)
        assertTrue("Level 1,000,000 must be marked Grand Final", finalPuzzle.isGrandFinal)
        assertEquals("Level 1,000,000 should use 12 colors", 12, finalPuzzle.colorsUsed)
        assertEquals("Level 1,000,000 should have 14 tubes", 14, finalPuzzle.tubes.size)
    }

    @Test
    fun testTubeLogic() {
        val tube = Tube(id = 0, capacity = 4, balls = listOf(BallColor.RED, BallColor.RED))
        assertFalse(tube.isFull)
        assertFalse(tube.isEmpty)
        assertTrue(tube.canAccept(BallColor.RED))
        assertFalse(tube.canAccept(BallColor.BLUE))

        val fullTube = Tube(id = 1, capacity = 4, balls = listOf(BallColor.BLUE, BallColor.BLUE, BallColor.BLUE, BallColor.BLUE))
        assertTrue(fullTube.isFull)
        assertTrue(fullTube.isSolved)
        assertFalse(fullTube.canAccept(BallColor.BLUE))
    }

    @Test
    fun testPuzzleSolverHint() {
        // Create an almost-solved state with an obvious winning move
        val tube1 = Tube(id = 0, capacity = 4, balls = listOf(BallColor.RED, BallColor.RED, BallColor.RED))
        val tube2 = Tube(id = 1, capacity = 4, balls = listOf(BallColor.RED))
        val tube3 = Tube(id = 2, capacity = 4, balls = emptyList())

        val hint = PuzzleSolver.findHintMove(listOf(tube1, tube2, tube3))
        assertNotNull("Hint must be found", hint)
        assertTrue("Hint must move to tube 0 or tube 1", hint?.toTubeId == 0 || hint?.toTubeId == 1)
    }
}
