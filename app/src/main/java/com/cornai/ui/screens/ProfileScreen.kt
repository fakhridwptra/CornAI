package com.cornai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

@Composable
private fun SimpleDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().height(1.dp).background(Divider))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onHelpClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onNotificationSettingsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileEnhancedClick: () -> Unit = {},
    onHistoryEnhancedClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    userName: String = "Guest",
    userEmail: String = "",
    isGuest: Boolean = false,
    totalScans: Int = 0,
    healthyScans: Int = 0,
    diseaseScans: Int = 0
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GreenPrimary) }
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = GreenPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState)) {
            ProfileHeader(userName = userName, userEmail = userEmail, isGuest = isGuest, onProfileEnhancedClick = onProfileEnhancedClick)
            Spacer(modifier = Modifier.height(24.dp))
            StatsCard(totalScans = totalScans, healthyScans = healthyScans, diseaseScans = diseaseScans)
            Spacer(modifier = Modifier.height(24.dp))
            SettingsSection(onNotificationSettingsClick = onNotificationSettingsClick, onSettingsClick = onSettingsClick)
            Spacer(modifier = Modifier.height(24.dp))
            QuickAccessSection(onHistoryEnhancedClick = onHistoryEnhancedClick)
            Spacer(modifier = Modifier.height(24.dp))
            AboutSection(onPrivacyClick = onPrivacyClick, onHelpClick = onHelpClick)

            // Sign Out Button / Login Button
            Spacer(modifier = Modifier.height(16.dp))
            if (isGuest) {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login / Register", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            } else {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    isGuest: Boolean,
    onProfileEnhancedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colors = listOf(GreenDark, GreenPrimary)))
            .clickable(onClick = onProfileEnhancedClick)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(shape = RoundedCornerShape(12.dp), color = GoldPrimary.copy(alpha = 0.2f)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Level 5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (userEmail.isNotEmpty()) {
                Text(userEmail, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }
            if (isGuest) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                    Text("Mode Tamu", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = 0.75f,
                modifier = Modifier.width(200.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = GoldPrimary,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap untuk lihat profil lengkap", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun StatsCard(totalScans: Int, healthyScans: Int, diseaseScans: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem(value = "$totalScans", label = "Total Scan", color = GreenPrimary)
            StatItem(value = "$healthyScans", label = "Sehat", color = Success)
            StatItem(value = "$diseaseScans", label = "Penyakit", color = Error)
            StatItem(value = "7", label = "Streak", color = GoldPrimary)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun SettingsSection(onNotificationSettingsClick: () -> Unit, onSettingsClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Pengaturan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column {
                SettingsItem(icon = Icons.Default.Notifications, title = "Notifikasi", subtitle = "Pengingat penyiraman & semprot", onClick = { onNotificationSettingsClick() })
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Settings, title = "Pengaturan Lengkap", subtitle = "Tema, bahasa, cache, dll", onClick = { onSettingsClick() })
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Language, title = "Bahasa", subtitle = "Indonesia")
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.DarkMode, title = "Mode Gelap", subtitle = "Aktifkan mode hemat baterai")
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Storage, title = "Hapus Cache", subtitle = "Bersihkan data sementara")
            }
        }
    }
}

@Composable
private fun QuickAccessSection(onHistoryEnhancedClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Akses Cepat", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column {
                SettingsItem(icon = Icons.Default.History, title = "Riwayat Lengkap", subtitle = "Semua hasil scan dengan filter", onClick = { onHistoryEnhancedClick() })
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Analytics, title = "Statistik Detail", subtitle = "Grafik kesehatan tanaman")
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.EmojiEvents, title = "Achievements", subtitle = "8/12 badge berhasil")
            }
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(GreenPrimary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
private fun AboutSection(onPrivacyClick: () -> Unit, onHelpClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Tentang", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column {
                SettingsItem(icon = Icons.Default.Info, title = "Versi App", subtitle = "Corn AI v1.0.0")
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Description, title = "Kebijakan Privasi", subtitle = "Baca kebijakan privasi kami", onClick = onPrivacyClick)
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Help, title = "Bantuan", subtitle = "Panduan penggunaan app", onClick = onHelpClick)
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Star, title = "Beri Rating", subtitle = "Dukung pengembangan kami")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f))) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Corn AI", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Deteksi Penyakit Jagung dengan AI", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Akurasi Model: 96% | 10 Kelas Deteksi", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}