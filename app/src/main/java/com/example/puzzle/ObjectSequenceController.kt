package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.cos
import kotlin.math.sin

class ObjectSequenceController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    data class ObjItem(val shape: String, val color: Color, val scale: Float)

    val sequence = listOf(
        ObjItem("cube", Color(0xFF00E5FF), 0.35f),
        ObjItem("pyramid", Color(0xFF9D4EDD), 0.45f),
        ObjItem("cylinder", Color(0xFFFF477E), 0.55f),
        ObjItem("cube", Color(0xFFFFD166), 0.65f)
    )

    val options = listOf(
        ObjItem("pyramid", Color(0xFF06D6A0), 0.75f), // Correct: next in cycle (cube->pyramid), growing scale
        ObjItem("cylinder", Color(0xFF00E5FF), 0.35f),
        ObjItem("cube", Color(0xFFFF477E), 0.5f)
    )

    val correctIndex = 0
    var solved = false
    var eliminatedOptions = mutableSetOf<Int>()
    private var steps = 0
    private var mistakes = 0

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        // Orbit ring of 4 sequence objects + 1 mystery slot
        val radius = 2.2f
        val count = sequence.size + 1
        val centerY = -0.3f

        for (i in 0 until sequence.size) {
            val angle = timeSec * 0.4f + (2.0 * Math.PI * i / count).toFloat()
            val ox = radius * cos(angle)
            val oz = radius * sin(angle) - 0.5f
            val item = sequence[i]
            val bob = sin(timeSec * 3f + i) * 0.1f

            polys.addAll(renderItem(item, Vec3(ox, centerY + bob, oz), null))
        }

        // 5th Mystery slot
        val mAngle = timeSec * 0.4f + (2.0 * Math.PI * sequence.size / count).toFloat()
        val mx = radius * cos(mAngle)
        val mz = radius * sin(mAngle) - 0.5f
        val mBob = sin(timeSec * 3f + sequence.size) * 0.1f

        if (solved) {
            polys.addAll(renderItem(options[correctIndex], Vec3(mx, centerY + mBob, mz), null))
        } else {
            polys.addAll(
                Mesh3D.createCube(
                    center = Vec3(mx, centerY + mBob, mz),
                    size = 0.65f,
                    frontColor = Color(0xFF3B2D60),
                    outlineColor = Color(0xFFFFD166),
                    tag = "mystery_slot"
                )
            )
        }

        // Choice pedestals in foreground
        val optStartX = -((options.size - 1) * 1.5f) / 2f
        for ((idx, opt) in options.withIndex()) {
            if (eliminatedOptions.contains(idx)) continue
            val ox = optStartX + idx * 1.5f
            val oCenter = Vec3(ox, -0.6f, 1.4f)
            val ringColor = if (solved && idx == correctIndex) Color(0xFF06D6A0) else Color(0xFF00E5FF)

            polys.addAll(renderItem(opt, oCenter, "seq_opt_$idx"))
            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(ox, -1.2f, 1.4f),
                    radius = 0.42f,
                    height = 0.5f,
                    segments = 8,
                    color = Color(0xFF162544),
                    tag = "seq_opt_$idx",
                    outlineColor = ringColor
                )
            )
        }

        return polys
    }

    private fun renderItem(item: ObjItem, pos: Vec3, tag: String?): List<Polygon3D> {
        return when (item.shape) {
            "pyramid" -> Mesh3D.createPyramid(pos, item.scale, item.scale * 1.2f, item.color, tag = tag)
            "cylinder" -> Mesh3D.createCylinder(pos, item.scale / 2f, item.scale, 8, item.color, tag = tag)
            else -> Mesh3D.createCube(pos, item.scale, item.color, tagPrefix = tag ?: "obj_cube")
        }
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (tag.startsWith("seq_opt_")) {
            val idx = tag.removePrefix("seq_opt_").toIntOrNull() ?: return
            steps++
            if (idx == correctIndex) {
                solved = true
                onEvent(PuzzleEvent.CorrectMove("Transformasi objek berhasil ditebak!"))
                onEvent(PuzzleEvent.Solved)
            } else {
                mistakes++
                onEvent(PuzzleEvent.WrongMove("Pilihan salah! Cermati siklus bentuk dan pertambahan ukuran."))
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            for (i in options.indices) {
                if (i != correctIndex) eliminatedOptions.add(i)
            }
        }
    }
}
