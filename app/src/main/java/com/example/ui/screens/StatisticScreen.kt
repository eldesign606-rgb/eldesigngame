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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun StatisticScreen(
    profile: PlayerProfile,
    onBack: () -> Unit
) {
    val totalPlayed = profile.totalPuzzlesPlayed
    val totalSolved = profile.totalPuzzlesSolved
    val accuracy = if (totalPlayed > 0) (totalSolved.toFloat() / totalPlayed.toFloat() * 100).toInt() else 100

    val totalHours = profile.totalPlayTimeSeconds / 3600
    val totalMins = (profile.totalPlayTimeSeconds % 3600) / 60
    val playTimeStr = if (totalHours > 0) "${totalHours} jam ${totalMins} menit" else "${totalMins} menit"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistik Otak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_btn")) {
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
            // Accuracy Circular Gauge Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("AKURASI PIKIRAN", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("$accuracy%", fontSize = 36.sp, color = NeonEmerald, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$totalSolved dari $totalPlayed puzzle selesai", fontSize = 12.sp, color = Color.LightGray)
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { (accuracy / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.size(76.dp),
                            strokeWidth = 8.dp,
                            color = NeonEmerald,
                            trackColor = Color(0xFF1E2D4A)
                        )
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Stat Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Puzzle",
                    value = "$totalPlayed",
                    icon = Icons.Default.Extension,
                    color = NeonCyan
                )
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Puzzle Berhasil",
                    value = "$totalSolved",
                    icon = Icons.Default.CheckCircle,
                    color = NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Level Tertinggi",
                    value = "Level ${profile.highestUnlockedLevel}",
                    icon = Icons.Default.TrendingUp,
                    color = NeonGold
                )
                StatMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Streak Harian",
                    value = "${profile.streak} Hari",
                    icon = Icons.Default.LocalFireDepartment,
                    color = NeonCoral
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StatMetricCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Total Waktu Bermain",
                value = playTimeStr,
                icon = Icons.Default.Schedule,
                color = NeonPurple
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cognitive Skill Breakdown Bars
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Profil Keahlian Kognitif 3D",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    CognitiveBar("Logika & Deduksi", 0.85f, NeonCyan)
                    CognitiveBar("Memori & Pola", 0.78f, NeonPurple)
                    CognitiveBar("Spasial & Rotasi 3D", 0.90f, NeonGold)
                    CognitiveBar("Problem Solving", 0.82f, NeonEmerald)
                    CognitiveBar("Konsentrasi & Fokus", 0.88f, NeonCoral)
                }
            }
        }
    }
}

@Composable
fun CognitiveBar(label: String, score: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = Color(0xFFD6E2F5))
            Text("${(score * 100).toInt()}%", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF162540)
        )
    }
}

@Composable
fun StatMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = modifier
            .border(1.dp, Color(0xFF1B2C4B), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
        }
    }
}
