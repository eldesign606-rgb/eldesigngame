package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    profile: PlayerProfile,
    onUpdateSettings: (sound: Boolean, music: Boolean, haptic: Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onBack: () -> Unit
) {
    var soundOn by remember(profile.soundEnabled) { mutableStateOf(profile.soundEnabled) }
    var musicOn by remember(profile.musicEnabled) { mutableStateOf(profile.musicEnabled) }
    var hapticOn by remember(profile.hapticEnabled) { mutableStateOf(profile.hapticEnabled) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pengaturan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("setting_back_btn")) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Audio & Haptic Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Audio & Umpan Balik",
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    SettingToggleRow(
                        icon = Icons.Default.VolumeUp,
                        title = "Efek Suara (SFX)",
                        checked = soundOn,
                        onCheckedChange = {
                            soundOn = it
                            onUpdateSettings(soundOn, musicOn, hapticOn)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        icon = Icons.Default.MusicNote,
                        title = "Musik Latar (Synthesizer)",
                        checked = musicOn,
                        onCheckedChange = {
                            musicOn = it
                            onUpdateSettings(soundOn, musicOn, hapticOn)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        title = "Getaran Haptic Sentuhan",
                        checked = hapticOn,
                        onCheckedChange = {
                            hapticOn = it
                            onUpdateSettings(soundOn, musicOn, hapticOn)
                        }
                    )
                }
            }

            // Panduan & Kontrol 3D
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Text("  Kontrol Dunia Brain Lab 3D", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Geser layar (Touch Drag): Memutar sudut pandang kamera orbit 3D.\n• Cubit layar (Pinch Zoom): Menyesuaikan jarak pandang.\n• Tap langsung pada objek 3D di ruangan (tombol, panel, kubus, konektor) untuk berinteraksi.\n• Semua puzzle dapat dimainkan secara offline tanpa koneksi internet.",
                        fontSize = 13.sp,
                        color = Color(0xFFCAD8EE),
                        lineHeight = 20.sp
                    )
                }
            }

            // About App
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Tentang BrainQuest 3D", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Versi: 1.0 (Low-Poly 3D Engine)", fontSize = 12.sp, color = Color.Gray)
                    Text("Didesain khusus untuk mengasah ketajaman logika, spasial, memori, dan pemecahan masalah.", fontSize = 12.sp, color = Color.LightGray)
                }
            }

            // Reset Game Data
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A141D)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, NeonCoral, RoundedCornerShape(16.dp))
                    .testTag("reset_progress_btn")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = NeonCoral)
                Text("  Reset Progres Permainan", color = NeonCoral, fontWeight = FontWeight.Bold)
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Konfirmasi Reset Progres", fontWeight = FontWeight.Bold, color = Color.White) },
                text = { Text("Apakah Anda yakin ingin menghapus seluruh skor, bintang, dan level yang telah terbuka?", color = Color.LightGray) },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetDialog = false
                            onResetProgress()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCoral)
                    ) {
                        Text("HAPUS SEMUA", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showResetDialog = false }) {
                        Text("BATAL", color = Color.White)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            Text("  $title", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = Color(0xFF005666),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF1E2D4A)
            )
        )
    }
}
