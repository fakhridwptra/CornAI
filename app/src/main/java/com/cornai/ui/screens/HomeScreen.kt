package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.CornIcon
import com.cornai.ui.components.GradientButton
import com.cornai.ui.theme.*
import com.cornai.ui.viewmodel.WeatherData
import kotlinx.coroutines.launch

data class QuickFeature(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onRefreshWeather: () -> Unit = {},
    userName: String = "Guest",
    totalScans: Int = 0,
    healthyScans: Int = 0,
    diseaseScans: Int = 0,
    isGuest: Boolean = false,
    weatherState: WeatherData = WeatherData("Cerah", 32, "Ideal untuk menyemrot pestisida", "🌤️", "Siang")
) {
    android.util.Log.d("CornAI_Home", "HomeScreen composed. userName=$userName, isGuest=$isGuest, weatherState=${weatherState.condition}")
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "home")
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        android.util.Log.d("CornAI_Home", "HomeScreen LaunchedEffect started")
        isAnimated = true
    }

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val features = listOf(
        QuickFeature(Icons.Default.CameraAlt, "Scan Kamera", "Deteksi langsung"),
        QuickFeature(Icons.Default.PhotoLibrary, "Galeri", "Upload dari foto"),
        QuickFeature(Icons.Default.History, "Riwayat", "Lihat hasil scan")
    )

    Scaffold(
        containerColor = Background,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Corn AI",
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                            if (isGuest) {
                                Text(
                                    text = "Mode Tamu",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onHistoryClick) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = GreenPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(GreenPrimary.copy(alpha = 0.12f))
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val wAlpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 0, EaseOutQuad))
            val wTranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 0, EaseOutQuad))
            Box(modifier = Modifier.graphicsLayer { alpha = wAlpha; translationY = wTranslateY }) {
                WeatherWidget(weather = weatherState, onRefresh = onRefreshWeather)
            }

            val hAlpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 80, EaseOutQuad))
            val hTranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 80, EaseOutQuad))
            Box(modifier = Modifier.graphicsLayer { alpha = hAlpha; translationY = hTranslateY }) {
                HeroSection(pulseScale = pulseScale)
            }

            val dAlpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 160, EaseOutQuad))
            val dTranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 160, EaseOutQuad))
            Box(modifier = Modifier.graphicsLayer { alpha = dAlpha; translationY = dTranslateY }) {
                DailyTipsCard()
            }

            Spacer(modifier = Modifier.height(16.dp))

            val sAlpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 240, EaseOutQuad))
            val sTranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 240, EaseOutQuad))
            Box(modifier = Modifier.graphicsLayer { alpha = sAlpha; translationY = sTranslateY }) {
                HomeStatsRow(total = totalScans, healthy = healthyScans, disease = diseaseScans)
            }

            val bAlpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 320, EaseOutQuad))
            val bTranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 320, EaseOutQuad))
            Column(modifier = Modifier.graphicsLayer { alpha = bAlpha; translationY = bTranslateY }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    GradientButton(
                        text = "Mulai Scan Sekarang",
                        onClick = onScanClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(icon = Icons.Default.CameraAlt, title = "Kamera", onClick = onScanClick, modifier = Modifier.weight(1f))
                    QuickActionCard(icon = Icons.Default.PhotoLibrary, title = "Galeri", onClick = onGalleryClick, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionHeader(title = "Apa yang Bisa Dilakukan?")
                Spacer(modifier = Modifier.height(16.dp))
                val i1Alpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 400, EaseOutQuad))
                val i1TranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 400, EaseOutQuad))
                Box(modifier = Modifier.graphicsLayer { alpha = i1Alpha; translationY = i1TranslateY }) {
                    InfoCard(icon = Icons.Default.Search, title = "Deteksi Cepat", description = "Identifikasi penyakit dalam hitungan detik menggunakan kamera")
                }
                Spacer(modifier = Modifier.height(12.dp))
                val i2Alpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 480, EaseOutQuad))
                val i2TranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 480, EaseOutQuad))
                Box(modifier = Modifier.graphicsLayer { alpha = i2Alpha; translationY = i2TranslateY }) {
                    InfoCard(icon = Icons.Default.Healing, title = "Rekomendasi Penanganan", description = "Dapatkan solusi dan rekomendasi penanganan yang tepat")
                }
                Spacer(modifier = Modifier.height(12.dp))
                val i3Alpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 560, EaseOutQuad))
                val i3TranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 560, EaseOutQuad))
                Box(modifier = Modifier.graphicsLayer { alpha = i3Alpha; translationY = i3TranslateY }) {
                    InfoCard(icon = Icons.Default.OfflineBolt, title = "Offline Mode", description = "Bekerja tanpa koneksi internet, cocok untuk daerah pedesaan")
                }
                Spacer(modifier = Modifier.height(12.dp))
                val i4Alpha by animateFloatAsState(targetValue = if (isAnimated) 1f else 0f, animationSpec = tween(500, 640, EaseOutQuad))
                val i4TranslateY by animateFloatAsState(targetValue = if (isAnimated) 0f else 60f, animationSpec = tween(500, 640, EaseOutQuad))
                Box(modifier = Modifier.graphicsLayer { alpha = i4Alpha; translationY = i4TranslateY }) {
                    InfoCard(icon = Icons.Default.Summarize, title = "Riwayat Scan", description = "Simpan dan lihat kembali hasil deteksi sebelumnya")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionHeader(title = "Kelas Deteksi")
                Spacer(modifier = Modifier.height(12.dp))

                var searchQuery by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari kelas deteksi...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = TextSecondary.copy(alpha = 0.2f),
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

                val allClasses = listOf(
                    "Common Rust", "Northern Leaf Blight", "Gray Leaf Spot", 
                    "Common Smut", "Healthy Leaf", "Healthy Cob", 
                    "Asphalt Stain", "Cob Rot", "Eyespot", "Maize Streak"
                )
                val filteredClasses = allClasses.filter { it.contains(searchQuery, ignoreCase = true) }

                if (filteredClasses.isEmpty()) {
                    Text(
                        text = "Tidak ada kelas deteksi yang cocok",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column {
                        filteredClasses.chunked(2).forEach { rowClasses ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowClasses.forEach { className ->
                                    ClassChip(name = className, modifier = Modifier.weight(1f))
                                }
                                if (rowClasses.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun StatItem(value: Int, label: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                (scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn())
                    .togetherWith(scaleOut(animationSpec = tween(150)) + fadeOut())
            },
            label = "statChange"
        ) { targetValue ->
            Text(
                text = "$targetValue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
private fun HomeStatsRow(total: Int, healthy: Int, disease: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, GreenPrimary.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Surface, SurfaceVariant)))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = total, label = "Total", textColor = GreenPrimary)
            StatItem(value = healthy, label = "Sehat", textColor = Success)
            StatItem(value = disease, label = "Penyakit", textColor = Error)
        }
    }
}


@Composable
private fun HeroSection(pulseScale: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(colors = listOf(GreenDark.copy(alpha = 0.08f), Background)))
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(140.dp).scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(Brush.radialGradient(colors = listOf(GreenPrimary.copy(alpha = 0.2f), GreenPrimary.copy(alpha = 0f)))))
                CornIcon(size = 100.dp, primaryColor = GreenPrimary, accentColor = GoldPrimary)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Deteksi Penyakit Jagung", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dengan AI dalam genggaman Anda", fontSize = 16.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun WeatherWidget(weather: WeatherData, onRefresh: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnim")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiScale"
    )

    val darkTheme = MaterialTheme.colorScheme.background == DarkBackground
    val weatherGradients = remember(weather.condition, darkTheme) {
        if (darkTheme) {
            when {
                weather.condition.contains("Cerah", ignoreCase = true) || weather.emoji.contains("☀️") || weather.emoji.contains("🌤️") ->
                    listOf(Color(0xFF2E2516), Color(0xFF1D1811), Color(0xFF121614))
                weather.condition.contains("Hujan", ignoreCase = true) || weather.emoji.contains("🌧️") || weather.emoji.contains("⛈️") ->
                    listOf(Color(0xFF182430), Color(0xFF141D25), Color(0xFF121614))
                weather.condition.contains("Mendung", ignoreCase = true) || weather.emoji.contains("☁️") ->
                    listOf(Color(0xFF1E2225), Color(0xFF16191B), Color(0xFF121614))
                else ->
                    listOf(Color(0xFF13231B), Color(0xFF101B15), Color(0xFF121614))
            }
        } else {
            when {
                weather.condition.contains("Cerah", ignoreCase = true) || weather.emoji.contains("☀️") || weather.emoji.contains("🌤️") ->
                    listOf(Color(0xFFFFF9E6), Color(0xFFFFF3C0), Color(0xFFF6F4EF))
                weather.condition.contains("Hujan", ignoreCase = true) || weather.emoji.contains("🌧️") || weather.emoji.contains("⛈️") ->
                    listOf(Color(0xFFE8F0F7), Color(0xFFD0E4F0), Color(0xFFF6F4EF))
                weather.condition.contains("Mendung", ignoreCase = true) || weather.emoji.contains("☁️") ->
                    listOf(Color(0xFFEFF2F5), Color(0xFFDFE6ED), Color(0xFFF6F4EF))
                else ->
                    listOf(Color(0xFFE8F5EE), Color(0xFFD4EDE0), Color(0xFFF6F4EF))
            }
        }
    }

    // Full-width strip — no card, no border, blends into page
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        weatherGradients[0],
                        weatherGradients[1],
                        weatherGradients[2],
                        Background   // fade to page background at bottom
                    )
                )
            )
    ) {
        AnimatedContent(
            targetState = weather,
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500), initialOffsetY = { it }))
                    .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutVertically(animationSpec = tween(500), targetOffsetY = { -it }))
            },
            label = "weatherAnimation"
        ) { targetWeather ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .scale(emojiScale)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = if (darkTheme) 0.15f else 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(targetWeather.emoji, fontSize = 26.sp)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cuaca Hari Ini", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        "${targetWeather.condition}, ${targetWeather.temperature}°C",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(targetWeather.recommendation, fontSize = 11.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                rotation.snapTo(0f)
                                rotation.animateTo(
                                    targetValue = 360f,
                                    animationSpec = tween(600, easing = EaseInOutQuad)
                                )
                            }
                            onRefresh()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer { rotationZ = rotation.value }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Perbarui",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(targetWeather.timeOfDay, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}



@Composable
private fun DailyTipsCard() {
    val tips = listOf(
        "Pastikan daun jagung kering sebelum melakukan scan untuk hasil terbaik.",
        "Scan dilakukan pada pagi atau sore hari dengan pencahayaan alami.",
        "Periksa tanaman secara rutin untuk deteksi dini penyakit jagung.",
        "Posisikan daun jagung 15–20 cm dari kamera untuk fokus terbaik.",
        "Hindari bayangan berlebih pada daun jagung saat melakukan pemindaian."
    )
    var currentTipIndex by remember { mutableStateOf(0) }
    val currentTip = tips[currentTipIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable {
                currentTipIndex = (currentTipIndex + 1) % tips.size
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GoldPrimary.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
    ) {
        AnimatedContent(
            targetState = currentTip,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 }))
                    .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutVertically(animationSpec = tween(300), targetOffsetY = { -it / 2 }))
            },
            label = "tipAnimation"
        ) { tipText ->
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(GoldPrimary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldDark, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("💡 Tips Hari Ini (Ketuk untuk lainnya)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GoldDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tipText, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(icon: ImageVector, title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, GreenPrimary.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Surface, SurfaceVariant)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(GreenPrimary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, GreenPrimary.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(Surface, SurfaceVariant)))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(GreenPrimary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun DiseaseClassGrid() {
    val classes = listOf("Common Rust", "Northern Leaf Blight", "Gray Leaf Spot", "Common Smut", "Healthy Leaf", "Healthy Cob", "Asphalt Stain", "Cob Rot", "Eyespot", "Maize Streak")
    Column {
        classes.chunked(2).forEach { rowClasses ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowClasses.forEach { className ->
                    ClassChip(name = className, modifier = Modifier.weight(1f))
                }
                if (rowClasses.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ClassChip(name: String, modifier: Modifier = Modifier) {
    val isHealthy = name.contains("Healthy")
    val backgroundColor = if (isHealthy) GreenPrimary.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
    val textColor = if (isHealthy) GreenPrimary else Error
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(backgroundColor).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}