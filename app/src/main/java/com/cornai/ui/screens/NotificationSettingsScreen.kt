package com.cornai.ui.screens

import androidx.compose.foundation.background
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
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var wateringReminder by remember { mutableStateOf(true) }
    var sprayReminder by remember { mutableStateOf(true) }
    var diseaseAlert by remember { mutableStateOf(true) }
    var weeklyReport by remember { mutableStateOf(false) }
    var priceAlert by remember { mutableStateOf(false) }
    var wateringTime by remember { mutableStateOf("07:00") }
    var sprayTime by remember { mutableStateOf("16:00") }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Notifikasi", fontWeight = FontWeight.Bold) },
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
            // Reminder Section
            Text(
                text = "Pengingat",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    NotificationToggleItem(
                        icon = Icons.Default.WaterDrop,
                        title = "Pengingat Penyiraman",
                        subtitle = "Notifikasi harian untuk penyiraman",
                        isEnabled = wateringReminder,
                        onToggle = { wateringReminder = it },
                        hasTimePicker = wateringReminder,
                        timeValue = wateringTime
                    )
                    DividerNotification()
                    NotificationToggleItem(
                        icon = Icons.Default.Science,
                        title = "Pengingat Penyemrotan",
                        subtitle = "Notifikasi untuk jadwal semprot",
                        isEnabled = sprayReminder,
                        onToggle = { sprayReminder = it },
                        hasTimePicker = sprayReminder,
                        timeValue = sprayTime
                    )
                    DividerNotification()
                    NotificationToggleItem(
                        icon = Icons.Default.Warning,
                        title = "Peringatan Penyakit",
                        subtitle = "Notifikasi saat cuaca mendukung penyakit",
                        isEnabled = diseaseAlert,
                        onToggle = { diseaseAlert = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information Section
            Text(
                text = "Informasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    NotificationToggleItem(
                        icon = Icons.Default.Assessment,
                        title = "Laporan Mingguan",
                        subtitle = "Ringkasan kesehatan tanaman setiap minggu",
                        isEnabled = weeklyReport,
                        onToggle = { weeklyReport = it }
                    )
                    DividerNotification()
                    NotificationToggleItem(
                        icon = Icons.Default.TrendingUp,
                        title = "Harga Jagung",
                        subtitle = "Notifikasi perubahan harga pasar",
                        isEnabled = priceAlert,
                        onToggle = { priceAlert = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tips",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GreenPrimary
                        )
                        Text(
                            text = "Nonaktifkan notifikasi yang tidak diperlukan untuk mengurangi gangguan.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DividerNotification() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(Divider)
    )
}

@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    hasTimePicker: Boolean = false,
    timeValue: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                if (hasTimePicker && isEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeValue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GreenPrimary
                        )
                    }
                }
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GreenPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}