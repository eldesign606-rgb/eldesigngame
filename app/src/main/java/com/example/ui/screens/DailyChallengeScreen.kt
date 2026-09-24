package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.data.DailyRecord
import com.example.data.GameRepository
import com.example.data.LevelCatalog
import com.example.data.PlayerProfile
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    profile: PlayerProfile,
    dailyRecord: DailyRecord?,
    onPlayDaily: () -> Unit,
    onBack: () -> Unit
) {
    val today = GameRepository.getTodayDateString()
    val dailyLevel = LevelCatalog.getDailyLevel(today)
    val isCompleted = dailyRecord?.completed == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tantangan Harian Otak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("daily_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Streak Fire Hero Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Brush.horizontalGradient(listOf(NeonCoral, NeonGold)), RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(NeonCoral.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = NeonCoral,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "STREAK ${profile.streak} HARI",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Selesaikan puzzle setiap hari untuk mengasah ketajaman kognitif!",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Milestones
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hadiah Milestone Streak",
                        fontWeight = FontWeight.Bold,
                        color = NeonGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    MilestoneRow(day = "3 Hari", reward = "+50 Koin 🪙", reached = profile.streak >= 3)
                    MilestoneRow(day = "7 Hari", reward = "+150 Koin 🪙", reached = profile.streak >= 7)
                    MilestoneRow(day = "30 Hari", reward = "Lencana Master Otak 🎖", reached = profile.streak >= 30)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Challenge Info
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TANGGAL: $today", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (isCompleted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selesai", tint = NeonEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Selesai", color = NeonEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dailyLevel.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = dailyLevel.objective,
                        fontSize = 13.sp,
                        color = Color(0xFFCCDDF7),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hadiah XP: +${dailyLevel.rewardXP}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Hadiah Koin: +${dailyLevel.rewardCoin} 🪙", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Play Button
            Button(
                onClick = onPlayDaily,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(NeonEmerald, NeonCyan)),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("play_daily_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                        contentDescription = "Main",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCompleted) "MAIN ULANG TANTANGAN" else "MAINKAN TANTANGAN HARIAN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneRow(day: String, reward: String, reached: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (reached) Icons.Default.CheckCircle else Icons.Default.Star,
                contentDescription = null,
                tint = if (reached) NeonEmerald else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(day, fontSize = 13.sp, color = if (reached) Color.White else Color.Gray, fontWeight = FontWeight.Medium)
        }
        Text(reward, fontSize = 12.sp, color = if (reached) NeonGold else Color.Gray, fontWeight = FontWeight.SemiBold)
    }
}
