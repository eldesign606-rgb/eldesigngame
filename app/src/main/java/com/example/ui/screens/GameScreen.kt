package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PlayerProfile
import com.example.data.PuzzleType
import com.example.engine3d.BrainLab3DCanvas
import com.example.engine3d.Camera3D
import com.example.engine3d.Particle3D
import com.example.engine3d.Vec3
import com.example.game.GameManager
import com.example.game.GameStatus
import com.example.puzzle.GravityPuzzleController
import com.example.puzzle.MazePuzzleController
import com.example.puzzle.PuzzleEvent
import com.example.puzzle.RotateCubeController
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import java.util.Locale

@Composable
fun GameScreen(
    gameManager: GameManager,
    profile: PlayerProfile,
    onNextLevel: () -> Unit,
    onExitToMenu: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "game_render_loop")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_time"
    )

    val camera = remember {
        Camera3D(
            azimuthDeg = 25f,
            elevationDeg = 32f,
            distance = 8.5f,
            target = Vec3(0f, 0f, 0f)
        )
    }

    // Reset camera position on level change
    LaunchedEffect(gameManager.currentLevelDef.id) {
        camera.reset(azimuth = 25f, elevation = 32f, dist = 8.5f)
    }

    var showHintDialog by remember { mutableStateOf(false) }
    var hintResultText by remember { mutableStateOf<String?>(null) }
    var showTutorialDialog by remember { mutableStateOf(gameManager.currentLevelDef.id == 1 && !profile.tutorialCompleted) }

    // Obtain polygons for current frame
    val polygons = remember(animTime, gameManager.currentPuzzle) {
        gameManager.currentPuzzle?.getPolygons(animTime) ?: emptyList()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070B14))) {
        // 3D Canvas
        BrainLab3DCanvas(
            camera = camera,
            polygons = polygons,
            allowCameraControl = (gameManager.status == GameStatus.PLAYING),
            onObjectTapped = { tag ->
                gameManager.handleObjectTap(tag)
            }
        )

        // HUD Overlay (Top & Bottom)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top HUD Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lives (Hearts)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xCC0D1527))
                            .border(1.dp, Color(0xFF1B2945), RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= gameManager.lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Nyawa $i",
                                tint = if (i <= gameManager.lives) NeonCoral else Color.DarkGray,
                                modifier = Modifier
                                    .size(22.dp)
                                    .padding(horizontal = 2.dp)
                            )
                        }
                    }

                    // Level Title Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xDD0D1527))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Level ${gameManager.currentLevelDef.id}: ${gameManager.currentLevelDef.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    // Timer Display
                    val minutes = gameManager.timeRemainingSeconds / 60
                    val seconds = gameManager.timeRemainingSeconds % 60
                    val timerStr = String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    val isUrgent = gameManager.timeRemainingSeconds < 20

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isUrgent) Color(0xDD44111D) else Color(0xCC0D1527))
                            .border(1.dp, if (isUrgent) NeonCoral else Color(0xFF1B2945), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱ $timerStr",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isUrgent) NeonCoral else NeonGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Objective Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xB3101A30))
                        .border(1.dp, Color(0xFF1E3255), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🎯 ${gameManager.currentLevelDef.objective}",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Status message feedback
                AnimatedVisibility(
                    visible = gameManager.lastStatusMessage.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xE600E5FF))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = gameManager.lastStatusMessage,
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bottom Controls Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Puzzle Specific Controls (e.g. Maze D-Pad, Rotate Cube Arrows, Gravity Launch)
                when (val p = gameManager.currentPuzzle) {
                    is MazePuzzleController -> {
                        MazeControls(
                            onMove = { dr, dc ->
                                p.move(dr, dc) { event ->
                                    handlePuzzleEvent(event, gameManager)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    is RotateCubeController -> {
                        RotateControls(
                            onRotate = { dx, dy ->
                                p.rotate(dx, dy) { event ->
                                    handlePuzzleEvent(event, gameManager)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    is GravityPuzzleController -> {
                        Button(
                            onClick = {
                                p.launchBall { event ->
                                    handlePuzzleEvent(event, gameManager)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("launch_ball_button")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = "Luncurkan", tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LUNCURKAN BOLA", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    else -> {}
                }

                // Standard Action Bar (Hint and Pause)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hint Button
                    Button(
                        onClick = { showHintDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1A2B4C)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .border(1.dp, NeonGold, RoundedCornerShape(14.dp))
                            .testTag("hint_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = NeonGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "💡 Hint (${profile.coins}🪙)",
                            color = NeonGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Pause Button
                    Button(
                        onClick = { gameManager.pause() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1A2B4C)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .border(1.dp, NeonCyan, RoundedCornerShape(14.dp))
                            .testTag("pause_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pause",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // PAUSE DIALOG
        if (gameManager.status == GameStatus.PAUSED) {
            Dialog(onDismissRequest = { gameManager.resume() }) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.5.dp, NeonCyan, RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PAUSED",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { gameManager.resume() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("pause_resume_btn")
                        ) {
                            Text("LANJUTKAN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { gameManager.restart() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("pause_restart_btn")
                        ) {
                            Text("ULANG LEVEL", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onOpenSettings,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("pause_settings_btn")
                        ) {
                            Text("PENGATURAN", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onExitToMenu,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("pause_exit_btn")
                        ) {
                            Text("KELUAR", color = NeonCoral)
                        }
                    }
                }
            }
        }

        // HINT MODAL DIALOG
        if (showHintDialog) {
            Dialog(onDismissRequest = { showHintDialog = false; hintResultText = null }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, NeonGold, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = NeonGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Petunjuk Laboratorium", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            IconButton(onClick = { showHintDialog = false; hintResultText = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (hintResultText != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1B2D4F))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = hintResultText ?: "",
                                    color = NeonGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            HintOptionRow("💡 Hint 1: Petunjuk Ringan", "5 Koin") {
                                gameManager.requestHint(1) { _, msg -> hintResultText = msg }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            HintOptionRow("💡 Hint 2: Eliminasi / Sorot", "10 Koin") {
                                gameManager.requestHint(2) { _, msg -> hintResultText = msg }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            HintOptionRow("💡 Hint 3: Petunjuk Kunci Jelas", "20 Koin") {
                                gameManager.requestHint(3) { _, msg -> hintResultText = msg }
                            }
                        }
                    }
                }
            }
        }

        // LEVEL COMPLETE DIALOG
        if (gameManager.status == GameStatus.LEVEL_COMPLETE) {
            val score = gameManager.scoreResult
            Dialog(onDismissRequest = { /* forced action */ }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .border(2.dp, NeonEmerald, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 42.sp
                        )
                        Text(
                            text = "LEVEL SELESAI!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonEmerald
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Stars
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = if (s <= (score?.stars ?: 1)) NeonGold else Color(0xFF2C394E),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score breakdown card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF101A2F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                ScoreRow("Skor Dasar", "+${score?.baseScore ?: 1000}")
                                ScoreRow("Bonus Waktu", "+${score?.timeBonus ?: 0}")
                                ScoreRow("Bonus Nyawa", "+${score?.accuracyBonus ?: 0}")
                                ScoreRow("Penalti Petunjuk", "-${score?.hintPenalty ?: 0}", isPenalty = true)
                                ScoreRow("Penalti Kesalahan", "-${score?.mistakePenalty ?: 0}", isPenalty = true)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("TOTAL SKOR", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("${score?.totalScore ?: 0}", fontWeight = FontWeight.ExtraBold, color = NeonCyan, fontSize = 16.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Rewards Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("+${gameManager.currentLevelDef.rewardXP} XP", color = NeonCyan, fontWeight = FontWeight.Bold)
                            Text("+${gameManager.currentLevelDef.rewardCoin} Koin 🪙", color = NeonGold, fontWeight = FontWeight.Bold)
                            val m = gameManager.elapsedSeconds / 60
                            val sec = gameManager.elapsedSeconds % 60
                            Text(String.format(Locale.US, "Waktu: %02d:%02d", m, sec), color = Color.LightGray)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Button(
                            onClick = onNextLevel,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("next_level_button")
                        ) {
                            Text("LEVEL BERIKUTNYA ▶", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { gameManager.restart() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("replay_button")
                            ) {
                                Text("Ulangi", color = Color.White)
                            }
                            OutlinedButton(
                                onClick = onExitToMenu,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("complete_menu_button")
                            ) {
                                Text("Menu", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // GAME OVER DIALOG
        if (gameManager.status == GameStatus.GAME_OVER) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .border(2.dp, NeonCoral, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💀",
                            fontSize = 38.sp
                        )
                        Text(
                            text = "GAME OVER",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            ),
                            color = NeonCoral
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"Setiap kesalahan adalah petunjuk untuk mencoba strategi baru.\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { gameManager.restart() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCoral),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("game_over_retry_button")
                        ) {
                            Text("🔄 COBA LAGI", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showHintDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("game_over_hint_button")
                        ) {
                            Text("💡 LIHAT PETUNJUK", color = NeonGold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onExitToMenu,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("game_over_menu_button")
                        ) {
                            Text("🏠 MENU UTAMA", color = Color.White)
                        }
                    }
                }
            }
        }

        // TUTORIAL DIALOG FOR LEVEL 1
        if (showTutorialDialog) {
            Dialog(onDismissRequest = { showTutorialDialog = false }) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.5.dp, NeonCyan, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Selamat Datang di Brain Lab 3D! 🧠",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Gunakan sentuhan geser (drag) untuk memutar sudut pandang ruang 3D.\n• Cubit (pinch) untuk memperbesar atau memperkecil pandangan.\n• Tap langsung objek 3D di ruangan untuk berinteraksi.\n• Selesaikan puzzle sebelum batas waktu habis!",
                            fontSize = 13.sp,
                            color = Color(0xFFCCE4FF),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { showTutorialDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SAYA MENGERTI", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun handlePuzzleEvent(event: PuzzleEvent, gm: GameManager) {
    when (event) {
        is PuzzleEvent.CorrectMove -> gm.audio.playCorrect()
        is PuzzleEvent.WrongMove -> {
            gm.audio.playWrong()
            gm.lastStatusMessage = event.message
        }
        is PuzzleEvent.StepTaken -> gm.audio.playRotate()
        PuzzleEvent.Solved -> gm.audio.playLevelComplete()
    }
}

@Composable
fun ScoreRow(label: String, value: String, isPenalty: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.LightGray)
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPenalty) NeonCoral else NeonEmerald
        )
    }
}

@Composable
fun HintOptionRow(title: String, cost: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF182846)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(cost, color = NeonGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MazeControls(onMove: (Int, Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        IconButton(
            onClick = { onMove(-1, 0) },
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xCC1A2B4C))
                .border(1.dp, NeonCyan, CircleShape)
        ) {
            Icon(Icons.Default.ArrowUpward, contentDescription = "Atas", tint = NeonCyan)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            IconButton(
                onClick = { onMove(0, -1) },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC1A2B4C))
                    .border(1.dp, NeonCyan, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kiri", tint = NeonCyan)
            }
            IconButton(
                onClick = { onMove(0, 1) },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC1A2B4C))
                    .border(1.dp, NeonCyan, CircleShape)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Kanan", tint = NeonCyan)
            }
        }
        IconButton(
            onClick = { onMove(1, 0) },
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xCC1A2B4C))
                .border(1.dp, NeonCyan, CircleShape)
        ) {
            Icon(Icons.Default.ArrowDownward, contentDescription = "Bawah", tint = NeonCyan)
        }
    }
}

@Composable
fun RotateControls(onRotate: (Float, Float) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Button(
            onClick = { onRotate(0f, -90f) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1A2B4C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
        ) {
            Text("↺ Putar Kiri", color = NeonCyan)
        }
        Button(
            onClick = { onRotate(90f, 0f) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1A2B4C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
        ) {
            Text("⇅ Putar Atas", color = NeonPurple)
        }
        Button(
            onClick = { onRotate(0f, 90f) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1A2B4C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
        ) {
            Text("Putar Kanan ↻", color = NeonCyan)
        }
    }
}
