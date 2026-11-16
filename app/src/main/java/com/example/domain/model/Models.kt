package com.example.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Color definitions for balls. Distinct, highly recognizable, accessible palette.
 */
enum class BallColor(
    val id: Int,
    val displayName: String,
    val primaryColor: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    RED(1, "Ruby Red", Color(0xFFEF4444), Color(0xFFFCA5A5), Color(0xFFB91C1C)),
    BLUE(2, "Sapphire Blue", Color(0xFF3B82F6), Color(0xFF93C5FD), Color(0xFF1D4ED8)),
    GREEN(3, "Emerald Green", Color(0xFF10B981), Color(0xFF6EE7B7), Color(0xFF047857)),
    YELLOW(4, "Amber Gold", Color(0xFFF59E0B), Color(0xFFFDE68A), Color(0xFFB45309)),
    PURPLE(5, "Amethyst Purple", Color(0xFF8B5CF6), Color(0xFFC4B5FD), Color(0xFF6D28D9)),
    CYAN(6, "Aqua Cyan", Color(0xFF06B6D4), Color(0xFF67E8F9), Color(0xFF0E7490)),
    ORANGE(7, "Flame Orange", Color(0xFFF97316), Color(0xFFFDBA74), Color(0xFFC2410C)),
    PINK(8, "Rose Pink", Color(0xFFEC4899), Color(0xFFF9A8D4), Color(0xFFBE185D)),
    LIME(9, "Electric Lime", Color(0xFF84CC16), Color(0xFFBEF264), Color(0xFF4D7C0F)),
    TEAL(10, "Deep Teal", Color(0xFF14B8A6), Color(0xFF5EEAD4), Color(0xFF0F766E)),
    INDIGO(11, "Neon Indigo", Color(0xFF6366F1), Color(0xFFA5B4FC), Color(0xFF4338CA)),
    VIOLET(12, "Royal Violet", Color(0xFFA855F7), Color(0xFFE9D5FF), Color(0xFF7E22CE));

    companion object {
        fun fromId(id: Int): BallColor = entries.firstOrNull { it.id == id } ?: RED
    }
}

/**
 * A single tube representation with a fixed capacity (default 4 balls).
 */
data class Tube(
    val id: Int,
    val capacity: Int = 4,
    val balls: List<BallColor> = emptyList(),
    val isExtra: Boolean = false
) {
    val isFull: Boolean get() = balls.size >= capacity
    val isEmpty: Boolean get() = balls.isEmpty()
    val topBall: BallColor? get() = balls.lastOrNull()
    val availableSpace: Int get() = capacity - balls.size

    /**
     * Tube is completed if it's completely empty OR full with all balls of identical color.
     */
    val isSolved: Boolean
        get() {
            if (balls.isEmpty()) return true
            if (balls.size != capacity) return false
            val first = balls.first()
            return balls.all { it == first }
        }

    fun canAccept(ball: BallColor): Boolean {
        if (isFull) return false
        if (isEmpty) return true
        return topBall == ball
    }

    fun addBall(ball: BallColor): Tube {
        require(!isFull) { "Tube is full" }
        return copy(balls = balls + ball)
    }

    fun removeTopBall(): Pair<Tube, BallColor> {
        require(balls.isNotEmpty()) { "Tube is empty" }
        val ball = balls.last()
        return copy(balls = balls.dropLast(1)) to ball
    }
}

/**
 * A recorded move for undo history and hint execution.
 */
data class GameMove(
    val fromTubeId: Int,
    val toTubeId: Int,
    val ballColor: BallColor,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Complete puzzle level state.
 */
data class LevelPuzzle(
    val levelNumber: Int,
    val tubes: List<Tube>,
    val colorsUsed: Int,
    val targetTubes: Int,
    val emptyTubes: Int,
    val isGrandFinal: Boolean = (levelNumber == 1_000_000)
) {
    val isWon: Boolean
        get() = tubes.all { it.isSolved }
}

/**
 * Player progress persisted locally and synced to Firebase.
 */
data class PlayerProgress(
    val currentLevel: Int = 1,
    val highestCompletedLevel: Int = 0,
    val totalCoins: Int = 150,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val grandPrizePopupShown: Boolean = false,
    val consecutiveDays: Int = 0,
    val lastDailyClaimEpochDay: Long = 0L,
    val totalMovesCount: Long = 0L,
    val totalPuzzlesSolved: Int = 0,
    val extraTubesInventory: Int = 1,
    val hintsInventory: Int = 3
)

/**
 * Prize claim data submitted to Firebase Realtime Database.
 */
data class PrizeClaim(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val fullAddress: String = "",
    val pinCode: String = "",
    val completedLevel: Int = 1_000_000,
    val verificationHash: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "pending" // pending, under_review, verified, rejected, completed
)
