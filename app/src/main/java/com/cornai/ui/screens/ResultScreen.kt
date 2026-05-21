package com.cornai.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.ui.components.*
import com.cornai.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class DiseaseInfo(
    val name: String,
    val symptoms: List<String>,
    val causes: String,
    val treatment: List<TreatmentStep>,
    val prevention: List<String>
)

data class TreatmentStep(
    val step: Int,
    val title: String,
    val description: String,
    val icon: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    diseaseName: String,
    confidence: Float,
    isHealthy: Boolean,
    onScanAgain: () -> Unit,
    onBackToHome: () -> Unit,
    onViewDetail: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val diseaseInfo = getDiseaseInfo(diseaseName)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Hasil Diagnosis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Hasil diagnosis Corn AI:\n" +
                                    "Penyakit: $diseaseName\n" +
                                    "Kepercayaan: ${(confidence * 100).toInt()}%\n\n" +
                                    "Download aplikasi Corn AI sekarang!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Hasil"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Result Header Card
            ResultHeaderCard(
                diseaseName = diseaseName,
                confidence = confidence,
                isHealthy = isHealthy
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Confidence Score Section
            ConfidenceSection(confidence = confidence)

            Spacer(modifier = Modifier.height(20.dp))

            // Disease Details
            if (!isHealthy && diseaseInfo != null) {
                DiseaseDetailsSection(diseaseInfo = diseaseInfo)
            } else if (isHealthy) {
                HealthyTipsSection()
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            ActionButtonsSection(
                onScanAgain = onScanAgain,
                onBackToHome = onBackToHome,
                onViewDetail = onViewDetail
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ResultHeaderCard(
    diseaseName: String,
    confidence: Float,
    isHealthy: Boolean
) {
    val backgroundColor = if (isHealthy) HealthyGreen else DiseaseRed
    val infiniteTransition = rememberInfiniteTransition(label = "header")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = if (isHealthy) {
                            listOf(HealthyGreen, GreenPrimary)
                        } else {
                            listOf(DiseaseRed, Color(0xFFC62828))
                        }
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Icon
                val iconScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = EaseInOutQuad),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "iconScale"
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(iconScale)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isHealthy) "Jagung Sehat!" else "Penyakit Terdeteksi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = diseaseName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confidence Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Kepercayaan: ${(confidence * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfidenceSection(confidence: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Tingkat Kepercayaan AI",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Progress
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularScoreIndicator(confidence = confidence)
                    Text(
                        text = "${(confidence * 100).toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            confidence >= 0.85f -> Success
                            confidence >= 0.70f -> Warning
                            else -> Error
                        }
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val status = when {
                        confidence >= 0.85f -> "Sangat Yakin"
                        confidence >= 0.70f -> "Cukup Yakin"
                        else -> "Kurang Yakin"
                    }

                    val statusColor = when {
                        confidence >= 0.85f -> Success
                        confidence >= 0.70f -> Warning
                        else -> Error
                    }

                    Text(
                        text = status,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            confidence >= 0.85f -> "Hasil deteksi sangat dapat diandalkan"
                            confidence >= 0.70f -> "Hasil deteksi cukup akurat, perlu konfirmasi manual"
                            else -> "Deteksi tidak pasti, sebaiknya scan ulang"
                        },
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularScoreIndicator(confidence: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = confidence,
        animationSpec = tween(1500, easing = EaseOutQuad),
        label = "progress"
    )

    val color = when {
        confidence >= 0.85f -> Success
        confidence >= 0.70f -> Warning
        else -> Error
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 10.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

@Composable
private fun DiseaseDetailsSection(diseaseInfo: DiseaseInfo) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Symptoms Card
        DetailCard(
            title = "Gejala",
            icon = Icons.Default.Visibility,
            content = {
                diseaseInfo.symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = symptom,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Causes Card
        DetailCard(
            title = "Penyebab",
            icon = Icons.Default.BugReport,
            content = {
                Text(
                    text = diseaseInfo.causes,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Treatment Card
        DetailCard(
            title = "Langkah Penanganan",
            icon = Icons.Default.Healing,
            content = {
                diseaseInfo.treatment.forEach { step ->
                    TreatmentStepItem(step = step)
                    if (step != diseaseInfo.treatment.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prevention Card
        DetailCard(
            title = "Pencegahan",
            icon = Icons.Default.Shield,
            content = {
                diseaseInfo.prevention.forEach { prevention ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = prevention,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun HealthyTipsSection() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
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
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Kabar Baik!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tanaman jagung Anda dalam kondisi sehat. Tetap pertahankan kondisi ini dengan perawatan yang baik.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(title = "Tips Menjaga Kesehatan")

                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    "Lanjutkan penyiraman teratur",
                    "Pastikan mendapat sinar matahari cukup",
                    "Beri pupuk secara berkala",
                    "Perhatikan tanda-tanda awal penyakit"
                ).forEach { tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = tip,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun TreatmentStepItem(step: TreatmentStep) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(GoldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.step.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onScanAgain: () -> Unit,
    onBackToHome: () -> Unit,
    onViewDetail: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        GradientButton(
            text = "Scan Ulang",
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth(),
            icon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Detail Button
        OutlinedButton(
            onClick = onViewDetail,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = GreenPrimary.copy(alpha = 0.1f),
                contentColor = GreenPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lihat Detail Lengkap",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = GreenPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kembali ke Beranda",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getDiseaseInfo(diseaseName: String): DiseaseInfo? {
    return when (diseaseName) {
        "Northern Leaf Blight" -> DiseaseInfo(
            name = "Hawar Daun Utara",
            symptoms = listOf(
                "Bercak灰色 berbentuk oval memanjang",
                "Lesi dimulai dari daun bawah",
                "Bercak dapat mencapai 2-15 cm",
                "Daun menguning dan mati prematur"
            ),
            causes = "Disebabkan oleh jamur Exserohilum turcicum. Jamur bertahan di residu tanaman dan menyebar melalui spora yang terbawa angin.",
            treatment = listOf(
                TreatmentStep(1, "Aplikasi Fungisida", "Semprotkan fungisida berbasis mancozeb atau azoxystrobin sesuai dosis yang dianjurkan", "spray"),
                TreatmentStep(2, "Pembersihan Sisa Tanaman", "Buang dan musnahkan sisa tanaman yang terinfeksi untuk mengurangi sumber penyakit", "trash"),
                TreatmentStep(3, "Rotasi Tanaman", "Lakukan rotasi tanaman dengan tanaman non-jagung selama minimal 2 musim", "sync")
            ),
            prevention = listOf(
                "Pilihvarietas tahan hawar daun",
                "Terapkan jarak tanam yang tepat",
                "Buang sisa tanaman setelah panen",
                "Pastikan drainase tanah baik"
            )
        )
        "Common Rust" -> DiseaseInfo(
            name = "Karat Biasa",
            symptoms = listOf(
                "Bintik-bintik coklat/orange kecil di kedua permukaan daun",
                "Bintik membesar dan berubah warna coklat gelap",
                "Daun menguning lebih awal",
                "Tanaman terlihat kerdil"
            ),
            causes = "Disebabkan oleh jamur Puccinia sorghi. Spora jamur menyebar melalui angin pada cuaca hangat dan lembab.",
            treatment = listOf(
                TreatmentStep(1, "Aplikasi Fungisida", "Gunakan fungisida sistemik seperti propiconazole atau triademefon", "spray"),
                TreatmentStep(2, "Perbaikan Drainase", "Pastikan drainase baik untuk mengurangi kelembaban", "water"),
                TreatmentStep(3, "Penyerdehanaan Jarak Tanam", "Tingkatkan jarak tanam untuk sirkulasi udara lebih baik", "expand")
            ),
            prevention = listOf(
                "Gunakanvarietas tahan karat",
                "Hindari penanaman terlalu rapat",
                "Buang daun yang terinfeksi berat",
                "Semprot fungisida preventif saat gejala pertama muncul"
            )
        )
        "Gray Leaf Spot" -> DiseaseInfo(
            name = "Bercak Daun Abu",
            symptoms = listOf(
                "Bercak kecil berwarna coklat kemerahan",
                "Bercak berkembang menjadi abu-abu rectangular",
                "Pelepah daun membusuk",
                "Penurunan hasil yang signifikan"
            ),
            causes = "Disebabkan oleh jamur Cercospora zeae. Jamur bertahan di residu tanaman dan berkembang pada kondisi lembab tinggi.",
            treatment = listOf(
                TreatmentStep(1, "Aplikasi Strobilurin", "Gunakan fungisida golongan strobilurin untuk hasil terbaik", "spray"),
                TreatmentStep(2, "Pengurangan Residu", "Masukkan residu tanaman ke dalam tanah segera setelah panen", "tools"),
                TreatmentStep(3, "Pengairan Teratur", "Atur pengairan untuk menghindari genangan air berlebih", "water")
            ),
            prevention = listOf(
                "Gunakanvarietas tahan penyakit",
                "Praktikkan crop rotation",
                "Bakar atau kubur sisa tanaman",
                "Hindari penanaman berulang di lahan yang sama"
            )
        )
        "Common Smut" -> DiseaseInfo(
            name = "Busuk Smut",
            symptoms = listOf(
                "Benjolan/gall pada tongkol, tangkai, atau daun",
                "Gall awalnya berwarna putih keabu-abuan",
                "Gall matang berubah menjadi massa spora hitam",
                "Tongkol menjadi tidak produktif"
            ),
            causes = "Disebabkan oleh jamur Ustilago maydis. Infeksi terjadi melalui luka pada jaringan tanaman.",
            treatment = listOf(
                TreatmentStep(1, "Pengangkatan Gall", "Cabut dan musnahkan gall sebelum matang dan pecah", "hand"),
                TreatmentStep(2, "Pengelolaan Luka", "Hindari melukai tanaman saat penyiangan atau pengolahan tanah", "bandage"),
                TreatmentStep(3, "Tidak Ada Fungisida", "Smut umum nya tidak dapat dikendalikan dengan fungisida, fokus pada pencegahan", "info")
            ),
            prevention = listOf(
                "Gunakanbenih bersertifikat",
                "Hindari melukai tanaman",
                "Kontrol hama yang menyebabkan luka",
                "Gunakanvarietas toleran"
            )
        )
        else -> DiseaseInfo(
            name = diseaseName,
            symptoms = listOf(
                "Gejala yang terlihat pada tanaman",
                "Perubahan warna pada daun",
                "Pembusukan pada jaringan"
            ),
            causes = "Penyebab spesifik masih dalam penelitian.",
            treatment = listOf(
                TreatmentStep(1, "Konsultasi", "Hubungi penyuluh pertanian setempat untuk informasi lebih lanjut", "phone"),
                TreatmentStep(2, "Dokumentasi", "Ambil foto dan catat lokasi untuk referensi selanjutnya", "camera")
            ),
            prevention = listOf(
                "Monitor tanaman secara teratur",
                "Catat perubahan yang terjadi",
                "Konsultasi dengan ahli"
            )
        )
    }
}
