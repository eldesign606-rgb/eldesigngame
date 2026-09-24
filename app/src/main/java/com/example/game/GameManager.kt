package com.example.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.GameRepository
import com.example.data.LevelCatalog
import com.example.data.LevelDefinition
import com.example.puzzle.PuzzleController
import com.example.puzzle.PuzzleEvent
import com.example.puzzle.PuzzleFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max

enum class GameStatus {
    PLAYING,
    PAUSED,
    LEVEL_COMPLETE,
    GAME_OVER
}

data class ScoreBreakdown(
    val baseScore: Int,
    val timeBonus: Int,
    val accuracyBonus: Int,
    val hintPenalty: Int,
    val mistakePenalty: Int,
    val totalScore: Int,
    val stars: Int
)

class GameManager(
    val repository: GameRepository,
    val audio: AudioSystem,
    private val scope: CoroutineScope
) {
    var status by mutableStateOf(GameStatus.PLAYING)
    var currentLevelDef by mutableStateOf<LevelDefinition>(LevelCatalog.levels.first())
    var currentPuzzle by mutableStateOf<PuzzleController?>(null)

    var lives by mutableIntStateOf(3)
    var timeRemainingSeconds by mutableIntStateOf(90)
    var elapsedSeconds by mutableIntStateOf(0)
    var hintsUsedCount by mutableIntStateOf(0)
    var isDailyChallenge by mutableStateOf(false)
    var lastStatusMessage by mutableStateOf("")

    var scoreResult by mutableStateOf<ScoreBreakdown?>(null)

    private var timerJob: Job? = null

    fun startLevel(levelId: Int, isDaily: Boolean = false) {
        val def = if (isDaily) {
            LevelCatalog.getDailyLevel(GameRepository.getTodayDateString())
        } else {
            LevelCatalog.getLevelById(levelId)
        }

        isDailyChallenge = isDaily
        currentLevelDef = def
        currentPuzzle = PuzzleFactory.create(def)
        lives = 3
        timeRemainingSeconds = def.timeLimitSeconds
        elapsedSeconds = 0
        hintsUsedCount = 0
        lastStatusMessage = ""
        scoreResult = null
        status = GameStatus.PLAYING

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (isActive && status == GameStatus.PLAYING) {
                delay(1000L)
                if (status == GameStatus.PLAYING) {
                    elapsedSeconds++
                    if (timeRemainingSeconds > 0) {
                        timeRemainingSeconds--
                        if (timeRemainingSeconds <= 0) {
                            onTimeExpired()
                        }
                    }
                }
            }
        }
    }

    fun pause() {
        if (status == GameStatus.PLAYING) {
            status = GameStatus.PAUSED
        }
    }

    fun resume() {
        if (status == GameStatus.PAUSED) {
            status = GameStatus.PLAYING
        }
    }

    fun restart() {
        startLevel(currentLevelDef.id, isDailyChallenge)
    }

    fun handleObjectTap(tag: String) {
        if (status != GameStatus.PLAYING) return
        audio.playClick()
        currentPuzzle?.handleTap(tag) { event ->
            when (event) {
                is PuzzleEvent.CorrectMove -> {
                    lastStatusMessage = event.message
                    audio.playCorrect()
                }
                is PuzzleEvent.WrongMove -> {
                    lastStatusMessage = event.message
                    audio.playWrong()
                    deductLife()
                }
                is PuzzleEvent.StepTaken -> {
                    // Audio step / rotate feedback
                    audio.playRotate()
                }
                PuzzleEvent.Solved -> {
                    onLevelComplete()
                }
            }
        }
    }

    private fun deductLife() {
        lives--
        if (lives <= 0) {
            status = GameStatus.GAME_OVER
            timerJob?.cancel()
            scope.launch {
                repository.recordGameFailed(elapsedSeconds)
            }
        }
    }

    private fun onTimeExpired() {
        status = GameStatus.GAME_OVER
        timerJob?.cancel()
        audio.playWrong()
        scope.launch {
            repository.recordGameFailed(elapsedSeconds)
        }
    }

    private fun onLevelComplete() {
        status = GameStatus.LEVEL_COMPLETE
        timerJob?.cancel()
        audio.playLevelComplete()

        // Calculate score
        val baseScore = 1000
        val timeRatio = timeRemainingSeconds.toFloat() / currentLevelDef.timeLimitSeconds.toFloat()
        val timeBonus = (timeRatio * 400).toInt()
        val accuracyBonus = lives * 100
        val hintPenalty = hintsUsedCount * 80
        val mistakePenalty = (currentPuzzle?.getMistakeCount() ?: 0) * 50
        val total = max(100, baseScore + timeBonus + accuracyBonus - hintPenalty - mistakePenalty)

        val stars = when {
            lives == 3 && elapsedSeconds <= (currentLevelDef.timeLimitSeconds * 0.65f) -> 3
            lives >= 2 -> 2
            else -> 1
        }

        scoreResult = ScoreBreakdown(
            baseScore = baseScore,
            timeBonus = timeBonus,
            accuracyBonus = accuracyBonus,
            hintPenalty = hintPenalty,
            mistakePenalty = mistakePenalty,
            totalScore = total,
            stars = stars
        )

        scope.launch {
            if (isDailyChallenge) {
                repository.recordDailyCompleted(
                    dateStr = GameRepository.getTodayDateString(),
                    stars = stars,
                    score = total,
                    elapsedSeconds = elapsedSeconds
                )
            } else {
                repository.recordLevelCompleted(
                    levelId = currentLevelDef.id,
                    stars = stars,
                    score = total,
                    elapsedSeconds = elapsedSeconds,
                    livesRemaining = lives,
                    isDaily = false
                )
            }
        }
    }

    fun requestHint(tier: Int, onResult: (Boolean, String) -> Unit) {
        val cost = when (tier) {
            1 -> 5
            2 -> 10
            else -> 20
        }
        scope.launch {
            val success = repository.deductCoins(cost)
            if (success) {
                hintsUsedCount++
                currentPuzzle?.applyHint(tier)
                audio.playCoin()
                val text = when (tier) {
                    1 -> currentLevelDef.hint1
                    2 -> currentLevelDef.hint2
                    else -> currentLevelDef.hint3
                }
                onResult(true, text)
            } else {
                onResult(false, "Koin tidak cukup! Butuh $cost Koin.")
            }
        }
    }
}
