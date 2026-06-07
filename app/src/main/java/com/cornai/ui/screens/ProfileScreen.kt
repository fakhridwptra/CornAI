package com.cornai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

data class BadgeData(
    val title: String,
    val requirement: String,
    val description: String,
    val unlocked: Boolean,
    val icon: ImageVector,
    val reward: String
)

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
    diseaseScans: Int = 0,
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {},
    onClearCache: () -> Unit = {},
    cacheSize: String = "0 MB",
    currentLanguage: String = "id",
    onLanguageSelected: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showFarmDialog by remember { mutableStateOf(false) }
    var farmSize by remember { mutableStateOf("1.5 Hektar") }
    var cornVariety by remember { mutableStateOf("Pioneer P35") }
    var plantingDate by remember { mutableStateOf("12 April 2026") }
    var selectedBadge by remember { mutableStateOf<BadgeData?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
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
            
            // Farm Info Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lahan Pertanian Saya 🌾",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { showFarmDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Lahan", tint = GreenPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Luas Lahan", fontSize = 11.sp, color = TextSecondary)
                            Text(farmSize, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Varietas Jagung", fontSize = 11.sp, color = TextSecondary)
                            Text(cornVariety, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Tanggal Tanam", fontSize = 11.sp, color = TextSecondary)
                            Text(plantingDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            SettingsSection(
                onNotificationSettingsClick = onNotificationSettingsClick,
                onSettingsClick = onSettingsClick,
                isDarkMode = isDarkMode,
                onDarkModeToggle = onDarkModeToggle,
                onClearCache = onClearCache,
                cacheSize = cacheSize,
                currentLanguage = currentLanguage,
                onLanguageSelected = onLanguageSelected
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickAccessSection(onHistoryEnhancedClick = onHistoryEnhancedClick)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Achievements Section
            val badges = listOf(
                BadgeData(
                    title = "Pendeteksi Pertama",
                    requirement = "Scan 1 kali",
                    description = "Lencana ini diberikan kepada petani yang pertama kali mencoba fitur deteksi penyakit jagung dengan Corn AI.",
                    unlocked = totalScans >= 1,
                    icon = Icons.Default.CameraAlt,
                    reward = "+50 XP"
                ),
                BadgeData(
                    title = "Pelindung Jagung",
                    requirement = "Scan 3 penyakit",
                    description = "Diraih setelah mendeteksi setidaknya 3 penyakit berbeda pada tanaman jagung. Kamu adalah garda terdepan ladang!",
                    unlocked = diseaseScans >= 3,
                    icon = Icons.Default.Security,
                    reward = "+100 XP"
                ),
                BadgeData(
                    title = "Ahli Diagnosis",
                    requirement = "Scan 10 kali",
                    description = "Lencana bergengsi untuk petani yang telah melakukan 10 scan atau lebih. Keahlianmu sudah terasah!",
                    unlocked = totalScans >= 10,
                    icon = Icons.Default.EmojiEvents,
                    reward = "+200 XP + Badge Emas"
                ),
                BadgeData(
                    title = "Petani Teladan",
                    requirement = "Isi info lahan",
                    description = "Diberikan kepada petani yang telah melengkapi informasi lahan pertaniannya. Data lengkap = analisis lebih akurat!",
                    unlocked = farmSize.isNotEmpty() && cornVariety.isNotEmpty() && plantingDate.isNotEmpty(),
                    icon = Icons.Default.Agriculture,
                    reward = "+75 XP"
                )
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Lencana Prestasi", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BadgeItem(
                                badge = badges[0],
                                modifier = Modifier.weight(1f),
                                onClick = { selectedBadge = badges[0] }
                            )
                            BadgeItem(
                                badge = badges[1],
                                modifier = Modifier.weight(1f),
                                onClick = { selectedBadge = badges[1] }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BadgeItem(
                                badge = badges[2],
                                modifier = Modifier.weight(1f),
                                onClick = { selectedBadge = badges[2] }
                            )
                            BadgeItem(
                                badge = badges[3],
                                modifier = Modifier.weight(1f),
                                onClick = { selectedBadge = badges[3] }
                            )
                        }
                    }
                }
            }

            // Badge Detail Bottom Sheet
            selectedBadge?.let { badge ->
                BadgeDetailBottomSheet(
                    badge = badge,
                    sheetState = sheetState,
                    onDismiss = { selectedBadge = null }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            AboutSection(onPrivacyClick = onPrivacyClick, onHelpClick = onHelpClick)

            if (showFarmDialog) {
                var inputSize by remember { mutableStateOf(farmSize) }
                var inputVariety by remember { mutableStateOf(cornVariety) }
                var inputDate by remember { mutableStateOf(plantingDate) }
                
                AlertDialog(
                    onDismissRequest = { showFarmDialog = false },
                    title = { Text("Penyuntingan Info Lahan Tani", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = inputSize,
                                onValueChange = { inputSize = it },
                                label = { Text("Luas Lahan") },
                                placeholder = { Text("Contoh: 1.5 Hektar") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = inputVariety,
                                onValueChange = { inputVariety = it },
                                label = { Text("Varietas Jagung") },
                                placeholder = { Text("Contoh: Pioneer P35") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = inputDate,
                                onValueChange = { inputDate = it },
                                label = { Text("Tanggal Tanam") },
                                placeholder = { Text("Contoh: 12 April 2026") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                farmSize = inputSize
                                cornVariety = inputVariety
                                plantingDate = inputDate
                                showFarmDialog = false
                            }
                        ) {
                            Text("Simpan", color = GreenPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFarmDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Sign Out Button / Login Button
            Spacer(modifier = Modifier.height(16.dp))
            if (isGuest) {
                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
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
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
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
    // XP bar animated on enter
    var xpEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        xpEntered = true
    }
    val animatedXp by animateFloatAsState(
        targetValue = if (xpEntered) 0.75f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutQuad),
        label = "xpBar"
    )
    val xpLabelAlpha by animateFloatAsState(
        targetValue = if (xpEntered) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 1000, easing = EaseOutQuad),
        label = "xpLabelAlpha"
    )

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
            Spacer(modifier = Modifier.height(14.dp))
            // Animated XP label
            Text(
                text = "750 / 1000 XP  •  Level 5",
                fontSize = 11.sp,
                color = GoldPrimary.copy(alpha = xpLabelAlpha),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Animated XP progress bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedXp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldPrimary.copy(alpha = 0.8f), GoldPrimary)
                            )
                        )
                        .clip(RoundedCornerShape(4.dp))
                )
            }
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
private fun SettingsSection(
    onNotificationSettingsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {},
    onClearCache: () -> Unit = {},
    cacheSize: String = "0 MB",
    currentLanguage: String = "id",
    onLanguageSelected: (String) -> Unit = {}
) {
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text("Pengaturan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column {
                SettingsItem(icon = Icons.Default.Notifications, title = "Notifikasi", subtitle = "Pengingat penyiraman & semprot", onClick = { onNotificationSettingsClick() })
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsItem(icon = Icons.Default.Settings, title = "Pengaturan Lengkap", subtitle = "Tema, bahasa, cache, dll", onClick = { onSettingsClick() })
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Bahasa — langsung dialog pilih bahasa
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Bahasa",
                    subtitle = if (currentLanguage == "id") "Indonesia" else "English",
                    onClick = { showLanguageDialog = true }
                )
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Mode Gelap — langsung toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDarkModeToggle(!isDarkMode) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(GreenPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mode Gelap", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text(if (isDarkMode) "Aktif" else "Nonaktif", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onDarkModeToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                    )
                }
                SimpleDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Hapus Cache — langsung dialog konfirmasi
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Hapus Cache",
                    subtitle = "Ukuran: $cacheSize",
                    onClick = { showClearCacheDialog = true }
                )
            }
        }
    }

    // Dialog: Pilih Bahasa
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Pilih Bahasa", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("id" to "🇮🇩  Indonesia", "en" to "🇺🇸  English").forEach { (code, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onLanguageSelected(code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == code,
                                onClick = {
                                    onLanguageSelected(code)
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 15.sp, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Tutup") }
            },
            containerColor = Surface
        )
    }

    // Dialog: Konfirmasi Hapus Cache
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Error) },
            title = { Text("Hapus Cache?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Cache berukuran $cacheSize akan dihapus.\nData scan Anda tidak akan terpengaruh.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearCache()
                        showClearCacheDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) { Text("Hapus", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) { Text("Batal") }
            },
            containerColor = Surface
        )
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
                SettingsItem(icon = Icons.AutoMirrored.Filled.Help, title = "Bantuan", subtitle = "Panduan penggunaan app", onClick = onHelpClick)
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

@Composable
private fun BadgeItem(
    badge: BadgeData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (badge.unlocked) GreenPrimary.copy(alpha = 0.08f) else Color.LightGray.copy(alpha = 0.05f))
            .border(1.dp, if (badge.unlocked) GreenPrimary.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (badge.unlocked) GoldPrimary.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = badge.title,
                tint = if (badge.unlocked) GoldPrimary else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = badge.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (badge.unlocked) TextPrimary else TextSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = badge.requirement,
            fontSize = 9.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (badge.unlocked) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✓ Terkunci Buka",
                fontSize = 8.sp,
                color = GreenPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BadgeDetailBottomSheet(
    badge: BadgeData,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge icon hero
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.unlocked)
                            Brush.radialGradient(listOf(GoldPrimary.copy(alpha = 0.3f), GoldPrimary.copy(alpha = 0.05f)))
                        else
                            Brush.radialGradient(listOf(Color.Gray.copy(alpha = 0.2f), Color.Gray.copy(alpha = 0.05f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = badge.title,
                    tint = if (badge.unlocked) GoldPrimary else Color.Gray,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status chip
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (badge.unlocked) GreenPrimary.copy(alpha = 0.12f) else Color.Gray.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (badge.unlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (badge.unlocked) GreenPrimary else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (badge.unlocked) "Sudah Dibuka" else "Belum Dibuka",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (badge.unlocked) GreenPrimary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = badge.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.description,
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Requirement & Reward cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Requirement
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Background),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Syarat", fontSize = 11.sp, color = TextSecondary)
                        Text(badge.requirement, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                    }
                }
                // Reward
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Background),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hadiah", fontSize = 11.sp, color = TextSecondary)
                        Text(badge.reward, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!badge.unlocked) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Ayo Kumpulkan Lencana Ini!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
            }
        }
    }
}