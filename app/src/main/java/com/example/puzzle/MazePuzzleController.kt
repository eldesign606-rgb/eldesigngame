package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class MazePuzzleController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    data class Point(val r: Int, val c: Int)

    val rows = 5
    val cols = 5

    // 0 = path, 1 = wall, 2 = start, 3 = exit
    val grid = Array(rows) { IntArray(cols) }
    var playerPos = Point(0, 0)
    var exitPos = Point(4, 4)
    var solved = false
    private var steps = 0
    private var mistakes = 0

    init {
        setupMaze(levelDef.id)
    }

    private fun setupMaze(id: Int) {
        // Preset solvable mazes
        val layouts = listOf(
            arrayOf(
                intArrayOf(2, 0, 1, 1, 1),
                intArrayOf(1, 0, 0, 0, 1),
                intArrayOf(1, 1, 1, 0, 1),
                intArrayOf(1, 0, 0, 0, 0),
                intArrayOf(1, 0, 1, 1, 3)
            ),
            arrayOf(
                intArrayOf(2, 0, 0, 1, 1),
                intArrayOf(1, 1, 0, 0, 1),
                intArrayOf(1, 0, 0, 1, 1),
                intArrayOf(1, 0, 1, 0, 0),
                intArrayOf(1, 0, 0, 0, 3)
            ),
            arrayOf(
                intArrayOf(2, 1, 1, 1, 1),
                intArrayOf(0, 0, 0, 0, 1),
                intArrayOf(1, 1, 1, 0, 1),
                intArrayOf(1, 0, 0, 0, 1),
                intArrayOf(1, 0, 1, 0, 3)
            )
        )
        val selected = layouts[(id - 1) % layouts.size]
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                grid[r][c] = selected[r][c]
                if (grid[r][c] == 2) playerPos = Point(r, c)
                if (grid[r][c] == 3) exitPos = Point(r, c)
            }
        }
    }

    fun move(dr: Int, dc: Int, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        val nr = playerPos.r + dr
        val nc = playerPos.c + dc
        if (nr in 0 until rows && nc in 0 until cols) {
            if (grid[nr][nc] == 1) {
                mistakes++
                onEvent(PuzzleEvent.WrongMove("Menabrak dinding labirin!"))
            } else {
                playerPos = Point(nr, nc)
                steps++
                onEvent(PuzzleEvent.StepTaken(steps))
                if (playerPos == exitPos) {
                    solved = true
                    onEvent(PuzzleEvent.CorrectMove("Berhasil mencapai jalan keluar!"))
                    onEvent(PuzzleEvent.Solved)
                }
            }
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        val cellSize = 0.9f
        val startX = -((cols - 1) * cellSize) / 2f
        val startZ = -((rows - 1) * cellSize) / 2f
        val baseY = -1.0f

        // Render Maze Base Platform
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0f, baseY - 0.25f, 0f),
                width = cols * cellSize + 0.5f,
                height = 0.3f,
                depth = rows * cellSize + 0.5f,
                color = Color(0xFF14223D),
                outlineColor = Color(0xFF00E5FF)
            )
        )

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cx = startX + c * cellSize
                val cz = startZ + r * cellSize
                val cellType = grid[r][c]

                when (cellType) {
                    1 -> {
                        // Wall block (Red/crimson sci-fi barricade)
                        val wallHeight = 0.8f
                        polys.addAll(
                            Mesh3D.createBox(
                                center = Vec3(cx, baseY + wallHeight / 2f, cz),
                                width = cellSize * 0.92f,
                                height = wallHeight,
                                depth = cellSize * 0.92f,
                                color = Color(0xFF8B1E3F),
                                outlineColor = Color(0xFFFF477E),
                                tag = "maze_wall_${r}_$c"
                            )
                        )
                    }
                    3 -> {
                        // Exit portal (Glowing emerald)
                        val bob = sin(timeSec * 4f) * 0.08f
                        polys.addAll(
                            Mesh3D.createBox(
                                center = Vec3(cx, baseY + 0.05f, cz),
                                width = cellSize * 0.88f,
                                height = 0.1f,
                                depth = cellSize * 0.88f,
                                color = Color(0xFF06D6A0),
                                outlineColor = Color(0xFF38EF7D),
                                tag = "maze_exit"
                            )
                        )
                        polys.addAll(
                            Mesh3D.createCylinder(
                                center = Vec3(cx, baseY + 0.4f + bob, cz),
                                radius = 0.28f,
                                height = 0.45f,
                                segments = 8,
                                color = Color(0xFF38EF7D),
                                outlineColor = Color(0xFFFFFFFF),
                                tag = "maze_tile_${r}_$c"
                            )
                        )
                    }
                    else -> {
                        // Floor Path (slate gray with cyan edge)
                        val isStart = (r == 0 && c == 0 && cellType == 2)
                        val tileColor = if (isStart) Color(0xFF0D3B66) else Color(0xFF1B2A4A)
                        polys.addAll(
                            Mesh3D.createBox(
                                center = Vec3(cx, baseY + 0.04f, cz),
                                width = cellSize * 0.88f,
                                height = 0.08f,
                                depth = cellSize * 0.88f,
                                color = tileColor,
                                outlineColor = Color(0xFF28487A),
                                tag = "maze_tile_${r}_$c"
                            )
                        )
                    }
                }
            }
        }

        // Render Player Drone (Cyan Sphere/Box with levitation)
        val playerX = startX + playerPos.c * cellSize
        val playerZ = startZ + playerPos.r * cellSize
        val playerBob = sin(timeSec * 5f) * 0.12f
        polys.addAll(
            Mesh3D.createCube(
                center = Vec3(playerX, baseY + 0.48f + playerBob, playerZ),
                size = 0.42f,
                frontColor = Color(0xFF00E5FF),
                outlineColor = Color(0xFFFFFFFF),
                tagPrefix = "player_drone"
            )
        )
        // Shadow ring under player
        polys.addAll(
            Mesh3D.createCylinder(
                center = Vec3(playerX, baseY + 0.09f, playerZ),
                radius = 0.22f,
                height = 0.02f,
                segments = 8,
                color = Color(0x66000000)
            )
        )

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (tag.startsWith("maze_tile_")) {
            val parts = tag.removePrefix("maze_tile_").split("_")
            if (parts.size == 2) {
                val tr = parts[0].toIntOrNull() ?: return
                val tc = parts[1].toIntOrNull() ?: return
                // Check if adjacent to player
                val dr = tr - playerPos.r
                val dc = tc - playerPos.c
                if ((kotlin.math.abs(dr) == 1 && dc == 0) || (kotlin.math.abs(dc) == 1 && dr == 0)) {
                    move(dr, dc, onEvent)
                }
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        // No-op for direct pathing hint
    }
}
