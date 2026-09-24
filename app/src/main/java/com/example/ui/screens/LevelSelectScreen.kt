package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LevelCatalog
import com.example.data.LevelDefinition
import com.example.data.LevelRecord
import com.example.data.PlayerProfile
import com.example.data.PuzzleCategory
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    profile: PlayerProfile,
    levelRecords: List<LevelRecord>,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<PuzzleCategory?>(null) }

    val recordMap = remember(levelRecords) {
        levelRecords.associateBy { it.levelId }
    }

    val filteredLevels = remember(selectedCategory) {
        if (selectedCategory == null) LevelCatalog.levels
        else LevelCatalog.levels.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pilih Level Laboratorium",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
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
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Semua (30)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                items(PuzzleCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Level Cards Grid / List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLevels) { levelDef ->
                    val isUnlocked = levelDef.id <= profile.highestUnlockedLevel
                    val rec = recordMap[levelDef.id]
                    val stars = rec?.stars ?: 0
                    val highScore = rec?.highScore ?: 0

                    LevelItemCard(
                        levelDef = levelDef,
                        isUnlocked = isUnlocked,
                        stars = stars,
                        highScore = highScore,
                        onClick = {
                            if (isUnlocked) onSelectLevel(levelDef.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelItemCard(
    levelDef: LevelDefinition,
    isUnlocked: Boolean,
    stars: Int,
    highScore: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) DarkSurface else Color(0xFF0F1626)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isUnlocked) Color(0xFF1E3355) else Color(0xFF172030),
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_item_${levelDef.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Level Number Circle / Lock icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) NeonCyan.copy(alpha = 0.2f) else Color(0xFF19253B)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        Text(
                            text = "${levelDef.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = NeonCyan
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = levelDef.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isUnlocked) Color.White else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${levelDef.category.label} • ${levelDef.difficulty}",
                        fontSize = 12.sp,
                        color = if (isUnlocked) NeonPurple else Color(0xFF556075)
                    )
                }
            }

            // Stars & High Score
            if (isUnlocked) {
                Column(horizontalAlignment = Alignment.End) {
                    Row {
                        for (s in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Bintang $s",
                                tint = if (s <= stars) NeonGold else Color(0xFF2C394E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (highScore > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Skor: $highScore",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
