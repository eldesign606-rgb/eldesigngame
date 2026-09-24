package com.example.puzzle

import androidx.compose.ui.graphics.Color
import com.example.data.LevelDefinition
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3

sealed class PuzzleEvent {
    data class CorrectMove(val message: String = "Benar!") : PuzzleEvent()
    data class WrongMove(val message: String = "Salah! Kehilangan 1 nyawa.") : PuzzleEvent()
    object Solved : PuzzleEvent()
    data class StepTaken(val stepCount: Int) : PuzzleEvent()
}

interface PuzzleController {
    val levelDef: LevelDefinition
    fun getPolygons(timeSec: Float): List<Polygon3D>
    fun handleTap(tag: String, onEvent: (PuzzleEvent) -> Unit)
    fun getStepCount(): Int
    fun getMistakeCount(): Int
    fun applyHint(tier: Int)
}
