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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementRecord
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(
    achievements: List<AchievementRecord>,
    onBack: () -> Unit
) {
    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Prestasi & Penghargaan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("achievement_back_btn")) {
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
        ) {
            // Summary Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(NeonGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = NeonGold,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Terbuka: $unlockedCount / ${achievements.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = if (achievements.isNotEmpty()) unlockedCount.toFloat() / achievements.size.toFloat() else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonGold,
                            trackColor = Color(0xFF1E2F4C)
                        )
                    }
                }
            }

            // Achievements List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(achievements) { ach ->
                    AchievementItemCard(ach)
                }
            }
        }
    }
}

@Composable
fun AchievementItemCard(ach: AchievementRecord) {
    val icon = getAchievementIcon(ach.icon)
    val isUnlocked = ach.isUnlocked

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) DarkSurfaceCard else Color(0xFF0F1728)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isUnlocked) NeonCyan.copy(alpha = 0.5f) else Color(0xFF1B283E),
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
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) NeonGold.copy(alpha = 0.2f) else Color(0xFF162136)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = ach.title,
                    tint = if (isUnlocked) NeonGold else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ach.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isUnlocked) Color.White else Color.Gray
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = ach.description,
                    fontSize = 12.sp,
                    color = if (isUnlocked) Color(0xFFCCDDF2) else Color.DarkGray
                )
                if (!isUnlocked && ach.maxProgress > 1) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ach.currentProgress.toFloat() / ach.maxProgress.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonCyan,
                        trackColor = Color(0xFF1A2A47)
                    )
                }
            }

            if (isUnlocked) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Selesai",
                    tint = NeonEmerald,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun getAchievementIcon(name: String): ImageVector {
    return when (name) {
        "flag" -> Icons.Default.Flag
        "psychology" -> Icons.Default.Psychology
        "extension" -> Icons.Default.Extension
        "workspace_premium" -> Icons.Default.WorkspacePremium
        "favorite" -> Icons.Default.Favorite
        "bolt" -> Icons.Default.Bolt
        "event" -> Icons.Default.Event
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "monetization_on" -> Icons.Default.MonetizationOn
        else -> Icons.Default.Star
    }
}
