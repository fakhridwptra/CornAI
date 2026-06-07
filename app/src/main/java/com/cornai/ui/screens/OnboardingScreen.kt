package com.cornai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.cornai.ui.components.GradientButton
import com.cornai.ui.theme.*

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.CameraAlt,
            title = "Pindai Daun Jagung",
            description = "Arahkan kamera ke daun atau tongkol jagung yang ingin diperiksa. Pastikan pencahayaan cukup.",
            backgroundColor = GreenDark
        ),
        OnboardingPage(
            icon = Icons.Default.Analytics,
            title = "Lihat Hasil Deteksi",
            description = "Dapatkan hasil deteksi penyakit dalam hitungan detik dengan akurasi AI hingga 96%.",
            backgroundColor = EmeraldDark
        ),
        OnboardingPage(
            icon = Icons.Default.Healing,
            title = "Atasi Penyakit",
            description = "Ikuti rekomendasi penanganan untuk menyembuhkan tanaman jagung Anda.",
            backgroundColor = GoldDark
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pages[currentPage].backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onFinish,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Lewati", fontSize = 14.sp)
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OnboardingPageContent(page = pages[currentPage])
            }

            // Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { index, _ ->
                    val isSelected = currentPage == index
                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.4f,
                        animationSpec = tween(300),
                        label = "indicator"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = alpha))
                            .clickable { currentPage = index }
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                if (currentPage > 0) {
                    IconButton(
                        onClick = { currentPage-- },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Next/Done button
                if (currentPage == pages.size - 1) {
                    GradientButton(
                        text = "Mulai",
                        onClick = onFinish,
                        modifier = Modifier.width(160.dp)
                    )
                } else {
                    GradientButton(
                        text = "Lanjut",
                        onClick = { currentPage++ },
                        modifier = Modifier.width(160.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "onboard")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}
