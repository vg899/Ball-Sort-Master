package com.example.data.repository

import android.content.Context
import com.example.audio.SoundManager
import com.example.data.firebase.FirebaseManager
import com.example.data.local.PreferencesManager
import com.example.domain.model.PlayerProgress
import com.example.domain.model.PrizeClaim
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class GameRepository(context: Context) {

    private val prefs = PreferencesManager(context)

    private val _progress = MutableStateFlow(prefs.loadPlayerProgress())
    val progress: StateFlow<PlayerProgress> = _progress.asStateFlow()

    init {
        // Sync initial audio preferences to SoundManager
        SoundManager.isSoundEnabled = _progress.value.soundEnabled
        SoundManager.isMusicEnabled = _progress.value.musicEnabled
    }

    suspend fun initializeAuth(): Result<String> {
        val result = FirebaseManager.ensureAnonymousAuth()
        // Sync latest local progress to Firebase
        FirebaseManager.syncProgress(_progress.value)
        return result
    }

    fun completeLevel(levelNumber: Int, movesTaken: Int) {
        val current = _progress.value
        val newHighest = maxOf(current.highestCompletedLevel, levelNumber)
        val nextLevel = if (levelNumber < 1_000_000) levelNumber + 1 else 1_000_000
        val earnedCoins = when {
            levelNumber % 100 == 0 -> 100
            levelNumber % 10 == 0 -> 40
            else -> 20
        }

        val updated = current.copy(
            currentLevel = nextLevel,
            highestCompletedLevel = newHighest,
            totalCoins = current.totalCoins + earnedCoins,
            totalMovesCount = current.totalMovesCount + movesTaken,
            totalPuzzlesSolved = current.totalPuzzlesSolved + 1
        )

        _progress.value = updated
        prefs.savePlayerProgress(updated)
        FirebaseManager.syncProgress(updated)
    }

    fun markGrandPrizePopupViewed() {
        val updated = _progress.value.copy(grandPrizePopupShown = true)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    fun setSoundEnabled(enabled: Boolean) {
        SoundManager.isSoundEnabled = enabled
        val updated = _progress.value.copy(soundEnabled = enabled)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    fun setMusicEnabled(enabled: Boolean) {
        SoundManager.setMusic(enabled)
        val updated = _progress.value.copy(musicEnabled = enabled)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    fun consumeHint(): Boolean {
        val current = _progress.value
        if (current.hintsInventory > 0) {
            val updated = current.copy(hintsInventory = current.hintsInventory - 1)
            _progress.value = updated
            prefs.savePlayerProgress(updated)
            return true
        } else if (current.totalCoins >= 25) {
            val updated = current.copy(totalCoins = current.totalCoins - 25)
            _progress.value = updated
            prefs.savePlayerProgress(updated)
            return true
        }
        return false
    }

    fun addHintFromReward(amount: Int = 1) {
        val updated = _progress.value.copy(hintsInventory = _progress.value.hintsInventory + amount)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    fun consumeExtraTube(): Boolean {
        val current = _progress.value
        if (current.extraTubesInventory > 0) {
            val updated = current.copy(extraTubesInventory = current.extraTubesInventory - 1)
            _progress.value = updated
            prefs.savePlayerProgress(updated)
            return true
        } else if (current.totalCoins >= 50) {
            val updated = current.copy(totalCoins = current.totalCoins - 50)
            _progress.value = updated
            prefs.savePlayerProgress(updated)
            return true
        }
        return false
    }

    fun addExtraTubeFromReward(amount: Int = 1) {
        val updated = _progress.value.copy(extraTubesInventory = _progress.value.extraTubesInventory + amount)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    fun addCoins(amount: Int) {
        val updated = _progress.value.copy(totalCoins = _progress.value.totalCoins + amount)
        _progress.value = updated
        prefs.savePlayerProgress(updated)
    }

    /**
     * Daily Reward logic: calculates if available and awards coins.
     */
    fun checkDailyRewardAvailable(): Boolean {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toLong()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toLong()
        val currentEpochDay = currentYear * 365 + currentDay
        return currentEpochDay > _progress.value.lastDailyClaimEpochDay
    }

    fun claimDailyReward(): Int {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR).toLong()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toLong()
        val currentEpochDay = currentYear * 365 + currentDay

        val current = _progress.value
        val consecutive = if (currentEpochDay - current.lastDailyClaimEpochDay == 1L) {
            (current.consecutiveDays % 7) + 1
        } else {
            1
        }

        val rewardCoins = when (consecutive) {
            1 -> 50
            2 -> 75
            3 -> 100
            4 -> 125
            5 -> 150
            6 -> 200
            7 -> 300
            else -> 50
        }

        val updated = current.copy(
            consecutiveDays = consecutive,
            lastDailyClaimEpochDay = currentEpochDay,
            totalCoins = current.totalCoins + rewardCoins
        )
        _progress.value = updated
        prefs.savePlayerProgress(updated)
        return rewardCoins
    }

    suspend fun getExistingPrizeClaim(): PrizeClaim? = FirebaseManager.getExistingPrizeClaim()

    suspend fun submitPrizeClaim(
        fullName: String,
        email: String,
        mobileNumber: String,
        fullAddress: String,
        pinCode: String
    ): Result<PrizeClaim> {
        return FirebaseManager.submitPrizeClaim(fullName, email, mobileNumber, fullAddress, pinCode)
    }
}
