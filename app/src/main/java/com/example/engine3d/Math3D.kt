package com.example.engine3d

import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

data class Vec3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(s: Float) = Vec3(x * s, y * s, z * s)
    operator fun div(s: Float) = Vec3(x / s, y / s, z / s)
    operator fun unaryMinus() = Vec3(-x, -y, -z)

    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun lengthSq(): Float = x * x + y * y + z * z

    fun normalize(): Vec3 {
        val l = length()
        return if (l > 1e-6f) this / l else Vec3(0f, 0f, 0f)
    }

    fun dot(other: Vec3): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vec3): Vec3 = Vec3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )
}

data class Vec4(val x: Float, val y: Float, val z: Float, val w: Float = 1f)

data class Mat4(val m: FloatArray) {
    init {
        require(m.size == 16)
    }

    operator fun times(o: Mat4): Mat4 {
        val r = FloatArray(16)
        for (i in 0..3) {
            for (j in 0..3) {
                var sum = 0f
                for (k in 0..3) {
                    sum += this.m[i * 4 + k] * o.m[k * 4 + j]
                }
                r[i * 4 + j] = sum
            }
        }
        return Mat4(r)
    }

    fun transform(v: Vec3): Vec3 {
        val x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3]
        val y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7]
        val z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11]
        val w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15]
        return if (kotlin.math.abs(w) > 1e-6f) Vec3(x / w, y / w, z / w) else Vec3(x, y, z)
    }

    fun transformVec4(v: Vec3): Vec4 {
        val x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3]
        val y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7]
        val z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11]
        val w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15]
        return Vec4(x, y, z, w)
    }

    companion object {
        fun identity(): Mat4 {
            return Mat4(
                floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun translation(tx: Float, ty: Float, tz: Float): Mat4 {
            return Mat4(
                floatArrayOf(
                    1f, 0f, 0f, tx,
                    0f, 1f, 0f, ty,
                    0f, 0f, 1f, tz,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun scale(sx: Float, sy: Float, sz: Float): Mat4 {
            return Mat4(
                floatArrayOf(
                    sx, 0f, 0f, 0f,
                    0f, sy, 0f, 0f,
                    0f, 0f, sz, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun rotationX(degrees: Float): Mat4 {
            val rad = Math.toRadians(degrees.toDouble()).toFloat()
            val c = cos(rad)
            val s = sin(rad)
            return Mat4(
                floatArrayOf(
                    1f, 0f, 0f, 0f,
                    0f, c, -s, 0f,
                    0f, s, c, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun rotationY(degrees: Float): Mat4 {
            val rad = Math.toRadians(degrees.toDouble()).toFloat()
            val c = cos(rad)
            val s = sin(rad)
            return Mat4(
                floatArrayOf(
                    c, 0f, s, 0f,
                    0f, 1f, 0f, 0f,
                    -s, 0f, c, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun rotationZ(degrees: Float): Mat4 {
            val rad = Math.toRadians(degrees.toDouble()).toFloat()
            val c = cos(rad)
            val s = sin(rad)
            return Mat4(
                floatArrayOf(
                    c, -s, 0f, 0f,
                    s, c, 0f, 0f,
                    0f, 0f, 1f, 0f,
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun lookAt(eye: Vec3, target: Vec3, up: Vec3): Mat4 {
            val f = (target - eye).normalize()
            val s = f.cross(up).normalize()
            val u = s.cross(f)

            return Mat4(
                floatArrayOf(
                    s.x, s.y, s.z, -s.dot(eye),
                    u.x, u.y, u.z, -u.dot(eye),
                    -f.x, -f.y, -f.z, f.dot(eye),
                    0f, 0f, 0f, 1f
                )
            )
        }

        fun perspective(fovYDeg: Float, aspect: Float, near: Float, far: Float): Mat4 {
            val fovRad = Math.toRadians(fovYDeg.toDouble()).toFloat()
            val tanHalfFov = tan(fovRad / 2f)
            val m00 = 1f / (aspect * tanHalfFov)
            val m11 = 1f / tanHalfFov
            val m22 = -(far + near) / (far - near)
            val m23 = -(2f * far * near) / (far - near)
            val m32 = -1f

            return Mat4(
                floatArrayOf(
                    m00, 0f, 0f, 0f,
                    0f, m11, 0f, 0f,
                    0f, 0f, m22, m23,
                    0f, 0f, m32, 0f
                )
            )
        }
    }
}

data class Polygon3D(
    val vertices: List<Vec3>,
    val baseColor: Color,
    val outlineColor: Color? = null,
    val tag: String? = null,
    val isEmissive: Boolean = false,
    val doubleSided: Boolean = false
) {
    fun normal(): Vec3 {
        if (vertices.size < 3) return Vec3(0f, 1f, 0f)
        val v0 = vertices[0]
        val v1 = vertices[1]
        val v2 = vertices[2]
        return (v1 - v0).cross(v2 - v0).normalize()
    }

    fun center(): Vec3 {
        var sx = 0f
        var sy = 0f
        var sz = 0f
        for (v in vertices) {
            sx += v.x
            sy += v.y
            sz += v.z
        }
        val n = vertices.size.toFloat()
        return Vec3(sx / n, sy / n, sz / n)
    }
}

data class Particle3D(
    var pos: Vec3,
    var vel: Vec3,
    val color: Color,
    var life: Float = 1f,
    val maxLife: Float = 1f,
    val size: Float = 4f
)
