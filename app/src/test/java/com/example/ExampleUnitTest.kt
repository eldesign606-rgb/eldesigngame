package com.example

import com.example.data.LevelCatalog
import com.example.data.PuzzleCategory
import com.example.data.PuzzleType
import com.example.engine3d.Mat4
import com.example.engine3d.Vec3
import com.example.puzzle.MazePuzzleController
import com.example.puzzle.PatternPuzzleController
import com.example.puzzle.PuzzleEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testLevelCatalogCountAndProperties() {
        val levels = LevelCatalog.levels
        assertEquals(30, levels.size)

        // Verify IDs 1 to 30
        for (i in 1..30) {
            val lvl = LevelCatalog.getLevelById(i)
            assertEquals(i, lvl.id)
            assertTrue(lvl.title.isNotEmpty())
            assertTrue(lvl.objective.isNotEmpty())
            assertTrue(lvl.timeLimitSeconds >= 60)
            assertTrue(lvl.rewardXP > 0)
            assertTrue(lvl.rewardCoin > 0)
        }

        // Verify categories
        val warmupCount = levels.count { it.category == PuzzleCategory.WARMUP }
        val logicCount = levels.count { it.category == PuzzleCategory.LOGIC }
        val memoryCount = levels.count { it.category == PuzzleCategory.MEMORY }
        val spatialCount = levels.count { it.category == PuzzleCategory.SPATIAL }
        val problemCount = levels.count { it.category == PuzzleCategory.PROBLEM_SOLVING }
        val masterCount = levels.count { it.category == PuzzleCategory.MASTER }

        assertEquals(5, warmupCount)
        assertEquals(5, logicCount)
        assertEquals(5, memoryCount)
        assertEquals(5, spatialCount)
        assertEquals(5, problemCount)
        assertEquals(5, masterCount)
    }

    @Test
    fun testDailyLevelGenerator() {
        val date1 = "2026-09-24"
        val daily1 = LevelCatalog.getDailyLevel(date1)
        assertNotNull(daily1)
        assertTrue(daily1.title.startsWith("Tantangan Harian:"))
    }

    @Test
    fun test3DVectorMath() {
        val v1 = Vec3(1f, 0f, 0f)
        val v2 = Vec3(0f, 1f, 0f)
        val cross = v1.cross(v2)
        assertEquals(0f, cross.x, 0.001f)
        assertEquals(0f, cross.y, 0.001f)
        assertEquals(1f, cross.z, 0.001f)

        val v3 = Vec3(3f, 4f, 0f)
        assertEquals(5f, v3.length(), 0.001f)
        val norm = v3.normalize()
        assertEquals(0.6f, norm.x, 0.001f)
        assertEquals(0.8f, norm.y, 0.001f)
    }

    @Test
    fun test3DMatrixTransformation() {
        val mat = Mat4.translation(2f, 3f, 4f)
        val point = Vec3(1f, 1f, 1f)
        val transformed = mat.transform(point)
        assertEquals(3f, transformed.x, 0.001f)
        assertEquals(4f, transformed.y, 0.001f)
        assertEquals(5f, transformed.z, 0.001f)
    }

    @Test
    fun testPatternPuzzleLogic() {
        val lvl1 = LevelCatalog.getLevelById(1)
        val controller = PatternPuzzleController(lvl1)
        var solved = false

        // Correct tap
        controller.handleTap("pattern_opt_${controller.correctIndex}") { event ->
            if (event is PuzzleEvent.Solved) solved = true
        }
        assertTrue(solved)
        assertEquals(1, controller.getStepCount())
        assertEquals(0, controller.getMistakeCount())
    }

    @Test
    fun testMazePuzzleNavigation() {
        val lvl4 = LevelCatalog.getLevelById(4)
        val controller = MazePuzzleController(lvl4)
        val startPos = controller.playerPos
        assertEquals(0, startPos.r)
        assertEquals(0, startPos.c)
    }
}
