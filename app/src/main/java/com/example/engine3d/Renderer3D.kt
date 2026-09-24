package com.example.engine3d

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.max
import kotlin.math.min

data class ProjectedPolygon(
    val points: List<Offset>,
    val depth: Float,
    val color: Color,
    val outlineColor: Color?,
    val tag: String?
)

@Composable
fun BrainLab3DCanvas(
    modifier: Modifier = Modifier,
    camera: Camera3D,
    polygons: List<Polygon3D>,
    particles: List<Particle3D> = emptyList(),
    allowCameraControl: Boolean = true,
    onObjectTapped: ((String) -> Unit)? = null
) {
    // Retain projected polygons for hit-testing tap
    var currentRenderedPolygons by remember { mutableStateOf<List<ProjectedPolygon>>(emptyList()) }

    var cameraAzimuth by remember { mutableStateOf(camera.azimuthDeg) }
    var cameraElevation by remember { mutableStateOf(camera.elevationDeg) }
    var cameraDistance by remember { mutableStateOf(camera.distance) }

    // Sync camera state with external triggers
    cameraAzimuth = camera.azimuthDeg
    cameraElevation = camera.elevationDeg
    cameraDistance = camera.distance

    val touchModifier = if (allowCameraControl) {
        modifier
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    onObjectTapped?.let { callback ->
                        // Hit-test from topmost (closest depth) to bottom
                        val hit = currentRenderedPolygons.find { poly ->
                            poly.tag != null && isPointInPolygon(tapOffset, poly.points)
                        }
                        hit?.tag?.let { callback(it) }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (zoom != 1.0f) {
                        camera.onZoom(zoom)
                        cameraDistance = camera.distance
                    }
                    if (pan != Offset.Zero) {
                        camera.onDrag(pan.x, pan.y)
                        cameraAzimuth = camera.azimuthDeg
                        cameraElevation = camera.elevationDeg
                    }
                }
            }
    } else {
        modifier.pointerInput(Unit) {
            detectTapGestures { tapOffset ->
                onObjectTapped?.let { callback ->
                    val hit = currentRenderedPolygons.find { poly ->
                        poly.tag != null && isPointInPolygon(tapOffset, poly.points)
                    }
                    hit?.tag?.let { callback(it) }
                }
            }
        }
    }

    Canvas(modifier = touchModifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) return@Canvas

        val aspect = width / height
        val viewMat = camera.getViewMatrix()
        val projMat = camera.getProjectionMatrix(aspect)
        val eyePos = camera.getEyePosition()

        val lightDir = Vec3(0.4f, 0.9f, 0.6f).normalize()

        val projectedList = ArrayList<ProjectedPolygon>(polygons.size)

        for (poly in polygons) {
            val normal = poly.normal()
            val center = poly.center()
            val viewDir = (eyePos - center).normalize()

            // Backface culling
            val dot = normal.dot(viewDir)
            if (!poly.doubleSided && dot <= 0f) continue

            // Transform vertices to clip space & screen space
            var allInFront = true
            var avgDepth = 0f
            val screenPts = ArrayList<Offset>(poly.vertices.size)

            for (v in poly.vertices) {
                val viewV = viewMat.transform(v)
                if (viewV.z > -0.1f) {
                    allInFront = false
                    break
                }
                val projV = projMat.transformVec4(viewV)
                if (projV.w <= 0.01f) {
                    allInFront = false
                    break
                }

                // Normalized Device Coordinates (-1 to 1)
                val ndcX = projV.x / projV.w
                val ndcY = projV.y / projV.w

                val sx = (ndcX + 1f) * 0.5f * width
                val sy = (1f - ndcY) * 0.5f * height
                screenPts.add(Offset(sx, sy))
                avgDepth += -viewV.z
            }

            if (!allInFront || screenPts.size < 3) continue

            avgDepth /= poly.vertices.size.toFloat()

            // Lighting calculation
            val shadedColor = if (poly.isEmissive) {
                poly.baseColor
            } else {
                val diffuse = max(0f, normal.dot(lightDir))
                val intensity = (0.42f + 0.58f * diffuse).coerceIn(0.2f, 1.0f)
                Color(
                    red = (poly.baseColor.red * intensity).coerceIn(0f, 1f),
                    green = (poly.baseColor.green * intensity).coerceIn(0f, 1f),
                    blue = (poly.baseColor.blue * intensity).coerceIn(0f, 1f),
                    alpha = poly.baseColor.alpha
                )
            }

            projectedList.add(
                ProjectedPolygon(
                    points = screenPts,
                    depth = avgDepth,
                    color = shadedColor,
                    outlineColor = poly.outlineColor,
                    tag = poly.tag
                )
            )
        }

        // Painter's algorithm: sort back-to-front (largest depth first)
        projectedList.sortByDescending { it.depth }

        // Draw polygons
        for (p in projectedList) {
            val path = Path().apply {
                moveTo(p.points[0].x, p.points[0].y)
                for (i in 1 until p.points.size) {
                    lineTo(p.points[i].x, p.points[i].y)
                }
                close()
            }
            drawPath(path, p.color, style = Fill)

            if (p.outlineColor != null && p.outlineColor != Color.Transparent) {
                drawPath(path, p.outlineColor, style = Stroke(width = 1.5f))
            }
        }

        // Draw 3D Particles
        for (part in particles) {
            val viewV = viewMat.transform(part.pos)
            if (viewV.z < -0.1f) {
                val projV = projMat.transformVec4(viewV)
                if (projV.w > 0.01f) {
                    val ndcX = projV.x / projV.w
                    val ndcY = projV.y / projV.w
                    val sx = (ndcX + 1f) * 0.5f * width
                    val sy = (1f - ndcY) * 0.5f * height
                    val pSize = (part.size * (15f / -viewV.z)).coerceIn(2f, 20f)
                    drawCircle(
                        color = part.color.copy(alpha = part.life / part.maxLife),
                        radius = pSize,
                        center = Offset(sx, sy)
                    )
                }
            }
        }

        // Update front-to-back list for tap hit testing
        currentRenderedPolygons = projectedList.reversed()
    }
}

// Ray-casting / winding algorithm for 2D polygon hit-testing
private fun isPointInPolygon(point: Offset, vertices: List<Offset>): Boolean {
    var intersectCount = 0
    val n = vertices.size
    for (i in 0 until n) {
        val v1 = vertices[i]
        val v2 = vertices[(i + 1) % n]
        if (((v1.y > point.y) != (v2.y > point.y)) &&
            (point.x < (v2.x - v1.x) * (point.y - v1.y) / (v2.y - v1.y) + v1.x)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 == 1
}
