package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class HiddenObjectController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    data class HiddenArtifact(
        val id: String,
        val name: String,
        val pos: Vec3,
        val color: Color,
        var isFound: Boolean = false
    )

    val artifacts = mutableListOf<HiddenArtifact>()
    var solved = false
    private var steps = 0
    private var mistakes = 0

    init {
        setupArtifacts(levelDef.id)
    }

    private fun setupArtifacts(id: Int) {
        if (id % 2 == 0) {
            artifacts.add(
                HiddenArtifact(
                    id = "art_core",
                    name = "Quantum Core Kristal",
                    pos = Vec3(4.2f, 0.4f, -4.2f), // Behind right pillar
                    color = Color(0xFF00E5FF)
                )
            )
            artifacts.add(
                HiddenArtifact(
                    id = "art_chip",
                    name = "Micro-Chip Memori",
                    pos = Vec3(-3.2f, 1.2f, -5.6f), // Near left back beam
                    color = Color(0xFFFFD166)
                )
            )
        } else {
            artifacts.add(
                HiddenArtifact(
                    id = "art_capsule",
                    name = "Energy Matrix Battery",
                    pos = Vec3(-4.2f, -0.6f, 3.8f), // Near front-left pillar base
                    color = Color(0xFF06D6A0)
                )
            )
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        for (art in artifacts) {
            if (art.isFound) continue
            val bob = sin(timeSec * 4f + art.pos.x) * 0.08f
            val curPos = Vec3(art.pos.x, art.pos.y + bob, art.pos.z)

            // Glowing hidden artifact crystal / pyramid
            polys.addAll(
                Mesh3D.createPyramid(
                    center = curPos,
                    baseSize = 0.5f,
                    height = 0.7f,
                    color = art.color,
                    tag = "hidden_${art.id}",
                    outlineColor = Color(0xFFFFFFFF)
                )
            )
            polys.addAll(
                Mesh3D.createCylinder(
                    center = Vec3(curPos.x, curPos.y - 0.2f, curPos.z),
                    radius = 0.18f,
                    height = 0.15f,
                    segments = 6,
                    color = Color(0xFFFFFFFF),
                    tag = "hidden_${art.id}"
                )
            )
        }

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        if (tag.startsWith("hidden_")) {
            val artId = tag.removePrefix("hidden_")
            val target = artifacts.find { it.id == artId }
            if (target != null && !target.isFound) {
                target.isFound = true
                steps++
                onEvent(PuzzleEvent.CorrectMove("Ditemukan: ${target.name}!"))
                val remaining = artifacts.count { !it.isFound }
                if (remaining == 0) {
                    solved = true
                    onEvent(PuzzleEvent.Solved)
                }
            }
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            // Find first remaining artifact
            val remaining = artifacts.find { !it.isFound }
            if (remaining != null) {
                remaining.isFound = true
                if (artifacts.all { it.isFound }) solved = true
            }
        }
    }
}
