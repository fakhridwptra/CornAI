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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.CornIcon
import com.cornai.ui.components.GradientButton
import com.cornai.ui.theme.*

data class QuickFeature(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "home")

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
            TopAppBar(
                title = {
                    Text(
                        text = "Corn AI",
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = GreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather Widget
            WeatherWidget()

            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GreenDark.copy(alpha = 0.08f),
                                Background
                            )
                        )
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Corn Icon
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            GreenPrimary.copy(alpha = 0.2f),
                                            GreenPrimary.copy(alpha = 0f)
                                        )
                                    )
                                )
                        )
                        CornIcon(
                            size = 100.dp,
                            primaryColor = GreenPrimary,
                            accentColor = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Deteksi Penyakit Jagung",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dengan AI dalam genggaman Anda",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Tips Card
            DailyTipsCard()

            // Scan Button - Main CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                GradientButton(
                    text = "Mulai Scan Sekarang",
                    onClick = onScanClick,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Default.CameraAlt,
                    title = "Kamera",
                    onClick = onScanClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Galeri",
                    onClick = { /* TODO: Gallery picker */ },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Cards Section
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                SectionHeader(title = "Apa yang Bisa Dilakukan?")

                Spacer(modifier = Modifier.height(16.dp))

                InfoCard(
                    icon = Icons.Default.Search,
                    title = "Deteksi Cepat",
                    description = "Identifikasi penyakit dalam hitungan detik menggunakan kamera"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoCard(
                    icon = Icons.Default.Healing,
                    title = "Rekomendasi Penanganan",
                    description = "Dapatkan solusi dan rekomendasi penanganan yang tepat"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoCard(
                    icon = Icons.Default.OfflineBolt,
                    title = "Offline Mode",
                    description = "Bekerja tanpa koneksi internet, cocok untuk daerah pedesaan"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoCard(
                    icon = Icons.Default.Summarize,
                    title = "Riwayat Scan",
                    description = "Simpan dan lihat kembali hasil deteksi sebelumnya"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Disease Classes Preview
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                SectionHeader(title = "Kelas Deteksi")

                Spacer(modifier = Modifier.height(16.dp))

                DiseaseClassGrid()
            }

            Spacer(modifier = Modifier.height(100.dp)) // Bottom nav spacing
        }
    }
}

@Composable
private fun WeatherWidget() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weather Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF87CEEB).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cuaca Hari Ini",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Cerah, 32°C",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Idealnya untuk menyemrot pestisida",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "🌤️",
                    fontSize = 24.sp
                )
                Text(
                    text = "Siang",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DailyTipsCard() {
    val tips = listOf(
        "Pastikan daun jagung kering sebelum melakukan scan untuk hasil terbaik",
        "Scan dilakukan pada pagi atau sore hari dengan pencahayaan alami",
        "Periksa tanaman secara rutin untuk deteksi dini penyakit"
    )
    var currentTip by remember { mutableStateOf(tips.random()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GoldPrimary.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
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
                    .background(GoldPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "💡 Tips Hari Ini",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentTip,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DiseaseClassGrid() {
    val classes = listOf(
        "Common Rust", "Northern Leaf Blight", "Gray Leaf Spot",
        "Common Smut", "Healthy Leaf", "Healthy Cob",
        "Asphalt Stain", "Cob Rot", "Eyespot", "Maize Streak"
    )

    Column {
        classes.chunked(2).forEach { rowClasses ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowClasses.forEach { className ->
                    ClassChip(
                        name = className,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowClasses.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ClassChip(name: String, modifier: Modifier = Modifier) {
    val isHealthy = name.contains("Healthy")
    val backgroundColor = if (isHealthy) {
        GreenPrimary.copy(alpha = 0.1f)
    } else {
        Error.copy(alpha = 0.1f)
    }
    val textColor = if (isHealthy) GreenPrimary else Error

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
