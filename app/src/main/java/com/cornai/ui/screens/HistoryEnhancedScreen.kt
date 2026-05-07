package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryEnhancedScreen(
    onBack: () -> Unit,
    onExportData: () -> Unit = {},
    onDeleteAll: () -> Unit = {}
) {
    val historyList = remember {
        listOf(
            EnhancedScanHistory(
                id = 1,
                diseaseName = "Northern Leaf Blight",
                confidence = 0.94f,
                isHealthy = false,
                timestamp = System.currentTimeMillis() - 3600000,
                location = "Jawa Timur",
                weather = "Cerah",
                imageUri = null
            ),
            EnhancedScanHistory(
                id = 2,
                diseaseName = "Healthy Leaf",
                confidence = 0.97f,
                isHealthy = true,
                timestamp = System.currentTimeMillis() - 86400000,
                location = "Jawa Tengah",
                weather = "Mendung",
                imageUri = null
            )
        )
    }

    var showFilters by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDateRange by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Long>()) }

    val filters = listOf("Semua", "Sehat", "Penyakit", "7 Hari", "30 Hari")

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchVisible) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari...", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Surface,
                                unfocusedContainerColor = Surface
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
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
                    IconButton(onClick = {
                        if (isSearchVisible) {
                            isSearchVisible = false
                            searchQuery = ""
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            if (isSearchVisible) Icons.Default.ArrowBack else Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (!isSearchVisible) {
                        IconButton(onClick = { isSearchVisible = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = GreenPrimary)
                        }

                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (showFilters) GreenPrimary else TextSecondary
                            )
                        }

                        IconButton(onClick = onExportData) {
                            Icon(Icons.Default.Download, contentDescription = "Export", tint = GreenPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Chips
            if (showFilters) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GreenPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                item {
                    // Date Range Picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pilih Tanggal", fontSize = 12.sp)
                        }

                        if (selectedItems.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { showBulkDeleteDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hapus (${selectedItems.size})", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Stats Summary
            item {
                HistoryStatsCard(historyList = historyList)
            }

            // Scan List
            items(historyList) { history ->
                SelectableHistoryCard(
                    history = history,
                    isSelected = history.id in selectedItems,
                    onToggleSelect = {
                        selectedItems = if (history.id in selectedItems) {
                            selectedItems - history.id
                        } else {
                            selectedItems + history.id
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Bulk Delete Dialog
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { Text("Hapus ${selectedItems.size} Scan?", fontWeight = FontWeight.Bold) },
            text = { Text("Riwayat scan yang dipilih akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    showBulkDeleteDialog = false
                    onDeleteAll()
                }) {
                    Text("Hapus", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

data class EnhancedScanHistory(
    val id: Long,
    val diseaseName: String,
    val confidence: Float,
    val isHealthy: Boolean,
    val timestamp: Long,
    val location: String,
    val weather: String,
    val imageUri: String?
)

@Composable
private fun HistoryStatsCard(historyList: List<EnhancedScanHistory>) {
    val healthyCount = historyList.count { it.isHealthy }
    val diseaseCount = historyList.size - healthyCount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = historyList.size.toString(), label = "Total", color = GreenPrimary)
            StatItem(value = healthyCount.toString(), label = "Sehat", color = Success)
            StatItem(value = diseaseCount.toString(), label = "Penyakit", color = Error)
            StatItem(value = "85%", label = "Avg Conf", color = GoldPrimary)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun SelectableHistoryCard(
    history: EnhancedScanHistory,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) BorderStroke(2.dp, GreenPrimary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (history.isHealthy) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (history.isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (history.isHealthy) Success else Error,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = history.diseaseName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    if (history.isHealthy) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "✓", color = Success)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Text(text = history.location, fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Text(text = history.weather, fontSize = 12.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTimestamp(history.timestamp),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Confidence
            Column(horizontalAlignment = Alignment.End) {
                ConfidenceBadge(confidence = history.confidence)
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(isHealthy = history.isHealthy)
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}

@Composable
fun ConfidenceBadge(confidence: Float) {
    val color = when {
        confidence >= 0.9f -> Success
        confidence >= 0.7f -> GoldPrimary
        else -> Error
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = "${(confidence * 100).toInt()}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatusBadge(isHealthy: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isHealthy) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
    ) {
        Text(
            text = if (isHealthy) "Sehat" else "Penyakit",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isHealthy) Success else Error,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}