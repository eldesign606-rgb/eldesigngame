package com.example.puzzle

import com.example.data.LevelDefinition
import com.example.data.PuzzleType

object PuzzleFactory {
    fun create(levelDef: LevelDefinition): PuzzleController {
        return when (levelDef.puzzleType) {
            PuzzleType.PATTERN_LOCK -> PatternPuzzleController(levelDef)
            PuzzleType.MAZE_3D -> MazePuzzleController(levelDef)
            PuzzleType.MEMORY_CUBE -> MemoryCubeController(levelDef)
            PuzzleType.LOGIC_DOOR -> LogicDoorController(levelDef)
            PuzzleType.BALANCE -> BalancePuzzleController(levelDef)
            PuzzleType.ROTATE_CUBE -> RotateCubeController(levelDef)
            PuzzleType.LIGHT_CIRCUIT -> CircuitPuzzleController(levelDef)
            PuzzleType.OBJECT_SEQUENCE -> ObjectSequenceController(levelDef)
            PuzzleType.HIDDEN_OBJECT -> HiddenObjectController(levelDef)
            PuzzleType.GRAVITY -> GravityPuzzleController(levelDef)
        }
    }
}
