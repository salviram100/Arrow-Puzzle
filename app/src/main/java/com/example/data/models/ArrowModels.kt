package com.example.data.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple

enum class ArrowDirection(
    val angle: Float,
    val dRow: Int,
    val dCol: Int,
    val symbol: String,
    val label: String
) {
    UP(0f, -1, 0, "↑", "Up"),
    RIGHT(90f, 0, 1, "→", "Right"),
    DOWN(180f, 1, 0, "↓", "Down"),
    LEFT(270f, 0, -1, "←", "Left"),
    UP_RIGHT(45f, -1, 1, "↗", "Up-Right"),
    DOWN_RIGHT(135f, 1, 1, "↘", "Down-Right"),
    DOWN_LEFT(225f, 1, -1, "↙", "Down-Left"),
    UP_LEFT(315f, -1, -1, "↖", "Up-Left");

    fun nextClockwise(allowDiagonals: Boolean = false): ArrowDirection {
        return if (allowDiagonals) {
            when (this) {
                UP -> UP_RIGHT
                UP_RIGHT -> RIGHT
                RIGHT -> DOWN_RIGHT
                DOWN_RIGHT -> DOWN
                DOWN -> DOWN_LEFT
                DOWN_LEFT -> LEFT
                LEFT -> UP_LEFT
                UP_LEFT -> UP
            }
        } else {
            when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP_RIGHT -> DOWN_RIGHT
                DOWN_RIGHT -> DOWN_LEFT
                DOWN_LEFT -> UP_LEFT
                UP_LEFT -> UP_RIGHT
            }
        }
    }
}

enum class CellType {
    EMPTY,
    ARROW,
    OBSTACLE,
    PORTAL_A,
    PORTAL_B,
    ROTATOR,      // Rotates arrows that fly into or tap
    COLOR_GATE    // Allows arrows of matching color to pass
}

enum class ArrowColor(val colorValue: Long, val displayName: String) {
    CYAN(0xFF00F0FF, "Cyan"),
    AMBER(0xFFFFB800, "Amber"),
    MAGENTA(0xFFFF007A, "Magenta"),
    GREEN(0xFF00FF88, "Green"),
    PURPLE(0xFFA855F7, "Purple"),
    BLUE(0xFF3B82F6, "Blue");

    fun toComposeColor(): Color = when (this) {
        CYAN -> NeonCyan
        AMBER -> NeonAmber
        MAGENTA -> NeonMagenta
        GREEN -> NeonGreen
        PURPLE -> NeonPurple
        BLUE -> NeonBlue
    }

    fun next(): ArrowColor {
        val entries = entries
        return entries[(ordinal + 1) % entries.size]
    }
}

data class ArrowCell(
    val id: Int,
    val type: CellType = CellType.ARROW,
    val direction: ArrowDirection = ArrowDirection.UP,
    val color: ArrowColor = ArrowColor.CYAN,
    val extraData: String = ""
) {
    fun isArrow(): Boolean = type == CellType.ARROW || type == CellType.ROTATOR
    fun isImpassable(): Boolean = type == CellType.OBSTACLE
}

data class PuzzleStage(
    val id: Int,
    val title: String,
    val zoneName: String,
    val zoneIndex: Int,
    val rows: Int,
    val cols: Int,
    val grid: List<List<ArrowCell?>>,
    val parMoves: Int,
    val isCustom: Boolean = false,
    val author: String = "Velocity Games",
    val description: String = ""
) {
    val totalArrows: Int
        get() = grid.sumOf { row -> row.count { it?.isArrow() == true } }
}

data class StageProgress(
    val stageId: Int,
    val stars: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeSeconds: Int = 0,
    val unlocked: Boolean = false,
    val completed: Boolean = false
)

data class UserProfile(
    val guestName: String = "Velocity_Pilot",
    val avatarIndex: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true
)

data class FlyingArrowAnimation(
    val id: Int,
    val startRow: Int,
    val startCol: Int,
    val direction: ArrowDirection,
    val color: ArrowColor,
    val timestamp: Long = System.currentTimeMillis()
)
