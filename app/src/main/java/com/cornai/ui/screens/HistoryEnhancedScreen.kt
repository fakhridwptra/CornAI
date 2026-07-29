// Composable UI untuk satu halaman tampilan aplikasi.
// File: java\com\cornai\ui\screens\HistoryEnhancedScreen.kt

package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.data.model.ScanHistory
import com.cornai.ui.theme.*
import com.cornai.ui.components.*
import androidx.compose.ui.graphics.graphicsLayer
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryEnhancedScreen(
    onBack: () -> Unit,
    historyList: List<ScanHistory>,
    onExportData: () -> Unit = {},
    onDeleteItems: (List<String>) -> Unit = {}
) {
    var showFilters by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    var viewMode by remember { mutableStateOf("list") } // "list" or "timeline"
    
    var isLocalLoading by remember { mutableStateOf(true) }
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        isLocalLoading = false
        isAnimated = true
    }

    val filters = listOf("Semua", "Sehat", "Penyakit", "7 Hari", "30 Hari")

    val filteredList = remember(historyList, searchQuery, selectedFilter) {
        historyList.filter { item ->
            val matchesQuery = item.displayName.contains(searchQuery, ignoreCase = true) ||
                    item.diseaseName.contains(searchQuery, ignoreCase = true)
            val matchesChip = when (selectedFilter) {
                "Sehat" -> item.isHealthy
                "Penyakit" -> !item.isHealthy
                "7 Hari" -> item.timestamp >= System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
                "30 Hari" -> item.timestamp >= System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                else -> true
            }
            matchesQuery && matchesChip
        }
    }

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
                        Text("Riwayat Scan Detail", fontWeight = FontWeight.Bold)
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
                            imageVector = Icons.Default.ArrowBack,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
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

                if (selectedItems.isNotEmpty()) {
                    item {
                        Button(
                            onClick = { showBulkDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus Terpilih (${selectedItems.size})", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }

            // Stats Summary
            if (isLocalLoading) {
                item {
                    ShimmerStats()
                }
                items(5) {
                    ShimmerListItem()
                }
            } else {
                item {
                    val animAlpha by animateFloatAsState(
                        targetValue = if (isAnimated) 1f else 0f,
                        animationSpec = tween(durationMillis = 500, delayMillis = 0, easing = EaseOutQuad),
                        label = "statsAlpha"
                    )
                    val animTranslationY by animateFloatAsState(
                        targetValue = if (isAnimated) 0f else 60f,
                        animationSpec = tween(durationMillis = 500, delayMillis = 0, easing = EaseOutQuad),
                        label = "statsTranslate"
                    )
                    Box(modifier = Modifier.graphicsLayer { alpha = animAlpha; translationY = animTranslationY }) {
                        HistoryStatsCard(historyList = historyList)
                    }
                }

                // View Mode Toggle (Card vs Timeline)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (viewMode == "list") GreenPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clickable { viewMode = "list" }
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "Tampilan Kartu",
                                        color = if (viewMode == "list") Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (viewMode == "timeline") GreenPrimary else Color.Transparent,
                                    modifier = Modifier
                                        .clickable { viewMode = "timeline" }
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = "Alur Waktu (Timeline)",
                                        color = if (viewMode == "timeline") Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Empty State
                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryToggleOff,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedFilter != "Semua") "Tidak ada hasil pencarian" else "Belum ada riwayat scan",
                                color = TextSecondary,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    if (viewMode == "list") {
                        // Scan List
                        itemsIndexed(filteredList, key = { _, item -> item.id }) { index, history ->
                            val animAlpha by animateFloatAsState(
                                targetValue = if (isAnimated) 1f else 0f,
                                animationSpec = tween(durationMillis = 500, delayMillis = (index + 1) * 60, easing = EaseOutQuad),
                                label = "alpha"
                            )
                            val animTranslationY by animateFloatAsState(
                                targetValue = if (isAnimated) 0f else 60f,
                                animationSpec = tween(durationMillis = 500, delayMillis = (index + 1) * 60, easing = EaseOutQuad),
                                label = "translate"
                            )
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    alpha = animAlpha
                                    translationY = animTranslationY
                                }
                            ) {
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
                        }
                    } else {
                        // Timeline View
                        itemsIndexed(filteredList, key = { _, item -> item.id }) { index, history ->
                            val animAlpha by animateFloatAsState(
                                targetValue = if (isAnimated) 1f else 0f,
                                animationSpec = tween(durationMillis = 500, delayMillis = (index + 1) * 60, easing = EaseOutQuad),
                                label = "alpha"
                            )
                            val animTranslationY by animateFloatAsState(
                                targetValue = if (isAnimated) 0f else 60f,
                                animationSpec = tween(durationMillis = 500, delayMillis = (index + 1) * 60, easing = EaseOutQuad),
                                label = "translate"
                            )
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    alpha = animAlpha
                                    translationY = animTranslationY
                                }
                            ) {
                                TimelineHistoryCard(
                                    history = history,
                                    isFirst = index == 0,
                                    isLast = index == filteredList.lastIndex
                                )
                            }
                        }
                    }
                }
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
            text = { Text("Riwayat scan yang dipilih akan dihapus secara permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    showBulkDeleteDialog = false
                    onDeleteItems(selectedItems.toList())
                    selectedItems = emptySet()
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

@Composable
private fun HistoryStatsCard(historyList: List<ScanHistory>) {
    val total = historyList.size
    val healthyCount = historyList.count { it.isHealthy }
    val diseaseCount = total - healthyCount
    val avgConfidenceVal = if (historyList.isNotEmpty()) historyList.map { it.confidence }.average().toFloat() else 0f
    val avgConfidence = "${(avgConfidenceVal * 100).toInt()}%"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Doughnut Chart Section (Left)
            Box(
                modifier = Modifier
                    .size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedSweepHealthy = remember { Animatable(0f) }
                val animatedSweepDisease = remember { Animatable(0f) }

                LaunchedEffect(healthyCount, diseaseCount) {
                    if (total > 0) {
                        val healthyRatio = healthyCount.toFloat() / total.toFloat()
                        val diseaseRatio = diseaseCount.toFloat() / total.toFloat()
                        animatedSweepHealthy.animateTo(
                            targetValue = healthyRatio * 360f,
                            animationSpec = tween(1000, easing = EaseInOutQuad)
                        )
                        animatedSweepDisease.animateTo(
                            targetValue = diseaseRatio * 360f,
                            animationSpec = tween(1000, easing = EaseInOutQuad)
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    // Background Ring (Placeholder)
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                    )

                    if (total > 0) {
                        // Draw Healthy (Success) segment
                        drawArc(
                            color = Success,
                            startAngle = -90f,
                            sweepAngle = animatedSweepHealthy.value,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )

                        // Draw Disease (Error) segment
                        drawArc(
                            color = Error,
                            startAngle = -90f + animatedSweepHealthy.value,
                            sweepAngle = animatedSweepDisease.value,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = total.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Scan",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Info & Legend Column (Right)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Akurasi & Distribusi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                // Rata-rata Confidence Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Rata-rata Akurasi", fontSize = 13.sp, color = TextPrimary)
                    }
                    Text(
                        text = avgConfidence,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }

                Divider(color = TextSecondary.copy(alpha = 0.15f), thickness = 0.5.dp)

                // Healthy Legend Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Success)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Daun Sehat", fontSize = 13.sp, color = TextPrimary)
                    }
                    Text(
                        text = "$healthyCount (${if (total > 0) (healthyCount * 100 / total) else 0}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Success
                    )
                }

                // Disease Legend Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Error)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Daun Sakit", fontSize = 13.sp, color = TextPrimary)
                    }
                    Text(
                        text = "$diseaseCount (${if (total > 0) (diseaseCount * 100 / total) else 0}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Error
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectableHistoryCard(
    history: ScanHistory,
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
                        text = history.displayName.ifEmpty { history.diseaseName },
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
                    Text(text = history.location.ifEmpty { "Lokal" }, fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Text(text = history.weather.ifEmpty { "Cerah" }, fontSize = 12.sp, color = TextSecondary)
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

@Composable
private fun TimelineHistoryCard(
    history: ScanHistory,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left timeline line & node indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Top line segment
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(16.dp)
                    .background(if (isFirst) Color.Transparent else GreenPrimary.copy(alpha = 0.5f))
            )
            
            // Timeline Node Circle
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (history.isHealthy) Success else Error)
                    .border(2.dp, Color.White, CircleShape)
            )
            
            // Bottom line segment
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(80.dp)
                    .background(if (isLast) Color.Transparent else GreenPrimary.copy(alpha = 0.5f))
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Card content on the right
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = history.displayName.ifEmpty { history.diseaseName },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatTimestamp(history.timestamp),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                ConfidenceBadge(confidence = history.confidence)
            }
        }
    }
}
