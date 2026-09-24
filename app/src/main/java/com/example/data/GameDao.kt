package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getPlayerProfileFlow(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayerProfileSync(): PlayerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerProfile(profile: PlayerProfile)

    @Update
    suspend fun updatePlayerProfile(profile: PlayerProfile)

    @Query("SELECT * FROM level_records")
    fun getAllLevelRecordsFlow(): Flow<List<LevelRecord>>

    @Query("SELECT * FROM level_records WHERE levelId = :levelId LIMIT 1")
    suspend fun getLevelRecord(levelId: Int): LevelRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLevelRecord(record: LevelRecord)

    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<AchievementRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(list: List<AchievementRecord>)

    @Update
    suspend fun updateAchievement(achievement: AchievementRecord)

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun getDailyRecordFlow(date: String): Flow<DailyRecord?>

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getDailyRecordSync(date: String): DailyRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyRecord(record: DailyRecord)

    @Query("DELETE FROM level_records")
    suspend fun clearLevelRecords()

    @Query("DELETE FROM daily_challenges")
    suspend fun clearDailyRecords()
}
