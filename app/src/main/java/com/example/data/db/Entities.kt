package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stage_progress")
data class StageProgressEntity(
    @PrimaryKey val stageId: Int,
    val stars: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeSeconds: Int = 0,
    val unlocked: Boolean = false,
    val completed: Boolean = false
)

@Entity(tableName = "custom_stages")
data class CustomStageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String,
    val rows: Int,
    val cols: Int,
    val gridJson: String,
    val parMoves: Int,
    val stars: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val guestName: String = "Velocity_Pilot",
    val avatarIndex: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val totalPlayTimeSeconds: Long = 0
)
