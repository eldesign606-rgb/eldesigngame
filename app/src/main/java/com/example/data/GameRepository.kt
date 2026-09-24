package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameRepository(private val dao: GameDao) {

    val playerProfile: Flow<PlayerProfile?> = dao.getPlayerProfileFlow()
    val levelRecords: Flow<List<LevelRecord>> = dao.getAllLevelRecordsFlow()
    val achievements: Flow<List<AchievementRecord>> = dao.getAllAchievementsFlow()

    fun getDailyRecord(date: String): Flow<DailyRecord?> = dao.getDailyRecordFlow(date)

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val currentProfile = dao.getPlayerProfileSync()
        if (currentProfile == null) {
            val todayStr = getTodayDateString()
            dao.insertPlayerProfile(
                PlayerProfile(
                    id = 1,
                    level = 1,
                    xp = 0,
                    coins = 150,
                    streak = 1,
                    lastActiveDate = todayStr,
                    highestUnlockedLevel = 1,
                    totalPuzzlesPlayed = 0,
                    totalPuzzlesSolved = 0,
                    totalPlayTimeSeconds = 0L,
                    soundEnabled = true,
                    musicEnabled = true,
                    hapticEnabled = true,
                    tutorialCompleted = false
                )
            )
            // Init level 1 unlocked
            dao.insertOrUpdateLevelRecord(
                LevelRecord(
                    levelId = 1,
                    stars = 0,
                    highScore = 0,
                    bestTimeSeconds = 0,
                    isCompleted = false
                )
            )
            // Init achievements
            dao.insertAchievements(AchievementCatalog.initialAchievements)
        }
    }

    suspend fun getProfile(): PlayerProfile {
        return dao.getPlayerProfileSync() ?: PlayerProfile()
    }

    suspend fun updateSettings(sound: Boolean, music: Boolean, haptic: Boolean) = withContext(Dispatchers.IO) {
        val profile = getProfile()
        dao.updatePlayerProfile(
            profile.copy(
                soundEnabled = sound,
                musicEnabled = music,
                hapticEnabled = haptic
            )
        )
    }

    suspend fun markTutorialCompleted() = withContext(Dispatchers.IO) {
        val profile = getProfile()
        dao.updatePlayerProfile(profile.copy(tutorialCompleted = true))
    }

    suspend fun deductCoins(amount: Int): Boolean = withContext(Dispatchers.IO) {
        val profile = getProfile()
        if (profile.coins >= amount) {
            dao.updatePlayerProfile(profile.copy(coins = profile.coins - amount))
            true
        } else {
            false
        }
    }

    suspend fun recordGameFailed(elapsedSeconds: Int) = withContext(Dispatchers.IO) {
        val profile = getProfile()
        dao.updatePlayerProfile(
            profile.copy(
                totalPuzzlesPlayed = profile.totalPuzzlesPlayed + 1,
                totalPlayTimeSeconds = profile.totalPlayTimeSeconds + elapsedSeconds
            )
        )
    }

    suspend fun recordLevelCompleted(
        levelId: Int,
        stars: Int,
        score: Int,
        elapsedSeconds: Int,
        livesRemaining: Int,
        isDaily: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val profile = getProfile()
        val levelDef = LevelCatalog.getLevelById(levelId)

        // Update level record if normal level
        if (!isDaily) {
            val existing = dao.getLevelRecord(levelId)
            val newStars = maxOf(existing?.stars ?: 0, stars)
            val newScore = maxOf(existing?.highScore ?: 0, score)
            val bestTime = if (existing == null || existing.bestTimeSeconds == 0) elapsedSeconds else minOf(existing.bestTimeSeconds, elapsedSeconds)
            dao.insertOrUpdateLevelRecord(
                LevelRecord(
                    levelId = levelId,
                    stars = newStars,
                    highScore = newScore,
                    bestTimeSeconds = bestTime,
                    isCompleted = true
                )
            )

            // Unlock next level
            val nextLevelId = levelId + 1
            if (nextLevelId <= LevelCatalog.levels.size) {
                val nextRecord = dao.getLevelRecord(nextLevelId)
                if (nextRecord == null) {
                    dao.insertOrUpdateLevelRecord(
                        LevelRecord(
                            levelId = nextLevelId,
                            stars = 0,
                            highScore = 0,
                            bestTimeSeconds = 0,
                            isCompleted = false
                        )
                    )
                }
            }
        }

        // Calculate rewards
        val earnedXP = levelDef.rewardXP
        val earnedCoins = levelDef.rewardCoin

        val newTotalXP = profile.xp + earnedXP
        val playerLevel = 1 + (newTotalXP / 500)
        val highestUnlocked = if (!isDaily) maxOf(profile.highestUnlockedLevel, levelId + 1) else profile.highestUnlockedLevel

        val updatedProfile = profile.copy(
            level = playerLevel,
            xp = newTotalXP,
            coins = profile.coins + earnedCoins,
            highestUnlockedLevel = highestUnlocked,
            totalPuzzlesPlayed = profile.totalPuzzlesPlayed + 1,
            totalPuzzlesSolved = profile.totalPuzzlesSolved + 1,
            totalPlayTimeSeconds = profile.totalPlayTimeSeconds + elapsedSeconds
        )
        dao.updatePlayerProfile(updatedProfile)

        // Check Achievements
        checkAndUpdateAchievements(levelId, stars, elapsedSeconds, livesRemaining, updatedProfile, isDaily)
    }

    suspend fun recordDailyCompleted(dateStr: String, stars: Int, score: Int, elapsedSeconds: Int) = withContext(Dispatchers.IO) {
        val profile = getProfile()
        dao.insertDailyRecord(
            DailyRecord(
                date = dateStr,
                completed = true,
                score = score,
                stars = stars
            )
        )

        // Streak check
        val newStreak = profile.streak + 1
        var bonusCoins = 50
        if (newStreak % 7 == 0) bonusCoins += 150
        else if (newStreak % 3 == 0) bonusCoins += 50

        dao.updatePlayerProfile(
            profile.copy(
                streak = newStreak,
                coins = profile.coins + bonusCoins,
                xp = profile.xp + 250,
                totalPuzzlesPlayed = profile.totalPuzzlesPlayed + 1,
                totalPuzzlesSolved = profile.totalPuzzlesSolved + 1,
                totalPlayTimeSeconds = profile.totalPlayTimeSeconds + elapsedSeconds
            )
        )

        // Achievement check for daily
        val achList = dao.getAllAchievementsFlow().firstOrNull() ?: emptyList()
        achList.find { it.id == "daily_dedication" }?.let { ach ->
            if (!ach.isUnlocked) {
                dao.updateAchievement(
                    ach.copy(
                        currentProgress = 1,
                        isUnlocked = true,
                        unlockedDate = getTodayDateString()
                    )
                )
            }
        }
    }

    private suspend fun checkAndUpdateAchievements(
        levelId: Int,
        stars: Int,
        elapsedSeconds: Int,
        livesRemaining: Int,
        profile: PlayerProfile,
        isDaily: Boolean
    ) {
        val achList = dao.getAllAchievementsFlow().firstOrNull() ?: emptyList()
        val todayStr = getTodayDateString()

        for (ach in achList) {
            if (ach.isUnlocked) continue
            var shouldUnlock = false
            var newProgress = ach.currentProgress

            when (ach.id) {
                "first_step" -> {
                    if (profile.totalPuzzlesSolved >= 1) {
                        newProgress = 1
                        shouldUnlock = true
                    }
                }
                "brain_starter" -> {
                    newProgress = profile.totalPuzzlesSolved
                    if (newProgress >= 5) shouldUnlock = true
                }
                "puzzle_master" -> {
                    newProgress = profile.totalPuzzlesSolved
                    if (newProgress >= 20) shouldUnlock = true
                }
                "master_mind" -> {
                    newProgress = profile.totalPuzzlesSolved
                    if (newProgress >= 30) shouldUnlock = true
                }
                "no_mistake" -> {
                    if (livesRemaining == 3) {
                        newProgress = 1
                        shouldUnlock = true
                    }
                }
                "speed_brain" -> {
                    if (elapsedSeconds <= 30) {
                        newProgress = 1
                        shouldUnlock = true
                    }
                }
                "streak_fire" -> {
                    newProgress = profile.streak
                    if (newProgress >= 3) shouldUnlock = true
                }
                "coin_collector" -> {
                    newProgress = profile.coins
                    if (newProgress >= 500) shouldUnlock = true
                }
            }

            if (newProgress != ach.currentProgress || shouldUnlock) {
                dao.updateAchievement(
                    ach.copy(
                        currentProgress = minOf(newProgress, ach.maxProgress),
                        isUnlocked = shouldUnlock,
                        unlockedDate = if (shouldUnlock) todayStr else ""
                    )
                )
            }
        }
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        dao.clearLevelRecords()
        dao.clearDailyRecords()
        val todayStr = getTodayDateString()
        dao.insertPlayerProfile(
            PlayerProfile(
                id = 1,
                level = 1,
                xp = 0,
                coins = 150,
                streak = 1,
                lastActiveDate = todayStr,
                highestUnlockedLevel = 1,
                totalPuzzlesPlayed = 0,
                totalPuzzlesSolved = 0,
                totalPlayTimeSeconds = 0L,
                soundEnabled = true,
                musicEnabled = true,
                hapticEnabled = true,
                tutorialCompleted = false
            )
        )
        dao.insertOrUpdateLevelRecord(LevelRecord(levelId = 1))
        dao.insertAchievements(AchievementCatalog.initialAchievements)
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
