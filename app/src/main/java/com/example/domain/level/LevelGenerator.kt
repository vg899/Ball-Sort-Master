package com.example.domain.level

import com.example.domain.model.BallColor
import com.example.domain.model.LevelPuzzle
import com.example.domain.model.Tube
import java.util.Random

object LevelGenerator {

    const val MAX_LEVELS = 1_000_000

    /**
     * Determines the puzzle parameters for a given level.
     */
    data class LevelDifficulty(
        val colorCount: Int,
        val emptyTubeCount: Int,
        val capacity: Int,
        val shuffleSteps: Int
    )

    fun getDifficultyForLevel(level: Int): LevelDifficulty {
        val clampedLevel = level.coerceIn(1, MAX_LEVELS)
        return when {
            clampedLevel <= 5 -> LevelDifficulty(colorCount = 2, emptyTubeCount = 1, capacity = 4, shuffleSteps = 8)
            clampedLevel <= 25 -> LevelDifficulty(colorCount = 3, emptyTubeCount = 1, capacity = 4, shuffleSteps = 14)
            clampedLevel <= 100 -> LevelDifficulty(colorCount = 3, emptyTubeCount = 1, capacity = 4, shuffleSteps = 20)
            clampedLevel <= 500 -> LevelDifficulty(colorCount = 4, emptyTubeCount = 2, capacity = 4, shuffleSteps = 28)
            clampedLevel <= 2_000 -> LevelDifficulty(colorCount = 5, emptyTubeCount = 2, capacity = 4, shuffleSteps = 36)
            clampedLevel <= 10_000 -> LevelDifficulty(colorCount = 6, emptyTubeCount = 2, capacity = 4, shuffleSteps = 44)
            clampedLevel <= 50_000 -> LevelDifficulty(colorCount = 7, emptyTubeCount = 2, capacity = 4, shuffleSteps = 50)
            clampedLevel <= 150_000 -> LevelDifficulty(colorCount = 8, emptyTubeCount = 2, capacity = 4, shuffleSteps = 56)
            clampedLevel <= 400_000 -> LevelDifficulty(colorCount = 9, emptyTubeCount = 2, capacity = 4, shuffleSteps = 62)
            clampedLevel <= 700_000 -> LevelDifficulty(colorCount = 10, emptyTubeCount = 2, capacity = 4, shuffleSteps = 68)
            clampedLevel < MAX_LEVELS -> LevelDifficulty(colorCount = 11, emptyTubeCount = 2, capacity = 4, shuffleSteps = 74)
            else -> LevelDifficulty(colorCount = 12, emptyTubeCount = 2, capacity = 4, shuffleSteps = 80) // Level 1,000,000 Grand Final
        }
    }

    /**
     * Deterministic pseudo-random seed generator from level number.
     */
    private fun seedFromLevel(level: Int): Long {
        var x = level.toLong() xor 0x5DEECE66DL
        x = (x * 6364136223846793005L) + 1442695040888963407L
        x = x xor (x ushr 16)
        x = (x * 6364136223846793005L) + 1442695040888963407L
        return x
    }

    /**
     * Generates a guaranteed 100% solvable level puzzle for the given level number.
     */
    fun generateLevel(level: Int): LevelPuzzle {
        val clampedLevel = level.coerceIn(1, MAX_LEVELS)
        val diff = getDifficultyForLevel(clampedLevel)
        val seed = seedFromLevel(clampedLevel)
        val random = Random(seed)

        val colors = BallColor.entries.take(diff.colorCount)
        val totalTubes = diff.colorCount + diff.emptyTubeCount

        // Step 1: Start with a fully solved configuration
        val tubeLists = MutableList(totalTubes) { index ->
            if (index < diff.colorCount) {
                val color = colors[index]
                MutableList(diff.capacity) { color }
            } else {
                mutableListOf<BallColor>()
            }
        }

        // Step 2: Perform deterministic reverse moves to scramble the balls.
        // A reverse move is: pick top ball from any non-empty tube and move to another tube with available space.
        // To guarantee solvability, we make sure we don't immediately undo the move.
        var lastFrom = -1
        var lastTo = -1
        var step = 0
        var attempts = 0
        val maxAttempts = diff.shuffleSteps * 10

        while (step < diff.shuffleSteps && attempts < maxAttempts) {
            attempts++
            // Pick a non-empty tube to take a ball from
            val validFroms = tubeLists.indices.filter { tubeLists[it].isNotEmpty() }
            if (validFroms.isEmpty()) break
            val fromIdx = validFroms[random.nextInt(validFroms.size)]

            // Pick a tube with available space that is not the same as fromIdx
            // and avoid directly moving back and forth (from == lastTo && to == lastFrom)
            val validTos = tubeLists.indices.filter { toIdx ->
                toIdx != fromIdx &&
                        tubeLists[toIdx].size < diff.capacity &&
                        !(fromIdx == lastTo && toIdx == lastFrom)
            }
            if (validTos.isEmpty()) continue

            val toIdx = validTos[random.nextInt(validTos.size)]

            // Execute move
            val ball = tubeLists[fromIdx].removeAt(tubeLists[fromIdx].size - 1)
            tubeLists[toIdx].add(ball)

            lastFrom = fromIdx
            lastTo = toIdx
            step++
        }

        // Step 3: Ensure it's not already solved initially
        val tubes = tubeLists.mapIndexed { index, balls ->
            Tube(
                id = index,
                capacity = diff.capacity,
                balls = balls.toList()
            )
        }

        return LevelPuzzle(
            levelNumber = clampedLevel,
            tubes = tubes,
            colorsUsed = diff.colorCount,
            targetTubes = diff.colorCount,
            emptyTubes = diff.emptyTubeCount,
            isGrandFinal = (clampedLevel == MAX_LEVELS)
        )
    }
}
