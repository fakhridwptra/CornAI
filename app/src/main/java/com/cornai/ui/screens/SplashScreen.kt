// Composable UI untuk satu halaman tampilan aplikasi.
// File: java\com\cornai\ui\screens\SplashScreen.kt

package com.cornai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornai.R
import com.cornai.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit = { onNavigateToHome() },
    isLoggedIn: Boolean = false,
    hasSeenOnboarding: Boolean = true
) {
    // One-shot enter animation state
    var entered by remember { mutableStateOf(false) }

    val currentIsLoggedIn by rememberUpdatedState(isLoggedIn)
    val currentHasSeenOnboarding by rememberUpdatedState(hasSeenOnboarding)
    val currentOnNavigateToHome by rememberUpdatedState(onNavigateToHome)
    val currentOnNavigateToOnboarding by rememberUpdatedState(onNavigateToOnboarding)
    val currentOnNavigateToLogin by rememberUpdatedState(onNavigateToLogin)

    android.util.Log.d("CornAI_Splash", "SplashScreen composed. isLoggedIn=$isLoggedIn, hasSeenOnboarding=$hasSeenOnboarding, entered=$entered")

    LaunchedEffect(Unit) {
        android.util.Log.d("CornAI_Splash", "SplashScreen LaunchedEffect started")
        delay(100) // small delay before starting animations
        entered = true
        android.util.Log.d("CornAI_Splash", "SplashScreen entered set to true")
        delay(3000)
        android.util.Log.d("CornAI_Splash", "SplashScreen delay finished. Navigating... isLoggedIn=$currentIsLoggedIn, hasSeenOnboarding=$currentHasSeenOnboarding")
        when {
            !currentHasSeenOnboarding -> {
                android.util.Log.d("CornAI_Splash", "Navigating to Onboarding")
                currentOnNavigateToOnboarding()
            }
            !currentIsLoggedIn -> {
                android.util.Log.d("CornAI_Splash", "Navigating to Login")
                currentOnNavigateToLogin()
            }
            else -> {
                android.util.Log.d("CornAI_Splash", "Navigating to Home")
                currentOnNavigateToHome()
            }
        }
    }

    // --- One-shot enter animations ---
    val logoScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutQuad),
        label = "logoAlpha"
    )
    val titleTranslateY by animateFloatAsState(
        targetValue = if (entered) 0f else 60f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = EaseOutQuad),
        label = "titleSlide"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = EaseOutQuad),
        label = "titleAlpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 600, easing = EaseOutQuad),
        label = "subtitleAlpha"
    )
    val progressAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 800, easing = EaseOutQuad),
        label = "progressAlpha"
    )
    val loadingProgress by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, delayMillis = 900, easing = LinearEasing),
        label = "loadingBar"
    )

    // --- Infinite ambient animations ---
    val infiniteTransition = rememberInfiniteTransition(label = "splash_ambient")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutQuad), RepeatMode.Reverse),
        label = "glow"
    )
    val outerRingScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutQuad), RepeatMode.Reverse),
        label = "outerRing"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(GreenPrimary, GreenDark, Color(0xFF0D2018)),
                    radius = 1800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo with enter animation
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ambient glow ring
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(outerRingScale)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = glowAlpha * 0.5f))
                )
                // Mid ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = glowAlpha * 0.15f))
                )
                // Logo image
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_cornai),
                    contentDescription = "Corn AI Logo",
                    modifier = Modifier
                        .size(108.dp)
                        .scale(logoScale)
                        .graphicsLayer { alpha = logoAlpha }
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title with slide-up animation
            Text(
                text = "Corn AI",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha
                    translationY = titleTranslateY
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with fade-in
            Text(
                text = "Deteksi Penyakit Jagung dengan AI",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Accuracy badge
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                color = GoldPrimary.copy(alpha = 0.2f),
                modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }
            ) {
                Text(
                    text = "✦ Akurasi 96%  •  10 Kelas Deteksi ✦",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = androidx.compose.ui.Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Animated loading progress bar
            Column(
                modifier = Modifier.graphicsLayer { alpha = progressAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(4.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(loadingProgress)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GoldPrimary.copy(alpha = 0.8f), GoldPrimary)
                                )
                            )
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Memuat model AI...",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
