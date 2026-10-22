package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM stage_progress ORDER BY stageId ASC")
    fun getAllProgress(): Flow<List<StageProgressEntity>>

    @Query("SELECT * FROM stage_progress WHERE stageId = :stageId")
    suspend fun getProgressForStage(stageId: Int): StageProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: StageProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProgress(list: List<StageProgressEntity>)

    // Custom stages
    @Query("SELECT * FROM custom_stages ORDER BY createdAt DESC")
    fun getAllCustomStages(): Flow<List<CustomStageEntity>>

    @Query("SELECT * FROM custom_stages WHERE id = :id")
    suspend fun getCustomStageById(id: Long): CustomStageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomStage(stage: CustomStageEntity): Long

    @Query("DELETE FROM custom_stages WHERE id = :id")
    suspend fun deleteCustomStage(id: Long)

    // User profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM stage_progress")
    suspend fun resetAllProgress()
}
