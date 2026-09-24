package com.example.engine3d

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Mesh3D {

    fun createCube(
        center: Vec3,
        size: Float,
        frontColor: Color,
        backColor: Color = frontColor,
        topColor: Color = frontColor,
        bottomColor: Color = frontColor,
        leftColor: Color = frontColor,
        rightColor: Color = frontColor,
        tagPrefix: String = "cube",
        tag: String? = null,
        outlineColor: Color? = Color(0x33FFFFFF)
    ): List<Polygon3D> {
        val h = size / 2f
        val x0 = center.x - h; val x1 = center.x + h
        val y0 = center.y - h; val y1 = center.y + h
        val z0 = center.z - h; val z1 = center.z + h

        // 8 vertices
        val v000 = Vec3(x0, y0, z0)
        val v100 = Vec3(x1, y0, z0)
        val v110 = Vec3(x1, y1, z0)
        val v010 = Vec3(x0, y1, z0)

        val v001 = Vec3(x0, y0, z1)
        val v101 = Vec3(x1, y0, z1)
        val v111 = Vec3(x1, y1, z1)
        val v011 = Vec3(x0, y1, z1)

        val fTag = tag ?: "${tagPrefix}_front"
        val bTag = tag ?: "${tagPrefix}_back"
        val tTag = tag ?: "${tagPrefix}_top"
        val botTag = tag ?: "${tagPrefix}_bottom"
        val lTag = tag ?: "${tagPrefix}_left"
        val rTag = tag ?: "${tagPrefix}_right"

        return listOf(
            // Front (+Z)
            Polygon3D(listOf(v001, v101, v111, v011), frontColor, outlineColor, tag = fTag),
            // Back (-Z)
            Polygon3D(listOf(v100, v000, v010, v110), backColor, outlineColor, tag = bTag),
            // Top (+Y)
            Polygon3D(listOf(v011, v111, v110, v010), topColor, outlineColor, tag = tTag),
            // Bottom (-Y)
            Polygon3D(listOf(v000, v100, v101, v001), bottomColor, outlineColor, tag = botTag),
            // Left (-X)
            Polygon3D(listOf(v000, v001, v011, v010), leftColor, outlineColor, tag = lTag),
            // Right (+X)
            Polygon3D(listOf(v101, v100, v110, v111), rightColor, outlineColor, tag = rTag)
        )
    }

    fun createBox(
        center: Vec3,
        width: Float,
        height: Float,
        depth: Float,
        color: Color,
        tag: String? = null,
        outlineColor: Color? = Color(0x33FFFFFF)
    ): List<Polygon3D> {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = center.x - hw; val x1 = center.x + hw
        val y0 = center.y - hh; val y1 = center.y + hh
        val z0 = center.z - hd; val z1 = center.z + hd

        val v000 = Vec3(x0, y0, z0)
        val v100 = Vec3(x1, y0, z0)
        val v110 = Vec3(x1, y1, z0)
        val v010 = Vec3(x0, y1, z0)

        val v001 = Vec3(x0, y0, z1)
        val v101 = Vec3(x1, y0, z1)
        val v111 = Vec3(x1, y1, z1)
        val v011 = Vec3(x0, y1, z1)

        return listOf(
            Polygon3D(listOf(v001, v101, v111, v011), color, outlineColor, tag = tag),
            Polygon3D(listOf(v100, v000, v010, v110), color, outlineColor, tag = tag),
            Polygon3D(listOf(v011, v111, v110, v010), color, outlineColor, tag = tag),
            Polygon3D(listOf(v000, v100, v101, v001), color, outlineColor, tag = tag),
            Polygon3D(listOf(v000, v001, v011, v010), color, outlineColor, tag = tag),
            Polygon3D(listOf(v101, v100, v110, v111), color, outlineColor, tag = tag)
        )
    }

    fun createCylinder(
        center: Vec3,
        radius: Float,
        height: Float,
        segments: Int = 8,
        color: Color,
        tag: String? = null,
        outlineColor: Color? = null
    ): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()
        val y0 = center.y - height / 2f
        val y1 = center.y + height / 2f

        val bottomVerts = mutableListOf<Vec3>()
        val topVerts = mutableListOf<Vec3>()

        for (i in 0 until segments) {
            val angle = (2.0 * PI * i / segments).toFloat()
            val vx = center.x + radius * cos(angle)
            val vz = center.z + radius * sin(angle)
            bottomVerts.add(Vec3(vx, y0, vz))
            topVerts.add(Vec3(vx, y1, vz))
        }

        // Side faces
        for (i in 0 until segments) {
            val next = (i + 1) % segments
            val b1 = bottomVerts[i]
            val b2 = bottomVerts[next]
            val t2 = topVerts[next]
            val t1 = topVerts[i]
            polys.add(Polygon3D(listOf(b1, b2, t2, t1), color, outlineColor, tag = tag))
        }

        // Top cap
        polys.add(Polygon3D(topVerts, color, outlineColor, tag = tag))
        // Bottom cap
        polys.add(Polygon3D(bottomVerts.reversed(), color, outlineColor, tag = tag))

        return polys
    }

    fun createPyramid(
        center: Vec3,
        baseSize: Float,
        height: Float,
        color: Color,
        tag: String? = null,
        outlineColor: Color? = Color(0x33FFFFFF)
    ): List<Polygon3D> {
        val h = baseSize / 2f
        val y0 = center.y - height / 2f
        val apex = Vec3(center.x, center.y + height / 2f, center.z)

        val v0 = Vec3(center.x - h, y0, center.z - h)
        val v1 = Vec3(center.x + h, y0, center.z - h)
        val v2 = Vec3(center.x + h, y0, center.z + h)
        val v3 = Vec3(center.x - h, y0, center.z + h)

        return listOf(
            Polygon3D(listOf(v0, v1, v2, v3), color, outlineColor, tag = tag),
            Polygon3D(listOf(v0, apex, v1), color, outlineColor, tag = tag),
            Polygon3D(listOf(v1, apex, v2), color, outlineColor, tag = tag),
            Polygon3D(listOf(v2, apex, v3), color, outlineColor, tag = tag),
            Polygon3D(listOf(v3, apex, v0), color, outlineColor, tag = tag)
        )
    }

    fun createWedge(
        center: Vec3,
        width: Float,
        height: Float,
        depth: Float,
        color: Color,
        tag: String? = null,
        outlineColor: Color? = Color(0x33FFFFFF)
    ): List<Polygon3D> {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = center.x - hw; val x1 = center.x + hw
        val y0 = center.y - hh; val y1 = center.y + hh
        val z0 = center.z - hd; val z1 = center.z + hd

        val v000 = Vec3(x0, y0, z0)
        val v100 = Vec3(x1, y0, z0)
        val v101 = Vec3(x1, y0, z1)
        val v001 = Vec3(x0, y0, z1)

        val v010 = Vec3(x0, y1, z0)
        val v110 = Vec3(x1, y1, z0)

        return listOf(
            // Bottom
            Polygon3D(listOf(v000, v100, v101, v001), color, outlineColor, tag = tag),
            // Back
            Polygon3D(listOf(v100, v000, v010, v110), color, outlineColor, tag = tag),
            // Slanted Ramp
            Polygon3D(listOf(v001, v101, v110, v010), color, outlineColor, tag = tag),
            // Left triangle
            Polygon3D(listOf(v000, v001, v010), color, outlineColor, tag = tag),
            // Right triangle
            Polygon3D(listOf(v101, v100, v110), color, outlineColor, tag = tag)
        )
    }

    /**
     * Builds the Brain Lab 3D environmental room:
     * - Sci-fi hexagonal / tiled grid floor with cyber lines
     * - 4 corner holographic pillars
     * - Back lab portal frame
     * - Central pedestal
     */
    fun createBrainLabRoom(): List<Polygon3D> {
        val polys = mutableListOf<Polygon3D>()

        val floorY = -1.8f
        val floorColor = Color(0xFF0F1829)
        val gridOutline = Color(0xFF1E3A5F)

        // Grid tiles (5x5 tiles)
        val tileSize = 2.4f
        for (i in -2..2) {
            for (j in -2..2) {
                val cx = i * tileSize
                val cz = j * tileSize
                val tileColor = if ((i + j) % 2 == 0) Color(0xFF111C32) else Color(0xFF0D1527)
                polys.add(
                    Polygon3D(
                        vertices = listOf(
                            Vec3(cx - tileSize / 2f, floorY, cz - tileSize / 2f),
                            Vec3(cx + tileSize / 2f, floorY, cz - tileSize / 2f),
                            Vec3(cx + tileSize / 2f, floorY, cz + tileSize / 2f),
                            Vec3(cx - tileSize / 2f, floorY, cz + tileSize / 2f)
                        ),
                        baseColor = tileColor,
                        outlineColor = gridOutline,
                        tag = "room_floor"
                    )
                )
            }
        }

        // Central glowing ring / pedestal
        polys.addAll(
            createCylinder(
                center = Vec3(0f, floorY + 0.12f, 0f),
                radius = 2.8f,
                height = 0.24f,
                segments = 12,
                color = Color(0xFF162544),
                tag = "pedestal_base",
                outlineColor = Color(0xFF00E5FF)
            )
        )
        polys.addAll(
            createCylinder(
                center = Vec3(0f, floorY + 0.28f, 0f),
                radius = 2.4f,
                height = 0.12f,
                segments = 12,
                color = Color(0xFF1B2F56),
                tag = "pedestal_inner",
                outlineColor = Color(0xFF38EF7D)
            )
        )

        // 4 Sci-Fi Pillars in corners
        val pillarOffsets = listOf(
            Vec3(-4.8f, floorY + 2.2f, -4.8f),
            Vec3(4.8f, floorY + 2.2f, -4.8f),
            Vec3(-4.8f, floorY + 2.2f, 4.8f),
            Vec3(4.8f, floorY + 2.2f, 4.8f)
        )
        for ((idx, p) in pillarOffsets.withIndex()) {
            polys.addAll(
                createCylinder(
                    center = p,
                    radius = 0.45f,
                    height = 4.4f,
                    segments = 6,
                    color = Color(0xFF142038),
                    tag = "pillar_$idx",
                    outlineColor = Color(0xFF00E5FF)
                )
            )
            // Pillar cap
            polys.addAll(
                createBox(
                    center = Vec3(p.x, p.y + 2.25f, p.z),
                    width = 1.1f,
                    height = 0.3f,
                    depth = 1.1f,
                    color = Color(0xFF1F335C),
                    outlineColor = Color(0xFF00E5FF)
                )
            )
        }

        // Back Wall Portal Frame
        val wallZ = -5.8f
        polys.addAll(
            createBox(
                center = Vec3(0f, floorY + 2.5f, wallZ),
                width = 7.0f,
                height = 0.4f,
                depth = 0.4f,
                color = Color(0xFF1A2A4A),
                outlineColor = Color(0xFF7F00FF)
            )
        )
        polys.addAll(
            createBox(
                center = Vec3(-3.5f, floorY + 1.25f, wallZ),
                width = 0.4f,
                height = 2.5f,
                depth = 0.4f,
                color = Color(0xFF1A2A4A),
                outlineColor = Color(0xFF7F00FF)
            )
        )
        polys.addAll(
            createBox(
                center = Vec3(3.5f, floorY + 1.25f, wallZ),
                width = 0.4f,
                height = 2.5f,
                depth = 0.4f,
                color = Color(0xFF1A2A4A),
                outlineColor = Color(0xFF7F00FF)
            )
        )

        return polys
    }
}
