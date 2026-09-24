package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class CircuitPuzzleController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    enum class TileType { BATTERY, STRAIGHT, CORNER, T_SPLIT, LAMP }

    data class Tile(
        val type: TileType,
        var rotation: Int, // 0: 0 deg, 1: 90 deg, 2: 180 deg, 3: 270 deg
        val correctRotation: Int,
        var isPowered: Boolean = false
    )

    val rows = 3
    val cols = 3
    val grid = Array(rows) { r ->
        Array(cols) { c ->
            when {
                r == 0 && c == 0 -> Tile(TileType.BATTERY, 0, 0, true)
                r == 2 && c == 2 -> Tile(TileType.LAMP, 0, 0, false)
                (r + c) % 2 == 1 -> Tile(TileType.CORNER, (r * 2 + c + 1) % 4, (r + c) % 4, false)
                else -> Tile(TileType.STRAIGHT, (r + 1) % 2, 0, false)
            }
        }
    }

    var solved = false
    private var steps = 0
    private var mistakes = 0

    init {
        evaluateCircuit()
    }

    fun rotateTile(r: Int, c: Int, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        val tile = grid[r][c]
        if (tile.type == TileType.BATTERY) return

        steps++
        tile.rotation = (tile.rotation + 1) % 4
        evaluateCircuit()

        if (grid[2][2].isPowered) {
            solved = true
            onEvent(PuzzleEvent.CorrectMove("Seluruh sirkuit terhubung sempurna! Lampu menyala."))
            onEvent(PuzzleEvent.Solved)
        } else {
            onEvent(PuzzleEvent.StepTaken(steps))
        }
    }

    private fun evaluateCircuit() {
        // Reset power
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r != 0 || c != 0) grid[r][c].isPowered = false
            }
        }

        // BFS flow from (0, 0)
        val queue = ArrayDeque<Pair<Int, Int>>()
        queue.add(Pair(0, 0))

        while (queue.isNotEmpty()) {
            val (cr, cc) = queue.removeFirst()
            val cTile = grid[cr][cc]
            val cExits = getTileExits(cTile)

            val neighbors = listOf(
                Pair(cr - 1, cc) to 0, // North
                Pair(cr, cc + 1) to 1, // East
                Pair(cr + 1, cc) to 2, // South
                Pair(cr, cc - 1) to 3  // West
            )

            for ((nPos, outDir) in neighbors) {
                val (nr, nc) = nPos
                if (nr in 0 until rows && nc in 0 until cols) {
                    val nTile = grid[nr][nc]
                    if (!nTile.isPowered) {
                        val inDir = (outDir + 2) % 4
                        val nExits = getTileExits(nTile)
                        if (cExits.contains(outDir) && nExits.contains(inDir)) {
                            nTile.isPowered = true
                            queue.add(Pair(nr, nc))
                        }
                    }
                }
            }
        }
    }

    private fun getTileExits(tile: Tile): Set<Int> {
        val baseExits = when (tile.type) {
            TileType.BATTERY -> setOf(1, 2) // East & South
            TileType.STRAIGHT -> setOf(0, 2) // North & South
            TileType.CORNER -> setOf(1, 2) // East & South
            TileType.T_SPLIT -> setOf(1, 2, 3) // East, South, West
            TileType.LAMP -> setOf(0, 3) // North & West
        }
        return baseExits.map { (it + tile.rotation) % 4 }.toSet()
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        val tileSize = 1.05f
        val startX = -((cols - 1) * tileSize) / 2f
        val startZ = -((rows - 1) * tileSize) / 2f
        val baseY = -0.7f

        // Grid board platform
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0f, baseY - 0.2f, 0f),
                width = cols * tileSize + 0.4f,
                height = 0.2f,
                depth = rows * tileSize + 0.4f,
                color = Color(0xFF13223D),
                outlineColor = Color(0xFF00E5FF)
            )
        )

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cx = startX + c * tileSize
                val cz = startZ + r * tileSize
                val tile = grid[r][c]

                val isLit = tile.isPowered
                val wireColor = if (isLit) Color(0xFF00E5FF) else Color(0xFF335577)
                val wireOutline = if (isLit) Color(0xFFFFFFFF) else Color(0x33000000)

                // Tile base plate
                polys.addAll(
                    Mesh3D.createBox(
                        center = Vec3(cx, baseY, cz),
                        width = tileSize * 0.92f,
                        height = 0.08f,
                        depth = tileSize * 0.92f,
                        color = Color(0xFF1B2B47),
                        outlineColor = if (isLit) Color(0xFF00E5FF) else Color(0xFF233A5E),
                        tag = "circuit_tile_${r}_$c"
                    )
                )

                // Wire segment or lamp
                when (tile.type) {
                    TileType.BATTERY -> {
                        // Battery cylinder
                        polys.addAll(
                            Mesh3D.createCylinder(
                                center = Vec3(cx, baseY + 0.25f, cz),
                                radius = 0.26f,
                                height = 0.42f,
                                segments = 8,
                                color = Color(0xFFFFD166),
                                tag = "circuit_tile_${r}_$c",
                                outlineColor = Color(0xFFFFFFFF)
                            )
                        )
                    }
                    TileType.LAMP -> {
                        // Lamp bulb
                        val lampColor = if (isLit) Color(0xFF06D6A0) else Color(0xFF4A5568)
                        val bulbY = baseY + 0.28f + (if (isLit) sin(timeSec * 6f) * 0.04f else 0f)
                        polys.addAll(
                            Mesh3D.createCube(
                                center = Vec3(cx, bulbY, cz),
                                size = 0.42f,
                                frontColor = lampColor,
                                outlineColor = if (isLit) Color(0xFFFFFFFF) else Color(0xFF718096),
                                tag = "circuit_tile_${r}_$c"
                            )
                        )
                    }
                    else -> {
                        // Rotatable wire bar
                        val isStraight = (tile.type == TileType.STRAIGHT)
                        val angleDeg = tile.rotation * 90f
                        val isRotated = (tile.rotation % 2 != 0)

                        polys.addAll(
                            Mesh3D.createBox(
                                center = Vec3(cx, baseY + 0.07f, cz),
                                width = if (isRotated) 0.65f else 0.2f,
                                height = 0.06f,
                                depth = if (isRotated) 0.2f else 0.65f,
                                color = wireColor,
                                outlineColor = wireOutline,
                                tag = "circuit_tile_${r}_$c"
                            )
                        )
                    }
                }
            }
        }

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (tag.startsWith("circuit_tile_")) {
            val parts = tag.removePrefix("circuit_tile_").split("_")
            if (parts.size == 2) {
                val r = parts[0].toIntOrNull() ?: return
                val c = parts[1].toIntOrNull() ?: return
                rotateTile(r, c, onEvent)
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            // Force correct rotation on diagonal path
            grid[0][1].rotation = 1
            grid[1][1].rotation = 1
            grid[1][2].rotation = 2
            evaluateCircuit()
            if (grid[2][2].isPowered) solved = true
        }
    }
}
