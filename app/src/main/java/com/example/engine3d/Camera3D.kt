package com.example.engine3d

import kotlin.math.cos
import kotlin.math.sin

class Camera3D(
    var azimuthDeg: Float = 25f,
    var elevationDeg: Float = 30f,
    var distance: Float = 8.5f,
    var target: Vec3 = Vec3(0f, 0f, 0f),
    var fovDeg: Float = 55f,
    var minDistance: Float = 4.0f,
    var maxDistance: Float = 16.0f,
    var minElevation: Float = -70f,
    var maxElevation: Float = 80f
) {
    fun getEyePosition(): Vec3 {
        val azRad = Math.toRadians(azimuthDeg.toDouble()).toFloat()
        val elRad = Math.toRadians(elevationDeg.toDouble()).toFloat()

        val x = target.x + distance * cos(elRad) * sin(azRad)
        val y = target.y + distance * sin(elRad)
        val z = target.z + distance * cos(elRad) * cos(azRad)

        return Vec3(x, y, z)
    }

    fun getViewMatrix(): Mat4 {
        val eye = getEyePosition()
        val up = Vec3(0f, 1f, 0f)
        return Mat4.lookAt(eye, target, up)
    }

    fun getProjectionMatrix(aspectRatio: Float): Mat4 {
        return Mat4.perspective(fovDeg, aspectRatio, 0.1f, 100f)
    }

    fun onDrag(deltaX: Float, deltaY: Float) {
        azimuthDeg = (azimuthDeg - deltaX * 0.45f) % 360f
        elevationDeg = (elevationDeg + deltaY * 0.35f).coerceIn(minElevation, maxElevation)
    }

    fun onZoom(scaleFactor: Float) {
        distance = (distance / scaleFactor).coerceIn(minDistance, maxDistance)
    }

    fun reset(azimuth: Float = 25f, elevation: Float = 30f, dist: Float = 8.5f, tgt: Vec3 = Vec3(0f, 0f, 0f)) {
        azimuthDeg = azimuth
        elevationDeg = elevation
        distance = dist
        target = tgt
    }
}
