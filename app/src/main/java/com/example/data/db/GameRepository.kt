package com.example.data.db

import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType
import com.example.data.models.PuzzleStage
import com.example.data.models.StageProgress
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class GameRepository(private val gameDao: GameDao) {

    val allProgress: Flow<List<StageProgress>> = gameDao.getAllProgress().map { entities ->
        entities.map { it.toDomain() }
    }

    val customStages: Flow<List<PuzzleStage>> = gameDao.getAllCustomStages().map { entities ->
        entities.map { it.toDomain() }
    }

    val userProfile: Flow<UserProfile> = gameDao.getUserProfile().map { entity ->
        entity?.let {
            UserProfile(
                guestName = it.guestName,
                avatarIndex = it.avatarIndex,
                soundEnabled = it.soundEnabled,
                hapticsEnabled = it.hapticsEnabled,
                glowEffectsEnabled = it.glowEffectsEnabled
            )
        } ?: UserProfile()
    }

    suspend fun saveStageProgress(stageId: Int, stars: Int, moves: Int, timeSec: Int) {
        val existing = gameDao.getProgressForStage(stageId)
        val newStars = maxOf(existing?.stars ?: 0, stars)
        val bestMoves = if (existing == null || existing.bestMoves == 0) moves else minOf(existing.bestMoves, moves)
        val bestTime = if (existing == null || existing.bestTimeSeconds == 0) timeSec else minOf(existing.bestTimeSeconds, timeSec)

        val updated = StageProgressEntity(
            stageId = stageId,
            stars = newStars,
            bestMoves = bestMoves,
            bestTimeSeconds = bestTime,
            unlocked = true,
            completed = true
        )
        gameDao.insertOrUpdateProgress(updated)

        // Unlock next stage if stageId < 100
        if (stageId < 100) {
            val nextExisting = gameDao.getProgressForStage(stageId + 1)
            if (nextExisting == null || !nextExisting.unlocked) {
                gameDao.insertOrUpdateProgress(
                    StageProgressEntity(
                        stageId = stageId + 1,
                        stars = nextExisting?.stars ?: 0,
                        bestMoves = nextExisting?.bestMoves ?: 0,
                        bestTimeSeconds = nextExisting?.bestTimeSeconds ?: 0,
                        unlocked = true,
                        completed = nextExisting?.completed ?: false
                    )
                )
            }
        }
    }

    suspend fun saveCustomStage(
        title: String,
        author: String,
        rows: Int,
        cols: Int,
        grid: List<List<ArrowCell?>>,
        parMoves: Int
    ): Long {
        val json = serializeGrid(grid)
        val entity = CustomStageEntity(
            title = title.ifBlank { "Custom Stage" },
            author = author.ifBlank { "Guest" },
            rows = rows,
            cols = cols,
            gridJson = json,
            parMoves = parMoves
        )
        return gameDao.insertCustomStage(entity)
    }

    suspend fun deleteCustomStage(id: Long) {
        gameDao.deleteCustomStage(id)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        gameDao.saveUserProfile(
            UserProfileEntity(
                id = 1,
                guestName = profile.guestName,
                avatarIndex = profile.avatarIndex,
                soundEnabled = profile.soundEnabled,
                hapticsEnabled = profile.hapticsEnabled,
                glowEffectsEnabled = profile.glowEffectsEnabled
            )
        )
    }

    suspend fun resetAllProgress() {
        gameDao.resetAllProgress()
        // Re-unlock Stage 1
        gameDao.insertOrUpdateProgress(
            StageProgressEntity(stageId = 1, unlocked = true, completed = false)
        )
    }

    suspend fun initDefaultProgressIfNeeded() {
        val p1 = gameDao.getProgressForStage(1)
        if (p1 == null) {
            gameDao.insertOrUpdateProgress(
                StageProgressEntity(stageId = 1, unlocked = true, completed = false)
            )
        }
    }

    companion object {
        fun serializeGrid(grid: List<List<ArrowCell?>>): String {
            val jsonArray = JSONArray()
            for (r in grid.indices) {
                val rowArray = JSONArray()
                for (c in grid[r].indices) {
                    val cell = grid[r][c]
                    if (cell == null) {
                        rowArray.put(JSONObject.NULL)
                    } else {
                        val obj = JSONObject()
                        obj.put("id", cell.id)
                        obj.put("type", cell.type.name)
                        obj.put("direction", cell.direction.name)
                        obj.put("color", cell.color.name)
                        obj.put("extraData", cell.extraData)
                        rowArray.put(obj)
                    }
                }
                jsonArray.put(rowArray)
            }
            return jsonArray.toString()
        }

        fun deserializeGrid(jsonStr: String, rows: Int, cols: Int): List<List<ArrowCell?>> {
            val result = MutableList(rows) { MutableList<ArrowCell?>(cols) { null } }
            try {
                val jsonArray = JSONArray(jsonStr)
                for (r in 0 until minOf(rows, jsonArray.length())) {
                    val rowArray = jsonArray.getJSONArray(r)
                    for (c in 0 until minOf(cols, rowArray.length())) {
                        if (!rowArray.isNull(c)) {
                            val obj = rowArray.getJSONObject(c)
                            val id = obj.optInt("id", r * cols + c + 1)
                            val type = CellType.valueOf(obj.optString("type", CellType.ARROW.name))
                            val dir = ArrowDirection.valueOf(obj.optString("direction", ArrowDirection.UP.name))
                            val color = ArrowColor.valueOf(obj.optString("color", ArrowColor.CYAN.name))
                            val extra = obj.optString("extraData", "")
                            result[r][c] = ArrowCell(id, type, dir, color, extra)
                        }
                    }
                }
            } catch (_: Exception) {}
            return result
        }
    }
}

private fun StageProgressEntity.toDomain() = StageProgress(
    stageId = stageId,
    stars = stars,
    bestMoves = bestMoves,
    bestTimeSeconds = bestTimeSeconds,
    unlocked = unlocked,
    completed = completed
)

private fun CustomStageEntity.toDomain(): PuzzleStage {
    val grid = GameRepository.deserializeGrid(gridJson, rows, cols)
    return PuzzleStage(
        id = id.toInt(),
        title = title,
        zoneName = "Custom Workshop",
        zoneIndex = 0,
        rows = rows,
        cols = cols,
        grid = grid,
        parMoves = parMoves,
        isCustom = true,
        author = author,
        description = "Created by $author"
    )
}
