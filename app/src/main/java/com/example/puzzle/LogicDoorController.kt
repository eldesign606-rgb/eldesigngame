package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3

class LogicDoorController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    data class LogicQuestion(
        val equation: String,
        val prompt: String,
        val options: List<String>,
        val correctIndex: Int
    )

    lateinit var question: LogicQuestion
    var doorOpenAnim = 0f
    var solved = false
    var eliminatedOptions = mutableSetOf<Int>()
    private var steps = 0
    private var mistakes = 0

    init {
        setupQuestion(levelDef.id)
    }

    private fun setupQuestion(id: Int) {
        question = when (id % 4) {
            1 -> LogicQuestion(
                equation = "A = 2,  B = 3,  C = 4",
                prompt = "Berapa nilai dari  A + B × C ?",
                options = listOf("20", "14", "24", "11"),
                correctIndex = 1 // 2 + 12 = 14
            )
            2 -> LogicQuestion(
                equation = "X = 5,  Y = 3",
                prompt = "Hitung:  (X × 2) - Y + 4 = ?",
                options = listOf("11", "9", "15", "13"),
                correctIndex = 0 // 10 - 3 + 4 = 11
            )
            3 -> LogicQuestion(
                equation = "P + Q = 10,  P - Q = 4",
                prompt = "Berapakah hasil dari  P × Q ?",
                options = listOf("24", "21", "18", "16"),
                correctIndex = 1 // P=7, Q=3 => 21
            )
            else -> LogicQuestion(
                equation = "Deret: 2, 4, 8, 16, ...",
                prompt = "Angka berikutnya dalam deret kuadrat?",
                options = listOf("24", "30", "32", "64"),
                correctIndex = 2 // 32
            )
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        if (solved && doorOpenAnim < 1.4f) {
            doorOpenAnim += 0.05f
        }

        val doorZ = -1.2f
        val doorY = 0.2f
        val doorColor = if (solved) Color(0xFF0F382E) else Color(0xFF1E2D48)
        val outline = if (solved) Color(0xFF06D6A0) else Color(0xFF00E5FF)

        // Door frame
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0f, doorY + 1.8f, doorZ),
                width = 3.6f,
                height = 0.4f,
                depth = 0.3f,
                color = Color(0xFF16233B),
                outlineColor = outline
            )
        )

        // Sliding door left panel
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(-0.75f - doorOpenAnim, doorY + 0.7f, doorZ),
                width = 1.4f,
                height = 2.0f,
                depth = 0.18f,
                color = doorColor,
                outlineColor = outline
            )
        )

        // Sliding door right panel
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0.75f + doorOpenAnim, doorY + 0.7f, doorZ),
                width = 1.4f,
                height = 2.0f,
                depth = 0.18f,
                color = doorColor,
                outlineColor = outline
            )
        )

        // Hologram Question Board above door
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0f, doorY + 2.2f, doorZ),
                width = 3.0f,
                height = 0.6f,
                depth = 0.08f,
                color = Color(0xFF0C1B33),
                outlineColor = Color(0xFF00E5FF),
                tag = "door_display"
            )
        )

        // Interactive Keypad Choice Pedestals in front of door
        val startX = -((question.options.size - 1) * 1.0f) / 2f
        for ((idx, _) in question.options.withIndex()) {
            if (eliminatedOptions.contains(idx)) continue
            val px = startX + idx * 1.0f
            val btnColor = if (solved && idx == question.correctIndex) Color(0xFF06D6A0) else Color(0xFF00E5FF)

            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(px, -0.6f, 0.8f),
                    radius = 0.35f,
                    height = 0.5f,
                    segments = 8,
                    color = Color(0xFF182846),
                    tag = "door_btn_$idx",
                    outlineColor = btnColor
                )
            )
            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(px, -0.32f, 0.8f),
                    radius = 0.22f,
                    height = 0.12f,
                    segments = 8,
                    color = btnColor,
                    tag = "door_btn_$idx"
                )
            )
        }

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (tag.startsWith("door_btn_")) {
            val idx = tag.removePrefix("door_btn_").toIntOrNull() ?: return
            steps++
            if (idx == question.correctIndex) {
                solved = true
                onEvent(PuzzleEvent.CorrectMove("Sandi benar! Pintu laboratorium terbuka."))
                onEvent(PuzzleEvent.Solved)
            } else {
                mistakes++
                onEvent(PuzzleEvent.WrongMove("Sandi salah! Periksa kembali perhitungan logika."))
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        when (tier) {
            1 -> { /* text hint */ }
            2 -> {
                for (i in question.options.indices) {
                    if (i != question.correctIndex && !eliminatedOptions.contains(i)) {
                        eliminatedOptions.add(i)
                        break
                    }
                }
            }
            3 -> {
                for (i in question.options.indices) {
                    if (i != question.correctIndex) eliminatedOptions.add(i)
                }
            }
        }
    }
}
