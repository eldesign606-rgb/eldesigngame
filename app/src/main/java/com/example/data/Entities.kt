package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 100,
    val streak: Int = 1,
    val lastActiveDate: String = "",
    val highestUnlockedLevel: Int = 1,
    val totalPuzzlesPlayed: Int = 0,
    val totalPuzzlesSolved: Int = 0,
    val totalPlayTimeSeconds: Long = 0L,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val tutorialCompleted: Boolean = false
)

@Entity(tableName = "level_records")
data class LevelRecord(
    @PrimaryKey val levelId: Int,
    val stars: Int = 0,
    val highScore: Int = 0,
    val bestTimeSeconds: Int = 0,
    val isCompleted: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementRecord(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val currentProgress: Int = 0,
    val maxProgress: Int = 1,
    val isUnlocked: Boolean = false,
    val unlockedDate: String = ""
)

@Entity(tableName = "daily_challenges")
data class DailyRecord(
    @PrimaryKey val date: String,
    val completed: Boolean = false,
    val score: Int = 0,
    val stars: Int = 0
)
