package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Mat4
import com.example.engine3d.Mesh3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3

class RotateCubeController(
    override val levelDef: LevelDefinition
) : PuzzleController {

    var rotXDeg = 0f
    var rotYDeg = 0f

    // Target angle (e.g. 0, 90, 180, 270)
    var targetRotX = 0f
    var targetRotY = 180f
    var solved = false
    private var steps = 0
    private var mistakes = 0

    init {
        // Scramble initial rotation
        rotXDeg = ((levelDef.id * 90) % 360).toFloat()
        rotYDeg = ((levelDef.id * 180 + 90) % 360).toFloat()
        targetRotX = 0f
        targetRotY = 0f
    }

    fun rotate(dx: Float, dy: Float, onEvent: (PuzzleEvent) -> Unit) {
        if (solved) return
        steps++
        rotYDeg = (rotYDeg + dy) % 360f
        rotXDeg = (rotXDeg + dx) % 360f

        // Normalize
        if (rotYDeg < 0) rotYDeg += 360f
        if (rotXDeg < 0) rotXDeg += 360f

        checkSolution(onEvent)
    }

    private fun checkSolution(onEvent: (PuzzleEvent) -> Unit) {
        val diffX = kotlin.math.abs(rotXDeg - targetRotX)
        val diffY = kotlin.math.abs(rotYDeg - targetRotY)

        val isMatchX = diffX < 15f || diffX > 345f
        val isMatchY = diffY < 15f || diffY > 345f

        if (isMatchX && isMatchY) {
            solved = true
            onEvent(PuzzleEvent.CorrectMove("Orientasi kubus sempurna!"))
            onEvent(PuzzleEvent.Solved)
        } else {
            onEvent(PuzzleEvent.StepTaken(steps))
        }
    }

    override fun getPolygons(timeSec: Float): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        polys.addAll(Mesh3D.createBrainLabRoom())

        // Build base cube faces
        val rawPolys = Mesh3D.createCube(
            center = Vec3(0f, 0f, 0f),
            size = 1.6f,
            frontColor = Color(0xFFFFD166), // Target: Golden Star
            backColor = Color(0xFF00E5FF),  // Cyan
            topColor = Color(0xFFFF477E),   // Red
            bottomColor = Color(0xFF06D6A0),// Green
            leftColor = Color(0xFF9D4EDD),  // Purple
            rightColor = Color(0xFF3A86FF), // Blue
            tagPrefix = "rot_cube"
        )

        // Rotate vertices by rotX and rotY
        val rotMat = Mat4.rotationX(rotXDeg) * Mat4.rotationY(rotYDeg)
        val cubeCenter = Vec3(0f, 0.1f, 0f)

        for (p in rawPolys) {
            val rotatedVerts = p.vertices.map { v ->
                rotMat.transform(v) + cubeCenter
            }
            polys.add(p.copy(vertices = rotatedVerts))
        }

        // Target Frame Hologram in top right
        polys.addAll(
            Mesh3D.createBox(
                center = Vec3(2.4f, 1.4f, -0.5f),
                width = 0.8f,
                height = 0.8f,
                depth = 0.1f,
                color = Color(0xFFFFD166),
                outlineColor = Color(0xFFFFFFFF),
                tag = "target_preview"
            )
        )

        return polys
    }

    override fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit) {
        if (tag.startsWith("rot_cube")) {
            rotate(0f, 90f, onEvent)
        }
    }

    override fun getStepCount(): Int = steps
    override fun getMistakeCount(): Int = mistakes

    override fun applyHint(tier: Int) {
        if (tier >= 2) {
            rotXDeg = targetRotX
            rotYDeg = targetRotY
            solved = true
        }
    }
}
