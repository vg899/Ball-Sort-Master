package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.PlayerProgress

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ball_sort_master_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_LEVEL = "current_level"
        private const val KEY_HIGHEST_LEVEL = "highest_level"
        private const val KEY_TOTAL_COINS = "total_coins"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_GRAND_PRIZE_POPUP = "grand_prize_popup"
        private const val KEY_CONSECUTIVE_DAYS = "consecutive_days"
        private const val KEY_LAST_DAILY_CLAIM = "last_daily_claim"
        private const val KEY_TOTAL_MOVES = "total_moves"
        private const val KEY_TOTAL_SOLVED = "total_solved"
        private const val KEY_EXTRA_TUBES = "extra_tubes"
        private const val KEY_HINTS = "hints"
        private const val KEY_LOCAL_UID = "local_anonymous_uid"
    }

    fun getLocalUid(): String {
        var uid = prefs.getString(KEY_LOCAL_UID, null)
        if (uid.isNullOrEmpty()) {
            uid = "user_" + java.util.UUID.randomUUID().toString().replace("-", "").take(16)
            prefs.edit().putString(KEY_LOCAL_UID, uid).apply()
        }
        return uid
    }

    fun loadPlayerProgress(): PlayerProgress {
        return PlayerProgress(
            currentLevel = prefs.getInt(KEY_CURRENT_LEVEL, 1),
            highestCompletedLevel = prefs.getInt(KEY_HIGHEST_LEVEL, 0),
            totalCoins = prefs.getInt(KEY_TOTAL_COINS, 150),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true),
            grandPrizePopupShown = prefs.getBoolean(KEY_GRAND_PRIZE_POPUP, false),
            consecutiveDays = prefs.getInt(KEY_CONSECUTIVE_DAYS, 0),
            lastDailyClaimEpochDay = prefs.getLong(KEY_LAST_DAILY_CLAIM, 0L),
            totalMovesCount = prefs.getLong(KEY_TOTAL_MOVES, 0L),
            totalPuzzlesSolved = prefs.getInt(KEY_TOTAL_SOLVED, 0),
            extraTubesInventory = prefs.getInt(KEY_EXTRA_TUBES, 2),
            hintsInventory = prefs.getInt(KEY_HINTS, 5)
        )
    }

    fun savePlayerProgress(progress: PlayerProgress) {
        prefs.edit()
            .putInt(KEY_CURRENT_LEVEL, progress.currentLevel)
            .putInt(KEY_HIGHEST_LEVEL, progress.highestCompletedLevel)
            .putInt(KEY_TOTAL_COINS, progress.totalCoins)
            .putBoolean(KEY_SOUND_ENABLED, progress.soundEnabled)
            .putBoolean(KEY_MUSIC_ENABLED, progress.musicEnabled)
            .putBoolean(KEY_GRAND_PRIZE_POPUP, progress.grandPrizePopupShown)
            .putInt(KEY_CONSECUTIVE_DAYS, progress.consecutiveDays)
            .putLong(KEY_LAST_DAILY_CLAIM, progress.lastDailyClaimEpochDay)
            .putLong(KEY_TOTAL_MOVES, progress.totalMovesCount)
            .putInt(KEY_TOTAL_SOLVED, progress.totalPuzzlesSolved)
            .putInt(KEY_EXTRA_TUBES, progress.extraTubesInventory)
            .putInt(KEY_HINTS, progress.hintsInventory)
            .apply()
    }

    fun updateLevelProgress(completedLevel: Int) {
        val currentHighest = prefs.getInt(KEY_HIGHEST_LEVEL, 0)
        val newHighest = maxOf(currentHighest, completedLevel)
        val nextLevel = minOf(completedLevel + 1, 1_000_000)
        val totalSolved = prefs.getInt(KEY_TOTAL_SOLVED, 0) + 1
        val coins = prefs.getInt(KEY_TOTAL_COINS, 150) + 20

        prefs.edit()
            .putInt(KEY_HIGHEST_LEVEL, newHighest)
            .putInt(KEY_CURRENT_LEVEL, nextLevel)
            .putInt(KEY_TOTAL_SOLVED, totalSolved)
            .putInt(KEY_TOTAL_COINS, coins)
            .apply()
    }
}
