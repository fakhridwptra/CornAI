package com.cornai.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLanguageClick: () -> Unit = {},
    onDarkModeToggle: (Boolean) -> Unit = {},
    onClearCache: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onShareApp: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isDarkMode by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showRateAppDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // General Settings
            SettingsSection(title = "Umum") {
                SettingsItemWithSwitch(
                    icon = Icons.Default.DarkMode,
                    title = "Mode Gelap",
                    subtitle = "Aktifkan tema gelap",
                    isChecked = isDarkMode,
                    onCheckedChange = {
                        isDarkMode = it
                        onDarkModeToggle(it)
                    }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Language,
                    title = "Bahasa",
                    subtitle = "Indonesia",
                    onClick = onLanguageClick
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Notifications,
                    title = "Notifikasi",
                    subtitle = "Pengaturan notifikasi",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Data Settings
            SettingsSection(title = "Data") {
                SettingsItemWithClick(
                    icon = Icons.Default.Storage,
                    title = "Hapus Cache",
                    subtitle = "Ukuran cache: 24.5 MB",
                    onClick = { showClearCacheDialog = true }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.CloudDownload,
                    title = "Backup Data",
                    subtitle = "Ekspor data scan",
                    onClick = { }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.CloudUpload,
                    title = "Pulihkan Data",
                    subtitle = "Impor data backup",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Support Settings
            SettingsSection(title = "Dukung Kami") {
                SettingsItemWithClick(
                    icon = Icons.Default.Star,
                    title = "Beri Rating",
                    subtitle = "Berikan penilaian 5 bintang",
                    onClick = { showRateAppDialog = true }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Share,
                    title = "Bagikan App",
                    subtitle = "Ajak teman menggunakan",
                    onClick = onShareApp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Legal Settings
            SettingsSection(title = "Legal") {
                SettingsItemWithClick(
                    icon = Icons.Default.PrivacyTip,
                    title = "Kebijakan Privasi",
                    subtitle = "Baca kebijakan privasi kami",
                    onClick = onPrivacyClick
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Description,
                    title = "Syarat & Ketentuan",
                    subtitle = "Baca ketentuan penggunaan",
                    onClick = onTermsClick
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Info,
                    title = "Lisensi",
                    subtitle = "Lisensi open source",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Corn AI",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Versi 1.0.0",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Deteksi Penyakit Jagung dengan AI\nAkurasi 96% | 10 Kelas Deteksi",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Made with ❤️") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Error.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Clear Cache Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Hapus Cache?", fontWeight = FontWeight.Bold) },
            text = { Text("Cache berukuran 24.5 MB akan dihapus. Data scan Anda tidak akan terpengaruh.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        onClearCache()
                    }
                ) {
                    Text("Hapus", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Rate App Dialog
    if (showRateAppDialog) {
        AlertDialog(
            onDismissRequest = { showRateAppDialog = false },
            title = { Text("Beri Rating", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Bagaimana pengalaman Anda dengan Corn AI?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showRateAppDialog = false
                    onRateApp()
                }) {
                    Text("Kirim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateAppDialog = false }) {
                    Text("Nanti")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItemWithClick(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}

@Composable
private fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
                .background(GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectorDialog(
    languages: List<LanguageOption>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Bahasa", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(language.code)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language.code == selectedLanguage,
                            onClick = {
                                onLanguageSelected(language.code)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = language.name,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (language.isNative) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${language.nativeName})",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val isNative: Boolean = false
)