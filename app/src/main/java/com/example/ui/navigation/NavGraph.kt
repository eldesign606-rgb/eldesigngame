package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.AchievementRecord
import com.example.data.GameRepository
import com.example.data.LevelRecord
import com.example.data.PlayerProfile
import com.example.game.AudioSystem
import com.example.game.GameManager
import com.example.ui.screens.AchievementScreen
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MissionScreen
import com.example.ui.screens.SettingScreen
import com.example.ui.screens.StatisticScreen
import kotlinx.coroutines.launch

object Destinations {
    const val HOME = "home"
    const val LEVEL_SELECT = "level_select"
    const val GAME = "game/{levelId}/{isDaily}"
    const val DAILY = "daily_challenge"
    const val ACHIEVEMENTS = "achievements"
    const val STATISTICS = "statistics"
    const val MISSIONS = "missions"
    const val SETTINGS = "settings"

    fun gameRoute(levelId: Int, isDaily: Boolean = false): String {
        return "game/$levelId/$isDaily"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: GameRepository,
    audioSystem: AudioSystem,
    gameManager: GameManager
) {
    val scope = rememberCoroutineScope()
    val profile by repository.playerProfile.collectAsState(initial = PlayerProfile())
    val levelRecords by repository.levelRecords.collectAsState(initial = emptyList())
    val achievements by repository.achievements.collectAsState(initial = emptyList())
    val today = GameRepository.getTodayDateString()
    val dailyRecord by repository.getDailyRecord(today).collectAsState(initial = null)

    val currentProfile = profile ?: PlayerProfile()

    NavHost(
        navController = navController,
        startDestination = Destinations.HOME
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                profile = currentProfile,
                onStartPlay = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.LEVEL_SELECT)
                },
                onDailyChallenge = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.DAILY)
                },
                onAchievements = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.ACHIEVEMENTS)
                },
                onStatistics = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.STATISTICS)
                },
                onMissions = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.MISSIONS)
                },
                onSettings = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.SETTINGS)
                }
            )
        }

        composable(Destinations.LEVEL_SELECT) {
            LevelSelectScreen(
                profile = currentProfile,
                levelRecords = levelRecords,
                onSelectLevel = { levelId ->
                    audioSystem.playClick()
                    gameManager.startLevel(levelId, isDaily = false)
                    navController.navigate(Destinations.gameRoute(levelId, isDaily = false))
                },
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destinations.GAME,
            arguments = listOf(
                navArgument("levelId") { type = NavType.IntType },
                navArgument("isDaily") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            val isDaily = backStackEntry.arguments?.getBoolean("isDaily") ?: false

            GameScreen(
                gameManager = gameManager,
                profile = currentProfile,
                onNextLevel = {
                    audioSystem.playClick()
                    val nextId = levelId + 1
                    if (nextId <= 30) {
                        gameManager.startLevel(nextId, isDaily = false)
                        navController.navigate(Destinations.gameRoute(nextId, isDaily = false)) {
                            popUpTo(Destinations.LEVEL_SELECT)
                        }
                    } else {
                        navController.popBackStack(Destinations.HOME, inclusive = false)
                    }
                },
                onExitToMenu = {
                    audioSystem.playClick()
                    navController.popBackStack(Destinations.HOME, inclusive = false)
                },
                onOpenSettings = {
                    audioSystem.playClick()
                    navController.navigate(Destinations.SETTINGS)
                }
            )
        }

        composable(Destinations.DAILY) {
            DailyChallengeScreen(
                profile = currentProfile,
                dailyRecord = dailyRecord,
                onPlayDaily = {
                    audioSystem.playClick()
                    gameManager.startLevel(1, isDaily = true)
                    navController.navigate(Destinations.gameRoute(1, isDaily = true))
                },
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.ACHIEVEMENTS) {
            AchievementScreen(
                achievements = achievements,
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.STATISTICS) {
            StatisticScreen(
                profile = currentProfile,
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.MISSIONS) {
            MissionScreen(
                profile = currentProfile,
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.SETTINGS) {
            SettingScreen(
                profile = currentProfile,
                onUpdateSettings = { sound, music, haptic ->
                    audioSystem.soundEnabled = sound
                    audioSystem.musicEnabled = music
                    scope.launch {
                        repository.updateSettings(sound, music, haptic)
                    }
                },
                onResetProgress = {
                    scope.launch {
                        repository.resetAllProgress()
                    }
                },
                onBack = {
                    audioSystem.playClick()
                    navController.popBackStack()
                }
            )
        }
    }
}
