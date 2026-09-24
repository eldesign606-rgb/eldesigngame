package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.game.AudioSystem
import com.example.game.GameManager
import com.example.ui.navigation.AppNavHost
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: GameRepository
    private lateinit var audioSystem: AudioSystem
    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(applicationContext)
        repository = GameRepository(database.gameDao())
        audioSystem = AudioSystem(lifecycleScope)
        gameManager = GameManager(repository, audioSystem, lifecycleScope)

        lifecycleScope.launch {
            repository.initializeIfEmpty()
            val profile = repository.getProfile()
            audioSystem.soundEnabled = profile.soundEnabled
            audioSystem.musicEnabled = profile.musicEnabled
        }

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        navController = navController,
                        repository = repository,
                        audioSystem = audioSystem,
                        gameManager = gameManager
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::audioSystem.isInitialized && audioSystem.musicEnabled) {
            audioSystem.startAmbientMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::audioSystem.isInitialized) {
            audioSystem.stopAmbientMusic()
        }
        if (::gameManager.isInitialized) {
            gameManager.pause()
        }
    }
}
