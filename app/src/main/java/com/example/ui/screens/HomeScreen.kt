package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProfile
import com.example.engine3d.BrainLab3DCanvas
import com.example.engine3d.Camera3D
import com.example.engine3d.Mesh3D
import com.example.engine3d.Particle3D
import com.example.engine3d.Polygon3D
import com.example.engine3d.Vec3
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    profile: PlayerProfile,
    onStartPlay: () -> Unit,
    onDailyChallenge: () -> Unit,
    onAchievements: () -> Unit,
    onStatistics: () -> Unit,
    onMissions: () -> Unit,
    onSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val camera = remember {
        Camera3D(
            azimuthDeg = 20f,
            elevationDeg = 25f,
            distance = 6.5f,
            target = Vec3(0f, 0f, 0f)
        )
    }
    camera.azimuthDeg = (time * 57.2958f * 0.4f) % 360f

    // Generate floating 3D brain crystal models
    val homePolys = remember(time) {
        val list = mutableListOf<Polygon3D>()
        val bob = sin(time * 2f) * 0.15f

        // Central brain core (hologram cube nodes)
        list.addAll(
            Mesh3D.createCube(
                center = Vec3(0f, 0.2f + bob, 0f),
                size = 1.1f,
                frontColor = Color(0xFF00E5FF),
                backColor = Color(0xFF9D4EDD),
                topColor = Color(0xFFFFD166),
                bottomColor = Color(0xFF06D6A0),
                leftColor = Color(0xFFFF477E),
                rightColor = Color(0xFF3A86FF),
                outlineColor = Color(0xFFFFFFFF)
            )
        )

        // Floating orbital satellite cubes
        val orbitCount = 4
        for (i in 0 until orbitCount) {
            val ang = time * 1.5f + (6.28318f * i / orbitCount)
            val ox = 1.6f * cos(ang)
            val oz = 1.6f * sin(ang)
            list.addAll(
                Mesh3D.createPyramid(
                    center = Vec3(ox, 0.2f + bob + sin(time * 3f + i) * 0.1f, oz),
                    baseSize = 0.4f,
                    height = 0.55f,
                    color = if (i % 2 == 0) NeonGold else NeonPurple,
                    outlineColor = Color(0xFFFFFFFF)
                )
            )
        }

        // Base pedestal
        list.addAll(
            Mesh3D.createCylinder(
                center = Vec3(0f, -1.2f, 0f),
                radius = 1.8f,
                height = 0.25f,
                segments = 12,
                color = Color(0xFF142442),
                outlineColor = NeonCyan
            )
        )
        list
    }

    val homeParticles = remember(time) {
        val parts = mutableListOf<Particle3D>()
        for (i in 0..8) {
            val a = (time * 2f + i * 0.7f) % 6.28f
            parts.add(
                Particle3D(
                    pos = Vec3(1.3f * cos(a), -0.5f + (i * 0.2f), 1.3f * sin(a)),
                    vel = Vec3(0f, 0f, 0f),
                    color = if (i % 2 == 0) NeonCyan else NeonGold,
                    life = 1f,
                    size = 5f
                )
            )
        }
        parts
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Logo & Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "BrainQuest Icon",
                    tint = NeonCyan,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BrainQuest 3D",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "ASAH OTAK & LOGIKA LAB",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        ),
                        color = NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Stats Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(NeonCyan, Color(0xFF005F73)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${profile.level}",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("LEVEL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("${profile.xp} XP", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Coins
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Koin",
                            tint = NeonGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile.coins}",
                            fontWeight = FontWeight.Bold,
                            color = NeonGold,
                            fontSize = 15.sp
                        )
                    }

                    // Streak
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = NeonCoral,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.streak} Hari",
                            fontWeight = FontWeight.Bold,
                            color = NeonCoral,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3D Brain Lab Interactive Viewport Preview
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(22.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BrainLab3DCanvas(
                        camera = camera,
                        polygons = homePolys,
                        particles = homeParticles,
                        allowCameraControl = true,
                        onObjectTapped = { /* preview tap */ }
                    )
                    // Floating Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xCC090F1C))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Lab 3D Interaktif • Geser untuk Memutar",
                            fontSize = 11.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Action Button: MULAI BERMAIN
            Button(
                onClick = onStartPlay,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFF7F00FF))
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .testTag("start_play_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "▶ MULAI BERMAIN",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Menu Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "PUZZLE HARI INI",
                    icon = Icons.Default.Extension,
                    iconColor = NeonEmerald,
                    tag = "daily_puzzle_btn",
                    onClick = onDailyChallenge
                )
                HomeMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "PRESTASI",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = NeonGold,
                    tag = "achievement_btn",
                    onClick = onAchievements
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "STATISTIK",
                    icon = Icons.Default.Insights,
                    iconColor = NeonCyan,
                    tag = "statistics_btn",
                    onClick = onStatistics
                )
                HomeMenuCard(
                    modifier = Modifier.weight(1f),
                    title = "MISI",
                    icon = Icons.Default.TrackChanges,
                    iconColor = NeonCoral,
                    tag = "missions_btn",
                    onClick = onMissions
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Settings Button
            HomeMenuCard(
                modifier = Modifier.fillMaxWidth(),
                title = "PENGATURAN",
                icon = Icons.Default.Settings,
                iconColor = Color.LightGray,
                tag = "settings_btn",
                onClick = onSettings
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HomeMenuCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    iconColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = modifier
            .height(72.dp)
            .border(1.dp, Color(0xFF1F3052), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}
