// Composable UI untuk satu halaman tampilan aplikasi.
// File: java\com\cornai\ui\screens\SettingsScreen.kt

package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.R
import com.cornai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onClearCache: () -> Unit,
    cacheSize: String,
    onRateApp: () -> Unit = {},
    onShareApp: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onCheckModelUpdate: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showRateAppDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val isIndonesian = currentLanguage == "id"
    val titleText = if (isIndonesian) "Pengaturan" else "Settings"
    val darkThemeText = if (isIndonesian) "Mode Gelap" else "Dark Mode"
    val darkThemeSub = if (isIndonesian) "Aktifkan tema gelap" else "Enable dark theme"
    val languageText = if (isIndonesian) "Bahasa" else "Language"
    val languageSub = if (isIndonesian) "Indonesia" else "English"
    val notificationsText = if (isIndonesian) "Notifikasi" else "Notifications"
    val notificationsSub = if (isIndonesian) "Pengaturan notifikasi" else "Notification settings"
    val clearCacheText = if (isIndonesian) "Hapus Cache" else "Clear Cache"
    val cacheSizePrefix = if (isIndonesian) "Ukuran cache:" else "Cache size:"
    val supportText = if (isIndonesian) "Dukung Kami" else "Support Us"
    val rateText = if (isIndonesian) "Beri Rating" else "Rate App"
    val rateSub = if (isIndonesian) "Berikan penilaian 5 bintang" else "Give 5-star rating"
    val shareText = if (isIndonesian) "Bagikan App" else "Share App"
    val shareSub = if (isIndonesian) "Ajak teman menggunakan" else "Invite friends to use"
    val legalText = if (isIndonesian) "Legal" else "Legal"
    val privacyText = if (isIndonesian) "Kebijakan Privasi" else "Privacy Policy"
    val privacySub = if (isIndonesian) "Baca kebijakan privasi kami" else "Read our privacy policy"
    val termsText = if (isIndonesian) "Syarat & Ketentuan" else "Terms & Conditions"
    val termsSub = if (isIndonesian) "Baca ketentuan penggunaan" else "Read terms of use"
    val licenseText = if (isIndonesian) "Lisensi" else "Licenses"
    val licenseSub = if (isIndonesian) "Lisensi open source" else "Open source licenses"
    
    // Dialog texts:
    val clearCacheTitle = if (isIndonesian) "Hapus Cache?" else "Clear Cache?"
    val clearCacheConfirmMsg = if (isIndonesian) 
        "Cache berukuran $cacheSize akan dihapus. Data scan Anda tidak akan terpengaruh." 
        else "Cache size of $cacheSize will be cleared. Your scan data will not be affected."
    val clearBtn = if (isIndonesian) "Hapus" else "Clear"
    val cancelBtn = if (isIndonesian) "Batal" else "Cancel"

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text(titleText, fontWeight = FontWeight.Bold) },
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
            SettingsSection(title = if (isIndonesian) "Umum" else "General") {
                SettingsItemWithSwitch(
                    icon = Icons.Default.DarkMode,
                    title = darkThemeText,
                    subtitle = darkThemeSub,
                    isChecked = isDarkMode,
                    onCheckedChange = onDarkModeToggle
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Language,
                    title = languageText,
                    subtitle = languageSub,
                    onClick = { showLanguageDialog = true }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Notifications,
                    title = notificationsText,
                    subtitle = notificationsSub,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Data Settings
            SettingsSection(title = if (isIndonesian) "Data" else "Data") {
                SettingsItemWithClick(
                    icon = Icons.Default.Storage,
                    title = clearCacheText,
                    subtitle = "$cacheSizePrefix $cacheSize",
                    onClick = { showClearCacheDialog = true }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.SystemUpdate,
                    title = if (isIndonesian) "Pembaruan Model AI" else "AI Model Update",
                    subtitle = if (isIndonesian) "Periksa pembaruan model OTA" else "Check OTA model updates",
                    onClick = onCheckModelUpdate
                )
                SettingsItemWithClick(
                    icon = Icons.Default.CloudDownload,
                    title = if (isIndonesian) "Backup Data" else "Backup Data",
                    subtitle = if (isIndonesian) "Ekspor data scan" else "Export scan data",
                    onClick = { }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.CloudUpload,
                    title = if (isIndonesian) "Pulihkan Data" else "Restore Data",
                    subtitle = if (isIndonesian) "Impor data backup" else "Import backup data",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Support Settings
            SettingsSection(title = supportText) {
                SettingsItemWithClick(
                    icon = Icons.Default.Star,
                    title = rateText,
                    subtitle = rateSub,
                    onClick = { showRateAppDialog = true }
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Share,
                    title = shareText,
                    subtitle = shareSub,
                    onClick = onShareApp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Legal Settings
            SettingsSection(title = legalText) {
                SettingsItemWithClick(
                    icon = Icons.Default.PrivacyTip,
                    title = privacyText,
                    subtitle = privacySub,
                    onClick = onPrivacyClick
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Description,
                    title = termsText,
                    subtitle = termsSub,
                    onClick = onTermsClick
                )
                SettingsItemWithClick(
                    icon = Icons.Default.Info,
                    title = licenseText,
                    subtitle = licenseSub,
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
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_cornai),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
                        text = if (isIndonesian) "Deteksi Penyakit Jagung dengan AI\nAkurasi 96% | 10 Kelas Deteksi" 
                               else "Corn Disease Detection with AI\n96% Accuracy | 10 Detection Classes",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
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
            title = { Text(clearCacheTitle, fontWeight = FontWeight.Bold) },
            text = { Text(clearCacheConfirmMsg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        onClearCache()
                    }
                ) {
                    Text(clearBtn, color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text(cancelBtn)
                }
            }
        )
    }

    // Rate App Dialog
    if (showRateAppDialog) {
        AlertDialog(
            onDismissRequest = { showRateAppDialog = false },
            title = { Text(rateText, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(if (isIndonesian) "Bagaimana pengalaman Anda dengan Corn AI?" else "How is your experience with Corn AI?")
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
                    Text(if (isIndonesian) "Kirim" else "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateAppDialog = false }) {
                    Text(if (isIndonesian) "Nanti" else "Later")
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        val languages = listOf(
            LanguageOption("id", "Bahasa Indonesia", "Indonesian", isNative = true),
            LanguageOption("en", "English", "English", isNative = true)
        )
        LanguageSelectorDialog(
            languages = languages,
            selectedLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected,
            onDismiss = { showLanguageDialog = false }
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
