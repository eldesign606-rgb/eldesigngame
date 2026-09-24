package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class MemoryCubeController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    enum class Mode { DEMO, PLAYER }

    val sequence = mutableListOf<Int>()
    val playerInput = mutableListOf<Int>()
    var mode = Mode.DEMO
    var demoIndex = 0
    var activeFlashIndex = -1
    var solved = false
    private var steps = 0
    private var mistakes = 0
    private var flashTimer = 0f

    // 4 Colored panels around central cube: 0: Cyan, 1: Emas, 2: Magenta, 3: Hijau
    val panelColors = listOf(
        Color(0xFF00E5FF),
        Color(0xFFFFD166),
        Color(0xFFFF477E),
        Color(0xFF06D6A0)
    )

    init {
        setupSequence(levelDef.id)
    }

    private fun setupSequence(id: Int) {
        val count = when {
            id <= 5 -> 3
            id <= 15 -> 4
            id <= 25 -> 5
            else -> 6
        }
        val preset = listOf(0, 2, 1, 3, 0, 1, 2)
        for (i in 0 until count) {
            sequence.add(preset[(i + id) % preset.size])
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        // Update demo flashing sequence
        if (mode == Mode.DEMO && !solved) {
            flashTimer += 0.035f
            if (flashTimer > 1.2f) {
                flashTimer = 0f
                demoIndex++
                if (demoIndex >= sequence.size) {
                    mode = Mode.PLAYER
                    activeFlashIndex = -1
                }
            }
            if (mode == Mode.DEMO && demoIndex < sequence.size) {
                activeFlashIndex = if (flashTimer < 0.8f) sequence[demoIndex] else -1
            }
        }

        val cubeY = 0.0f
        val bob = sin(timeSec * 2f) * 0.08f

        // Central Core
        polys.addAll(
            Mesh3D.createCube(
                center = Vec3(0f, cubeY + bob, 0f),
                size = 1.3f,
                frontColor = Color(0xFF142036),
                outlineColor = Color(0xFF00E5FF)
            )
        )

        // 4 Memory Panels around core
        val panelOffsets = listOf(
            Vec3(0f, cubeY + bob, 0.72f),   // Front
            Vec3(0.72f, cubeY + bob, 0f),   // Right
            Vec3(0f, cubeY + bob, -0.72f),  // Back
            Vec3(-0.72f, cubeY + bob, 0f)   // Left
        )

        for (i in 0..3) {
            val p = panelOffsets[i]
            val isFlashing = (activeFlashIndex == i)
            val baseColor = panelColors[i]
            val displayColor = if (isFlashing) Color(0xFFFFFFFF) else baseColor.copy(alpha = 0.75f)
            val outline = if (isFlashing) Color(0xFFFFFFFF) else baseColor

            polys.addAll(
                Mesh3D.createBox(
                    center = p,
                    width = if (i % 2 == 0) 0.88f else 0.12f,
                    height = 0.88f,
                    depth = if (i % 2 == 0) 0.12f else 0.88f,
                    color = displayColor,
                    outlineColor = outline,
                    tag = "memory_panel_$i"
                )
            )
        }

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (mode == Mode.DEMO) {
            onEvent(PuzzleEvent.WrongMove("Perhatikan pola kilatan cahaya terlebih dahulu!"))
            return
        }

        if (tag.startsWith("memory_panel_")) {
            val idx = tag.removePrefix("memory_panel_").toIntOrNull() ?: return
            steps++
            activeFlashIndex = idx

            val expected = sequence[playerInput.size]
            if (idx == expected) {
                playerInput.add(idx)
                onEvent(PuzzleEvent.CorrectMove("Langkah ${playerInput.size}/${sequence.size} tepat!"))
                if (playerInput.size == sequence.size) {
                    solved = true
                    onEvent(PuzzleEvent.Solved)
                }
            } else {
                mistakes++
                playerInput.clear()
                // Restart demo sequence
                mode = Mode.DEMO
                demoIndex = 0
                flashTimer = 0f
                onEvent(PuzzleEvent.WrongMove("Urutan salah! Hafalkan kembali polanya."))
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        // Replay demo
        mode = Mode.DEMO
        demoIndex = 0
        flashTimer = 0f
        playerInput.clear()
    }
}
