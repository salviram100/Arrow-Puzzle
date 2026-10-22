package com.example.game.solver

import com.example.data.models.ArrowCell
import com.example.data.models.ArrowDirection
import com.example.data.models.CellType

object PuzzleEngine {

    sealed class TapResult {
        data class Escaped(
            val path: List<Pair<Int, Int>>,
            val newGrid: List<List<ArrowCell?>>
        ) : TapResult()

        data class Blocked(
            val blockedAtRow: Int,
            val blockedAtCol: Int,
            val reason: String
        ) : TapResult()

        object NotAnArrow : TapResult()
    }

    /**
     * Attempts to fly the arrow at (row, col).
     */
    fun attemptMove(
        grid: List<List<ArrowCell?>>,
        row: Int,
        col: Int
    ): TapResult {
        val rows = grid.size
        if (rows == 0) return TapResult.NotAnArrow
        val cols = grid[0].size
        if (row !in 0 until rows || col !in 0 until cols) return TapResult.NotAnArrow

        val cell = grid[row][col] ?: return TapResult.NotAnArrow
        if (!cell.isArrow()) return TapResult.NotAnArrow

        val path = mutableListOf<Pair<Int, Int>>()
        path.add(row to col)

        var curR = row
        var curC = col
        var curDir = cell.direction

        // Trace ray in arrow direction
        val visitedPortals = mutableSetOf<Pair<Int, Int>>()
        val maxSteps = rows * cols * 2
        var steps = 0

        while (steps < maxSteps) {
            steps++
            val nextR = curR + curDir.dRow
            val nextC = curC + curDir.dCol

            // Check if exited boundary
            if (nextR !in 0 until rows || nextC !in 0 until cols) {
                // Successfully escaped outside board!
                val newGrid = grid.mapIndexed { r, rowList ->
                    rowList.mapIndexed { c, cellItem ->
                        if (r == row && c == col) null else cellItem
                    }
                }
                return TapResult.Escaped(path, newGrid)
            }

            val occupant = grid[nextR][nextC]
            if (occupant != null) {
                when (occupant.type) {
                    CellType.ARROW, CellType.ROTATOR -> {
                        return TapResult.Blocked(nextR, nextC, "Blocked by another arrow")
                    }
                    CellType.OBSTACLE -> {
                        return TapResult.Blocked(nextR, nextC, "Blocked by cyber barrier")
                    }
                    CellType.PORTAL_A -> {
                        // Find PORTAL_B on the board
                        val portalB = findPortal(grid, CellType.PORTAL_B)
                        if (portalB != null && (nextR to nextC) !in visitedPortals) {
                            visitedPortals.add(nextR to nextC)
                            path.add(nextR to nextC)
                            curR = portalB.first
                            curC = portalB.second
                            path.add(curR to curC)
                            continue
                        } else {
                            return TapResult.Blocked(nextR, nextC, "Portal loop")
                        }
                    }
                    CellType.PORTAL_B -> {
                        val portalA = findPortal(grid, CellType.PORTAL_A)
                        if (portalA != null && (nextR to nextC) !in visitedPortals) {
                            visitedPortals.add(nextR to nextC)
                            path.add(nextR to nextC)
                            curR = portalA.first
                            curC = portalA.second
                            path.add(curR to curC)
                            continue
                        } else {
                            return TapResult.Blocked(nextR, nextC, "Portal loop")
                        }
                    }
                    CellType.EMPTY -> {
                        curR = nextR
                        curC = nextC
                        path.add(curR to curC)
                    }
                    CellType.COLOR_GATE -> {
                        // Check if color gate matches cell color
                        if (occupant.color == cell.color) {
                            curR = nextR
                            curC = nextC
                            path.add(curR to curC)
                        } else {
                            return TapResult.Blocked(nextR, nextC, "Color Gate mismatch")
                        }
                    }
                }
            } else {
                curR = nextR
                curC = nextC
                path.add(curR to curC)
            }
        }

        return TapResult.Blocked(curR, curC, "Path loop")
    }

    private fun findPortal(grid: List<List<ArrowCell?>>, targetType: CellType): Pair<Int, Int>? {
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c]?.type == targetType) return r to c
            }
        }
        return null
    }

    /**
     * Finds all currently unblocked arrows that can be cleared right now.
     */
    fun findAvailableMoves(grid: List<List<ArrowCell?>>): List<Pair<Int, Int>> {
        val available = mutableListOf<Pair<Int, Int>>()
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                val cell = grid[r][c]
                if (cell != null && cell.isArrow()) {
                    if (attemptMove(grid, r, c) is TapResult.Escaped) {
                        available.add(r to c)
                    }
                }
            }
        }
        return available
    }

    /**
     * Solves the board from current state to completion using BFS/DFS.
     * Returns the sequence of moves (row, col) to win, or null if unsolvable.
     */
    fun solve(grid: List<List<ArrowCell?>>, maxDepth: Int = 50): List<Pair<Int, Int>>? {
        val solution = mutableListOf<Pair<Int, Int>>()
        var currentGrid = grid

        while (true) {
            val totalRemaining = currentGrid.sumOf { row -> row.count { it?.isArrow() == true } }
            if (totalRemaining == 0) return solution

            val available = findAvailableMoves(currentGrid)
            if (available.isEmpty()) return null // Deadlock / unsolvable

            // Pick the first available move
            val nextMove = available.first()
            val res = attemptMove(currentGrid, nextMove.first, nextMove.second)
            if (res is TapResult.Escaped) {
                solution.add(nextMove)
                currentGrid = res.newGrid
            } else {
                return null
            }
        }
    }

    /**
     * Check if a board is valid and completely solvable.
     */
    fun isSolvable(grid: List<List<ArrowCell?>>): Boolean {
        val totalArrows = grid.sumOf { row -> row.count { it?.isArrow() == true } }
        if (totalArrows == 0) return false
        val sol = solve(grid)
        return sol != null && sol.size == totalArrows
    }
}
