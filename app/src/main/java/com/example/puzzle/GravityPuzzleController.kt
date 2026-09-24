package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import kotlin.math.sin

class GravityPuzzleController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    var ramp1Angle = 0 // 0: flat, 1: tilt right (correct), 2: tilt left
    var ramp2Angle = 2 // 0: tilt right, 1: flat, 2: tilt left (correct)

    var isRolling = false
    var ballProgress = 0f
    var solved = false
    private var steps = 0
    private var mistakes = 0

    fun toggleRamp(rampId: Int, onEvent: (PuzzleEvent) -> Unit) {
        if (solved || isRolling) return
        steps++
        if (rampId == 1) {
            ramp1Angle = (ramp1Angle + 1) % 3
        } else {
            ramp2Angle = (ramp2Angle + 1) % 3
        }
        onEvent(PuzzleEvent.StepTaken(steps))
    }

    fun launchBall(onEvent: (PuzzleEvent) -> Unit) {
        if (solved || isRolling) return
        isRolling = true
        ballProgress = 0f
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        if (isRolling && !solved) {
            ballProgress += 0.025f
            if (ballProgress >= 1.0f) {
                isRolling = false
                // Check win condition: ramp1Angle == 1 and ramp2Angle == 2
                if (ramp1Angle == 1 && ramp2Angle == 2) {
                    solved = true
                } else {
                    mistakes++
                    ballProgress = 0f
                }
            }
        }

        val baseY = -0.6f

        // Top Launch Platform
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(-2.2f, baseY + 1.6f, 0f),
                width = 1.0f,
                height = 0.15f,
                depth = 1.0f,
                color = Color(0xFF1E3A68),
                outlineColor = Color(0xFF00E5FF)
            )
        )

        // Ramp 1 (Upper ramp, adjustable)
        val r1Color = if (ramp1Angle == 1) Color(0xFF06D6A0) else Color(0xFF254170)
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(-0.8f, baseY + 1.0f, 0f),
                width = 1.6f,
                height = 0.12f,
                depth = 0.8f,
                color = r1Color,
                outlineColor = Color(0xFF00E5FF),
                tag = "ramp_1"
            )
        )

        // Ramp 2 (Lower ramp, adjustable)
        val r2Color = if (ramp2Angle == 2) Color(0xFF06D6A0) else Color(0xFF254170)
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(0.8f, baseY + 0.3f, 0f),
                width = 1.6f,
                height = 0.12f,
                depth = 0.8f,
                color = r2Color,
                outlineColor = Color(0xFF00E5FF),
                tag = "ramp_2"
            )
        )

        // Target Basket / Goal Hole
        polys.addAll(
            Mesh3D.createCylinder(
                center = Vec3(2.2f, baseY - 0.2f, 0f),
                radius = 0.5f,
                height = 0.4f,
                segments = 8,
                color = Color(0xFF0D5C43),
                outlineColor = Color(0xFF06D6A0),
                tag = "target_basket"
            )
        )

        // Gravity Ball position
        val ballPos = if (!isRolling && !solved) {
            Vec3(-2.2f, baseY + 1.85f, 0f)
        } else if (solved) {
            Vec3(2.2f, baseY - 0.05f, 0f)
        } else {
            // Interpolate ball along trajectory
            when {
                ballProgress < 0.4f -> {
                    val t = ballProgress / 0.4f
                    Vec3(-2.2f + t * 1.4f, baseY + 1.85f - t * 0.7f, 0f)
                }
                ballProgress < 0.8f -> {
                    val t = (ballProgress - 0.4f) / 0.4f
                    Vec3(-0.8f + t * 1.6f, baseY + 1.15f - t * 0.7f, 0f)
                }
                else -> {
                    val t = (ballProgress - 0.8f) / 0.2f
                    Vec3(0.8f + t * 1.4f, baseY + 0.45f - t * 0.5f, 0f)
                }
            }
        }

        polys.addAll(
            Mesh3D.createCube(
                center = ballPos,
                size = 0.36f,
                frontColor = Color(0xFFFFD166),
                outlineColor = Color(0xFFFFFFFF),
                tagPrefix = "gravity_ball"
            )
        )

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (tag == "ramp_1") toggleRamp(1, onEvent)
        else if (tag == "ramp_2") toggleRamp(2, onEvent)
        else if (tag.startsWith("gravity_ball")) launchBall(onEvent)
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            ramp1Angle = 1
            ramp2Angle = 2
        }
    }
}
