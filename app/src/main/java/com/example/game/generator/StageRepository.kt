package com.example.game.generator

import com.example.data.models.ArrowCell
import com.example.data.models.ArrowColor
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType
import com.example.data.models.PuzzleStage
import com.example.game.solver.PuzzleEngine
import kotlin.random.Random

object StageRepository {

    val ZONES = listOf(
        ZoneInfo(1, "Vector Gateway", 1, 20, "Learn the fundamental vector laws"),
        ZoneInfo(2, "Neon Nexus", 21, 40, "Obstacles & diagonal vector physics"),
        ZoneInfo(3, "Quantum Circuit", 41, 60, "Color gates and resonance channels"),
        ZoneInfo(4, "Cyber Matrix", 61, 80, "Portal warps and spatial mazes"),
        ZoneInfo(5, "Velocity Singularity", 81, 100, "Master-tier 100th stage challenges")
    )

    data class ZoneInfo(
        val index: Int,
        val name: String,
        val startStage: Int,
        val endStage: Int,
        val description: String
    )

    private val cachedStages = mutableMapOf<Int, PuzzleStage>()

    fun getStage(stageId: Int): PuzzleStage {
        val clampedId = stageId.coerceIn(1, 100)
        return cachedStages.getOrPut(clampedId) {
            generateStage(clampedId)
        }
    }

    fun getAllStages(): List<PuzzleStage> {
        return (1..100).map { getStage(it) }
    }

    private fun generateStage(id: Int): PuzzleStage {
        val zone = ZONES.first { id in it.startStage..it.endStage }
        val random = Random(id * 7919L + 31)

        // Handcrafted milestone designs or procedural reverse generation
        val (rows, cols, grid) = when {
            id == 1 -> createTutorial1()
            id == 2 -> createTutorial2()
            id == 3 -> createTutorial3()
            id == 4 -> createTutorial4()
            id == 5 -> createTutorial5()
            id == 10 -> createMilestone10()
            id == 20 -> createMilestone20()
            id == 50 -> createMilestone50()
            id == 100 -> createMilestone100()
            else -> generateProceduralStage(id, zone, random)
        }

        val totalArrows = grid.sumOf { row -> row.count { it?.isArrow() == true } }
        val parMoves = totalArrows

        return PuzzleStage(
            id = id,
            title = "Stage $id: ${getStageSubtitle(id)}",
            zoneName = zone.name,
            zoneIndex = zone.index,
            rows = rows,
            cols = cols,
            grid = grid,
            parMoves = parMoves,
            isCustom = false,
            author = "Velocity Games",
            description = "Clear all $totalArrows vector arrows in optimal sequence."
        )
    }

    private fun getStageSubtitle(id: Int): String = when (id) {
        1 -> "First Vector"
        2 -> "Crossing Paths"
        3 -> "Double Lock"
        4 -> "Corner Tangle"
        5 -> "Quad Direction"
        10 -> "Vector Nexus"
        20 -> "Gateway Pinnacle"
        25 -> "Neon Spiral"
        50 -> "Quantum Equilibrium"
        75 -> "Warp Dimension"
        100 -> "Velocity Singularity"
        in 1..20 -> "Gateway Seq $id"
        in 21..40 -> "Neon Drift ${id - 20}"
        in 41..60 -> "Quantum Grid ${id - 40}"
        in 61..80 -> "Matrix Mesh ${id - 60}"
        else -> "Singularity ${id - 80}"
    }

    // Handcrafted Stage 1: 3x3 simple 2 arrows
    private fun createTutorial1(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 3
        val cols = 3
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 1 && c == 1 -> ArrowCell(1, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.CYAN)
                    r == 1 && c == 2 -> ArrowCell(2, CellType.ARROW, ArrowDirection.UP, ArrowColor.AMBER)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    // Handcrafted Stage 2: 3x3 3 arrows in a chain
    private fun createTutorial2(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 3
        val cols = 3
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 0 && c == 1 -> ArrowCell(1, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 1 && c == 1 -> ArrowCell(2, CellType.ARROW, ArrowDirection.UP, ArrowColor.AMBER)
                    r == 2 && c == 1 -> ArrowCell(3, CellType.ARROW, ArrowDirection.UP, ArrowColor.MAGENTA)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    // Handcrafted Stage 3: 3x3 4-way pinwheel
    private fun createTutorial3(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 3
        val cols = 3
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 0 && c == 0 -> ArrowCell(1, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 0 && c == 2 -> ArrowCell(2, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.AMBER)
                    r == 2 && c == 2 -> ArrowCell(3, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.MAGENTA)
                    r == 2 && c == 0 -> ArrowCell(4, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.GREEN)
                    r == 1 && c == 1 -> ArrowCell(5, CellType.ARROW, ArrowDirection.UP, ArrowColor.PURPLE)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    // Handcrafted Stage 4: 3x3 Cross lock
    private fun createTutorial4(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 3
        val cols = 3
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 1 && c == 0 -> ArrowCell(1, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.CYAN)
                    r == 1 && c == 1 -> ArrowCell(2, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.AMBER)
                    r == 0 && c == 1 -> ArrowCell(3, CellType.ARROW, ArrowDirection.UP, ArrowColor.GREEN)
                    r == 2 && c == 1 -> ArrowCell(4, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.MAGENTA)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    // Handcrafted Stage 5: 4x4 Spiral
    private fun createTutorial5(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 4
        val cols = 4
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 1 && c == 1 -> ArrowCell(1, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.CYAN)
                    r == 1 && c == 2 -> ArrowCell(2, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.AMBER)
                    r == 2 && c == 2 -> ArrowCell(3, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.MAGENTA)
                    r == 2 && c == 1 -> ArrowCell(4, CellType.ARROW, ArrowDirection.UP, ArrowColor.GREEN)
                    r == 0 && c == 1 -> ArrowCell(5, CellType.ARROW, ArrowDirection.UP, ArrowColor.PURPLE)
                    r == 3 && c == 2 -> ArrowCell(6, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.BLUE)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    private fun createMilestone10(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 4
        val cols = 4
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 0 && c == 0 -> ArrowCell(1, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.CYAN)
                    r == 0 && c == 3 -> ArrowCell(2, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.AMBER)
                    r == 3 && c == 3 -> ArrowCell(3, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.MAGENTA)
                    r == 3 && c == 0 -> ArrowCell(4, CellType.ARROW, ArrowDirection.UP, ArrowColor.GREEN)
                    r == 1 && c == 1 -> ArrowCell(5, CellType.ARROW, ArrowDirection.UP, ArrowColor.PURPLE)
                    r == 1 && c == 2 -> ArrowCell(6, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.BLUE)
                    r == 2 && c == 2 -> ArrowCell(7, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.CYAN)
                    r == 2 && c == 1 -> ArrowCell(8, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.AMBER)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    private fun createMilestone20(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 4
        val cols = 4
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 0 && c == 1 -> ArrowCell(1, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 0 && c == 2 -> ArrowCell(2, CellType.ARROW, ArrowDirection.UP, ArrowColor.AMBER)
                    r == 1 && c == 0 -> ArrowCell(3, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.MAGENTA)
                    r == 1 && c == 3 -> ArrowCell(4, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.GREEN)
                    r == 2 && c == 0 -> ArrowCell(5, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.PURPLE)
                    r == 2 && c == 3 -> ArrowCell(6, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.BLUE)
                    r == 3 && c == 1 -> ArrowCell(7, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.CYAN)
                    r == 3 && c == 2 -> ArrowCell(8, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.AMBER)
                    r == 1 && c == 1 -> ArrowCell(9, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.GREEN)
                    r == 1 && c == 2 -> ArrowCell(10, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.MAGENTA)
                    r == 2 && c == 2 -> ArrowCell(11, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.PURPLE)
                    r == 2 && c == 1 -> ArrowCell(12, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    private fun createMilestone50(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 5
        val cols = 5
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 2 && c == 2 -> ArrowCell(99, CellType.OBSTACLE, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 0 -> ArrowCell(r * 5 + c, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 4 -> ArrowCell(r * 5 + c, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.AMBER)
                    c == 0 -> ArrowCell(r * 5 + c, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.MAGENTA)
                    c == 4 -> ArrowCell(r * 5 + c, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.GREEN)
                    r == 1 && c == 1 -> ArrowCell(10, CellType.ARROW, ArrowDirection.UP, ArrowColor.PURPLE)
                    r == 1 && c == 3 -> ArrowCell(11, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.BLUE)
                    r == 3 && c == 3 -> ArrowCell(12, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.CYAN)
                    r == 3 && c == 1 -> ArrowCell(13, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.AMBER)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    private fun createMilestone100(): Triple<Int, Int, List<List<ArrowCell?>>> {
        val rows = 6
        val cols = 6
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    (r == 2 && c == 2) || (r == 3 && c == 3) -> ArrowCell(r * 6 + c, CellType.PORTAL_A, ArrowDirection.UP, ArrowColor.PURPLE)
                    (r == 2 && c == 3) || (r == 3 && c == 2) -> ArrowCell(r * 6 + c, CellType.PORTAL_B, ArrowDirection.UP, ArrowColor.PURPLE)
                    r == 0 -> ArrowCell(r * 6 + c, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == 5 -> ArrowCell(r * 6 + c, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.MAGENTA)
                    c == 0 -> ArrowCell(r * 6 + c, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.AMBER)
                    c == 5 -> ArrowCell(r * 6 + c, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.GREEN)
                    r == 1 && c == 1 -> ArrowCell(20, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.BLUE)
                    r == 1 && c == 4 -> ArrowCell(21, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.PURPLE)
                    r == 4 && c == 4 -> ArrowCell(22, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.CYAN)
                    r == 4 && c == 1 -> ArrowCell(23, CellType.ARROW, ArrowDirection.UP, ArrowColor.AMBER)
                    r == 1 && c == 2 -> ArrowCell(24, CellType.ARROW, ArrowDirection.UP, ArrowColor.GREEN)
                    r == 1 && c == 3 -> ArrowCell(25, CellType.ARROW, ArrowDirection.UP, ArrowColor.MAGENTA)
                    r == 4 && c == 2 -> ArrowCell(26, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.BLUE)
                    r == 4 && c == 3 -> ArrowCell(27, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.PURPLE)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }

    /**
     * Procedural Reverse Construction Algorithm:
     * Guarantees 100% solvability for every generated level by starting with an empty board
     * and iteratively inserting arrows that have an unobstructed path backwards from the edge.
     */
    private fun generateProceduralStage(
        id: Int,
        zone: ZoneInfo,
        random: Random
    ): Triple<Int, Int, List<List<ArrowCell?>>> {
        val (rows, cols) = when (zone.index) {
            1 -> if (id <= 10) 3 to 3 else 4 to 4
            2 -> if (id <= 30) 4 to 4 else 5 to 5
            3 -> 5 to 5
            4 -> if (id <= 70) 5 to 5 else 6 to 6
            else -> if (id <= 90) 6 to 6 else 6 to 6
        }

        val targetArrows = when (zone.index) {
            1 -> (3 + (id * 0.4)).toInt().coerceIn(3, 10)
            2 -> (8 + ((id - 20) * 0.4)).toInt().coerceIn(8, 16)
            3 -> (12 + ((id - 40) * 0.4)).toInt().coerceIn(12, 20)
            4 -> (15 + ((id - 60) * 0.4)).toInt().coerceIn(15, 24)
            else -> (18 + ((id - 80) * 0.4)).toInt().coerceIn(18, 28)
        }

        val useDiagonals = zone.index >= 2
        val useObstacles = zone.index >= 2 && (id % 3 == 0)
        val usePortals = zone.index >= 4 && (id % 5 == 0)

        // Try constructing backwards
        var attempts = 0
        while (attempts < 50) {
            attempts++
            val board = MutableList(rows) { MutableList<ArrowCell?>(cols) { null } }
            var cellIdCounter = 1

            // Place obstacles if any
            if (useObstacles) {
                val numObstacles = if (zone.index >= 3) 2 else 1
                for (o in 0 until numObstacles) {
                    val or = random.nextInt(1, rows - 1)
                    val oc = random.nextInt(1, cols - 1)
                    board[or][oc] = ArrowCell(cellIdCounter++, CellType.OBSTACLE, ArrowDirection.UP, ArrowColor.CYAN)
                }
            }

            // Place portals if zone 4/5
            if (usePortals) {
                val p1r = random.nextInt(0, rows)
                val p1c = random.nextInt(0, cols)
                var p2r = random.nextInt(0, rows)
                var p2c = random.nextInt(0, cols)
                if (p1r == p2r && p1c == p2c) {
                    p2r = (p1r + 1) % rows
                }
                if (board[p1r][p1c] == null && board[p2r][p2c] == null) {
                    board[p1r][p1c] = ArrowCell(cellIdCounter++, CellType.PORTAL_A, ArrowDirection.UP, ArrowColor.PURPLE)
                    board[p2r][p2c] = ArrowCell(cellIdCounter++, CellType.PORTAL_B, ArrowDirection.UP, ArrowColor.PURPLE)
                }
            }

            // Reverse placement: start empty, insert arrows where pointing direction towards edge is currently CLEAR!
            var arrowsPlaced = 0
            val maxInnerAttempts = 200
            var innerAttempts = 0

            val cardinalDirs = listOf(ArrowDirection.UP, ArrowDirection.RIGHT, ArrowDirection.DOWN, ArrowDirection.LEFT)
            val allDirs = ArrowDirection.entries.toList()
            val allowedDirs = if (useDiagonals) allDirs else cardinalDirs

            while (arrowsPlaced < targetArrows && innerAttempts < maxInnerAttempts) {
                innerAttempts++
                val r = random.nextInt(rows)
                val c = random.nextInt(cols)

                if (board[r][c] != null) continue

                // Pick a direction that has a clear path to edge
                val shuffledDirs = allowedDirs.shuffled(random)
                var placed = false

                for (dir in shuffledDirs) {
                    // Check if path in `dir` to edge is unobstructed
                    if (isPathClearToEdge(board, r, c, dir, rows, cols)) {
                        val color = ArrowColor.entries[random.nextInt(ArrowColor.entries.size)]
                        board[r][c] = ArrowCell(
                            id = cellIdCounter++,
                            type = CellType.ARROW,
                            direction = dir,
                            color = color
                        )
                        arrowsPlaced++
                        placed = true
                        break
                    }
                }
            }

            // Verify with solver
            if (arrowsPlaced >= 3 && PuzzleEngine.isSolvable(board)) {
                return Triple(rows, cols, board)
            }
        }

        // Fallback default safe solvable grid
        return createFallbackStage(rows, cols)
    }

    private fun isPathClearToEdge(
        board: List<List<ArrowCell?>>,
        startR: Int,
        startC: Int,
        dir: ArrowDirection,
        rows: Int,
        cols: Int
    ): Boolean {
        var r = startR + dir.dRow
        var c = startC + dir.dCol
        while (r in 0 until rows && c in 0 until cols) {
            if (board[r][c] != null) return false
            r += dir.dRow
            c += dir.dCol
        }
        return true
    }

    private fun createFallbackStage(rows: Int, cols: Int): Triple<Int, Int, List<List<ArrowCell?>>> {
        var id = 1
        val grid = List(rows) { r ->
            List(cols) { c ->
                when {
                    r == 0 -> ArrowCell(id++, CellType.ARROW, ArrowDirection.UP, ArrowColor.CYAN)
                    r == rows - 1 -> ArrowCell(id++, CellType.ARROW, ArrowDirection.DOWN, ArrowColor.AMBER)
                    c == 0 -> ArrowCell(id++, CellType.ARROW, ArrowDirection.LEFT, ArrowColor.MAGENTA)
                    c == cols - 1 -> ArrowCell(id++, CellType.ARROW, ArrowDirection.RIGHT, ArrowColor.GREEN)
                    else -> null
                }
            }
        }
        return Triple(rows, cols, grid)
    }
}
