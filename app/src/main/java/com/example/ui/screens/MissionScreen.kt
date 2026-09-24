package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

data class MissionItem(
    val id: String,
    val title: String,
    val rewardXP: Int,
    val rewardCoins: Int,
    val current: Int,
    val target: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionScreen(
    profile: PlayerProfile,
    onBack: () -> Unit
) {
    val missions = listOf(
        MissionItem(
            id = "m1",
            title = "Selesaikan 3 level laboratorium",
            rewardXP = 150,
            rewardCoins = 30,
            current = minOf(profile.totalPuzzlesSolved, 3),
            target = 3
        ),
        MissionItem(
            id = "m2",
            title = "Capai 3 bintang pada level apa pun",
            rewardXP = 120,
            rewardCoins = 25,
            current = 1,
            target = 1
        ),
        MissionItem(
            id = "m3",
            title = "Kumpulkan total 200 Koin Laboratorium",
            rewardXP = 200,
            rewardCoins = 50,
            current = minOf(profile.coins, 200),
            target = 200
        ),
        MissionItem(
            id = "m4",
            title = "Pertahankan Streak 3 Hari",
            rewardXP = 300,
            rewardCoins = 75,
            current = minOf(profile.streak, 3),
            target = 3
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Misi & Sasaran Harian",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("mission_back_btn")) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCoral.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 Sasaran Pelatihan Kognitif",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Selesaikan misi harian untuk mempercepat peningkatan level dan mengumpulkan koin ekstra.",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            items(missions) { mission ->
                MissionCard(mission)
            }
        }
    }
}

@Composable
fun MissionCard(mission: MissionItem) {
    val isComplete = mission.current >= mission.target

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isComplete) NeonEmerald.copy(alpha = 0.5f) else Color(0xFF1F304E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isComplete) NeonEmerald.copy(alpha = 0.2f) else Color(0xFF17253D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isComplete) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = if (isComplete) NeonEmerald else NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mission.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+${mission.rewardXP} XP  •  +${mission.rewardCoins} Koin 🪙",
                    fontSize = 12.sp,
                    color = NeonGold,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { mission.current.toFloat() / mission.target.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isComplete) NeonEmerald else NeonCyan,
                    trackColor = Color(0xFF162540)
                )
            }

            Text(
                text = "${mission.current}/${mission.target}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isComplete) NeonEmerald else Color.Gray
            )
        }
    }
}
