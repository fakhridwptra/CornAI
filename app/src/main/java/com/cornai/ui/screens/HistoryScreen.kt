package com.cornai.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.*
import com.cornai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class ScanHistoryData(
    val id: String,
    val diseaseName: String,
    val displayName: String,
    val confidence: Float,
    val isHealthy: Boolean,
    val timestamp: Long,
    val symptoms: List<String> = emptyList(),
    val treatment: String = ""
)

data class HistoryStatsData(
    val totalScans: Int = 0,
    val healthyCount: Int = 0,
    val diseaseCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onHistoryEnhancedClick: () -> Unit = {},
    historyList: List<ScanHistoryData> = emptyList(),
    stats: HistoryStatsData = HistoryStatsData(),
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    val filteredHistory = remember(searchQuery, historyList) {
        if (searchQuery.isBlank()) historyList
        else historyList.filter {
            it.diseaseName.contains(searchQuery, ignoreCase = true) || it.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

    var selectedHistory by remember { mutableStateOf<ScanHistoryData?>(null) }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari riwayat...", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            ),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = ""; showSearch = false }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                                    }
                                }
                            }
                        )
                    } else {
                        Text("Riwayat Scan", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    if (showSearch) {
                        IconButton(onClick = { showSearch = false; searchQuery = "" }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    } else {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) { Icon(Icons.Default.Search, contentDescription = "Search", tint = GreenPrimary) }
                        IconButton(onClick = onHistoryEnhancedClick) { Icon(Icons.Default.FilterList, contentDescription = "Advanced Filter", tint = GreenPrimary) }
                        if (historyList.isNotEmpty()) {
                            IconButton(onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, buildString {
                                        append("📋 Riwayat Scan Corn AI\n\n")
                                        historyList.take(5).forEach { h ->
                                            append("• ${h.displayName} (${(h.confidence * 100).toInt()}%) - ${formatTimestamp(h.timestamp)}\n")
                                        }
                                        append("\nDownload Corn AI sekarang!")
                                    })
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Riwayat"))
                            }) { Icon(Icons.Default.Share, contentDescription = "Share", tint = GreenPrimary) }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item { ShimmerStats() }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        items(5) { ShimmerListItem() }
                        items(5) { ShimmerCard() }
                    }
                }
                historyList.isEmpty() -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Belum ada riwayat scan", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Mulai scan untuk melihat hasil di sini", fontSize = 14.sp, color = TextSecondary)
                    }
                }
                searchQuery.isNotEmpty() && filteredHistory.isEmpty() -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tidak ditemukan", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("Coba kata kunci lain", fontSize = 14.sp, color = TextSecondary)
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item { HistoryStatsRowDisplay(stats = stats) }
                        item { Text("Scan Terbaru", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp)) }
                        items(filteredHistory) { history ->
                            HistoryCard(history = history, onClick = { selectedHistory = history })
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        selectedHistory?.let { history ->
            HistoryDetailBottomSheet(history = history, onDismiss = { selectedHistory = null }, onShare = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, buildString {
                        append("🌽 Hasil Scan Corn AI\n\n")
                        append("Penyakit: ${history.displayName}\n")
                        append("Kepercayaan: ${(history.confidence * 100).toInt()}%\n")
                        if (!history.isHealthy && history.treatment.isNotEmpty()) {
                            append("\nPenanganan: ${history.treatment}\n")
                        }
                        append("\nScan dengan Corn AI - Deteksi Penyakit Jagung")
                    })
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Hasil"))
            })
        }
    }
}

@Composable
private fun HistoryStatsRowDisplay(stats: HistoryStatsData) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(value = stats.totalScans.toString(), label = "Total Scan", color = GreenPrimary, modifier = Modifier.weight(1f))
        StatCard(value = stats.healthyCount.toString(), label = "Sehat", color = Success, modifier = Modifier.weight(1f))
        StatCard(value = stats.diseaseCount.toString(), label = "Penyakit", color = Error, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 12.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryCard(history: ScanHistoryData, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(if (history.isHealthy) HealthyGreen.copy(alpha = 0.1f) else DiseaseRed.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (history.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning, contentDescription = null, tint = if (history.isHealthy) HealthyGreen else DiseaseRed, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = history.displayName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = formatTimestamp(history.timestamp), fontSize = 12.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                ConfidenceBadge(confidence = history.confidence)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(isHealthy = history.isHealthy)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailBottomSheet(history: ScanHistoryData, onDismiss: () -> Unit, onShare: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(if (history.isHealthy) HealthyGreen.copy(alpha = 0.1f) else DiseaseRed.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = if (history.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning, contentDescription = null, tint = if (history.isHealthy) HealthyGreen else DiseaseRed, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = history.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = formatTimestamp(history.timestamp), fontSize = 12.sp, color = TextSecondary)
                }
                ConfidenceBadge(confidence = history.confidence)
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (!history.isHealthy) {
                if (history.symptoms.isNotEmpty()) {
                    Text(text = "Gejala", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    history.symptoms.forEach { symptom ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Circle, contentDescription = null, tint = Error, modifier = Modifier.size(8.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = symptom, fontSize = 13.sp, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (history.treatment.isNotEmpty()) {
                    Text(text = "Penanganan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = history.treatment, fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = HealthyGreen.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Tanaman jagung dalam kondisi sehat!", fontSize = 14.sp, color = HealthyGreen, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bagikan")
                }
                Button(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                    Text("Tutup")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}