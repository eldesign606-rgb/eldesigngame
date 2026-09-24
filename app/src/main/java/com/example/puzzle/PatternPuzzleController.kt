package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class PatternPuzzleController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    data class Token(val shape: String, val color: Color, val label: String)

    val sequenceTokens = mutableListOf<Token>()
    val options = mutableListOf<Token>()
    var correctIndex = 0
    var solved = false
    var eliminatedOptions = mutableSetOf<Int>()
    private var steps = 0
    private var mistakes = 0

    init {
        setupLevel(levelDef.id)
    }

    private fun setupLevel(id: Int) {
        when (id % 4) {
            1 -> {
                // Alternating: Sphere/Cube -> Pyramid -> Sphere/Cube -> Pyramid -> ?
                val t1 = Token("cube", Color(0xFF00E5FF), "Kubus Cyan")
                val t2 = Token("pyramid", Color(0xFFFFD166), "Piramida Emas")
                val t3 = Token("cylinder", Color(0xFFFF477E), "Silinder Merah")
                sequenceTokens.addAll(listOf(t1, t1, t2, t1, t1))
                options.addAll(listOf(t2, t1, t3))
                correctIndex = 0 // Pyramid
            }
            2 -> {
                // Growing sides: Triangle(3) -> Square(4) -> Pentagon(5) -> ? (Hexagon)
                val t1 = Token("pyramid", Color(0xFF06D6A0), "3 Sudut")
                val t2 = Token("cube", Color(0xFF00E5FF), "4 Sudut")
                val t3 = Token("cylinder", Color(0xFF9D4EDD), "5 Sudut")
                val t4 = Token("cube", Color(0xFFFFD166), "6 Sudut")
                sequenceTokens.addAll(listOf(t1, t2, t3))
                options.addAll(listOf(t1, t4, t2))
                correctIndex = 1 // 6 sides
            }
            3 -> {
                // Color spectrum shift
                val t1 = Token("cube", Color(0xFF00E5FF), "Cyan")
                val t2 = Token("cube", Color(0xFF9D4EDD), "Ungu")
                val t3 = Token("cube", Color(0xFFFF477E), "Magenta")
                val t4 = Token("cube", Color(0xFFFFD166), "Emas")
                sequenceTokens.addAll(listOf(t1, t2, t3))
                options.addAll(listOf(t4, t1, t2))
                correctIndex = 0
            }
            else -> {
                val t1 = Token("cylinder", Color(0xFF38EF7D), "Silinder Hijau")
                val t2 = Token("pyramid", Color(0xFF00E5FF), "Piramida Biru")
                val t3 = Token("cube", Color(0xFFFF9E00), "Kubus Oranye")
                sequenceTokens.addAll(listOf(t1, t2, t3, t1, t2))
                options.addAll(listOf(t2, t1, t3))
                correctIndex = 2 // Kubus Oranye
            }
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        // Render sequence tokens on pedestal
        val startX = -((sequenceTokens.size) * 1.0f) / 2f
        for ((idx, tok) in sequenceTokens.withIndex()) {
            val px = startX + idx * 1.0f
            val bob = sin(timeSec * 3f + idx) * 0.1f
            val center = Vec3(px, -0.6f + bob, -0.8f)
            polys.addAll(renderTokenMesh(tok, center, 0.45f, tag = null))
            // Stand under token
            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(px, -1.3f, -0.8f),
                    radius = 0.25f,
                    height = 0.8f,
                    segments = 6,
                    color = Color(0xFF1E2F50),
                    outlineColor = Color(0xFF00E5FF)
                )
            )
        }

        // Render mystery box at end of sequence
        val mysteryX = startX + sequenceTokens.size * 1.0f
        val bobM = sin(timeSec * 3f + sequenceTokens.size) * 0.1f
        polys.addAll(
            Mesh3D.createCube(
                center = Vec3(mysteryX, -0.6f + bobM, -0.8f),
                size = 0.45f,
                frontColor = if (solved) options[correctIndex].color else Color(0xFF553388),
                outlineColor = Color(0xFFFFD166),
                tag = "mystery_target"
            )
        )
        polys.addAll(
            Mesh3D.createCylinder(
                center = Vec3(mysteryX, -1.3f, -0.8f),
                radius = 0.25f,
                height = 0.8f,
                segments = 6,
                color = Color(0xFF2E1B4E),
                outlineColor = Color(0xFFFFD166)
            )
        )

        // Render 3 Option pedestals in foreground
        val optStartX = -((options.size - 1) * 1.6f) / 2f
        for ((idx, tok) in options.withIndex()) {
            if (eliminatedOptions.contains(idx)) continue
            val ox = optStartX + idx * 1.6f
            val oCenter = Vec3(ox, -0.5f, 1.2f)
            val isSelected = solved && idx == correctIndex
            val ringColor = if (isSelected) Color(0xFF06D6A0) else Color(0xFF00E5FF)

            // Interactive token
            polys.addAll(renderTokenMesh(tok, oCenter, 0.5f, tag = "pattern_opt_$idx"))
            // Interactive pedestal button
            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(ox, -1.2f, 1.2f),
                    radius = 0.45f,
                    height = 0.6f,
                    segments = 8,
                    color = Color(0xFF16294D),
                    tag = "pattern_opt_$idx",
                    outlineColor = ringColor
                )
            )
        }

        return polys
    }

    private fun renderTokenMesh(token: Token, center: Vec3, size: Float, tag: String?): List<Polygon3D> {
        return when (token.shape) {
            "pyramid" -> Mesh3D.createPyramid(center, size, size * 1.2f, token.color, tag = tag)
            "cylinder" -> Mesh3D.createCylinder(center, size / 2f, size, 8, token.color, tag = tag)
            else -> Mesh3D.createCube(center, size, token.color, tagPrefix = tag ?: "cube")
        }
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (tag.startsWith("pattern_opt_")) {
            val idx = tag.removePrefix("pattern_opt_").toIntOrNull() ?: return
            steps++
            if (idx == correctIndex) {
                solved = true
                onEvent(PuzzleEvent.CorrectMove("Pola berhasil terpecahkan!"))
                onEvent(PuzzleEvent.Solved)
            } else {
                mistakes++
                onEvent(PuzzleEvent.WrongMove("Pola salah! Coba amati urutan dengan saksama."))
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        when (tier) {
            1 -> { /* general text */ }
            2 -> {
                // Eliminate one wrong option
                for (i in options.indices) {
                    if (i != correctIndex && !eliminatedOptions.contains(i)) {
                        eliminatedOptions.add(i)
                        break
                    }
                }
            }
            3 -> {
                // Eliminate all except correct
                for (i in options.indices) {
                    if (i != correctIndex) eliminatedOptions.add(i)
                }
            }
        }
    }
}
