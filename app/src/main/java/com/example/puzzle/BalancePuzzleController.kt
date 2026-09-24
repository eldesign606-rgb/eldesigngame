package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class BalancePuzzleController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    val targetLeftTorque = 12 // e.g. 4kg at dist 3
    var rightSlots = IntArray(3) // distances 1, 2, 3
    var solved = false
    private var steps = 0
    private var mistakes = 0

    // Available weights for player to place
    val availableWeights = listOf(2, 4, 6)
    var selectedWeightIdx = 1 // default 4

    init {
        // Preset: distance 1 has 0, distance 2 has 0, distance 3 has 0
    }

    fun currentRightTorque(): Int {
        var t = 0
        for (i in 0..2) {
            val dist = i + 1
            t += rightSlots[i] * dist
        }
        return t
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        val rTorque = currentRightTorque()
        val diff = targetLeftTorque - rTorque
        val tiltAngle = (diff.toFloat() * 1.5f).coerceIn(-18f, 18f)

        val fulcrumY = -0.6f

        // Central Fulcrum (Piramida Penyangga)
        polys.addAll(
            Mesh3D.createPyramid(
                center = Vec3(0f, fulcrumY - 0.25f, 0f),
                baseSize = 0.9f,
                height = 0.7f,
                color = Color(0xFF1B2F55),
                outlineColor = Color(0xFF00E5FF)
            )
        )

        // Balance Beam (papan jungkat-jungkit)
        val beamLength = 4.2f
        val rad = Math.toRadians(tiltAngle.toDouble()).toFloat()
        val yOffset = kotlin.math.tan(rad) * (beamLength / 2f)

        val leftEnd = Vec3(-beamLength / 2f, fulcrumY + 0.15f + yOffset, 0f)
        val rightEnd = Vec3(beamLength / 2f, fulcrumY + 0.15f - yOffset, 0f)
        val beamColor = if (solved) Color(0xFF06D6A0) else Color(0xFF1E3A68)

        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0f, fulcrumY + 0.15f, 0f),
                width = beamLength,
                height = 0.14f,
                depth = 0.7f,
                color = beamColor,
                outlineColor = if (solved) Color(0xFF38EF7D) else Color(0xFF00E5FF)
            )
        )

        // Left weight (Preset 4kg at distance 3)
        val leftWeightPos = Vec3(-1.8f, fulcrumY + 0.35f + (yOffset * 0.85f), 0f)
        polys.addAll(
            Mesh3D.createCube(
                center = leftWeightPos,
                size = 0.5f,
                frontColor = Color(0xFFFF477E),
                outlineColor = Color(0xFFFFFFFF),
                tagPrefix = "left_weight"
            )
        )

        // Right slots (Distances 1, 2, 3)
        val slotDistances = listOf(0.7f, 1.3f, 1.9f)
        for (i in 0..2) {
            val dist = slotDistances[i]
            val slotY = fulcrumY + 0.18f - (yOffset * (dist / 2.1f))
            val slotPos = Vec3(dist, slotY, 0f)
            val weightHere = rightSlots[i]

            // Slot ring
            polys.addAll(
                Mesh3D.createCylinder(
                    center = slotPos,
                    radius = 0.26f,
                    height = 0.04f,
                    segments = 8,
                    color = Color(0xFF102140),
                    tag = "balance_slot_$i",
                    outlineColor = Color(0xFFFFD166)
                )
            )

            // If weight is placed in this slot
            if (weightHere > 0) {
                val weightSize = 0.3f + weightHere * 0.05f
                polys.addAll(
                    Mesh3D.createCube(
                        center = Vec3(slotPos.x, slotPos.y + weightSize / 2f + 0.02f, slotPos.z),
                        size = weightSize,
                        frontColor = Color(0xFF00E5FF),
                        outlineColor = Color(0xFFFFFFFF),
                        tagPrefix = "right_weight_$i"
                    )
                )
            }
        }

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (tag.startsWith("balance_slot_")) {
            val slotIdx = tag.removePrefix("balance_slot_").toIntOrNull() ?: return
            steps++
            // Cycle weight: 0 -> 2 -> 4 -> 6 -> 0
            val current = rightSlots[slotIdx]
            rightSlots[slotIdx] = when (current) {
                0 -> 2
                2 -> 4
                4 -> 6
                else -> 0
            }

            val rTorque = currentRightTorque()
            if (rTorque == targetLeftTorque) {
                solved = true
                onEvent(PuzzleEvent.CorrectMove("Timbangan seimbang sempurna! (Torsi: $rTorque)"))
                onEvent(PuzzleEvent.Solved)
            } else {
                onEvent(PuzzleEvent.StepTaken(steps))
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            // Place correct weight directly into slot 2 (dist 3, weight 4 -> 12)
            rightSlots[0] = 0
            rightSlots[1] = 0
            rightSlots[2] = 4
            solved = true
        }
    }
}
