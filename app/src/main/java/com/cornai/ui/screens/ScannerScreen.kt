package com.cornai.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.cornai.ui.components.GradientButton
import com.cornai.ui.components.ScanningIndicator
import com.cornai.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onResultReady: (diseaseName: String, confidence: Float, isHealthy: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var isScanning by remember { mutableStateOf(false) }
    var isGalleryMode by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            isGalleryMode = true
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            !cameraPermissionState.status.isGranted -> {
                PermissionRequest(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                    onBack = onBack
                )
            }
            isGalleryMode && selectedImageUri != null -> {
                GalleryPreview(
                    uri = selectedImageUri!!,
                    isScanning = isScanning,
                    onScanStart = {
                        isScanning = true
                        simulateDetection(context, onResultReady) { scanning ->
                            isScanning = scanning
                        }
                    },
                    onBack = {
                        selectedImageUri = null
                        isGalleryMode = false
                    }
                )
            }
            else -> {
                CameraPreview(
                    isScanning = isScanning,
                    onScanStart = {
                        isScanning = true
                        simulateDetection(context, onResultReady) { scanning ->
                            isScanning = scanning
                        }
                    },
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun PermissionRequest(
    onRequestPermission: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GreenDark, Background)
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Izinkan Akses Kamera",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Untuk memindai penyakit jagung, kami memerlukan akses kamera.",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        GradientButton(
            text = "Berikan Izin",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text(
                text = "Kembali",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CameraPreview(
    isScanning: Boolean,
    onScanStart: () -> Unit,
    onGalleryClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    val previewView = remember { PreviewView(context) }

    // Camera setup
    DisposableEffect(cameraSelector, isFlashEnabled) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(
                if (isFlashEnabled) ImageCapture.FLASH_MODE_ON
                else ImageCapture.FLASH_MODE_OFF
            )
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            // Handle camera binding error
        }

        onDispose {
            cameraProvider.unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay gradient (top)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Animated Scanning Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            AnimatedScanningFrame(isScanning = isScanning)
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Text(
                text = if (isScanning) "Memindai..." else "Arahkan ke Daun/Tongkol",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // Flash & Flip buttons
            Row {
                IconButton(
                    onClick = { isFlashEnabled = !isFlashEnabled },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFlashEnabled) GoldPrimary.copy(alpha = 0.8f)
                            else Color.Black.copy(alpha = 0.5f)
                        )
                ) {
                    Icon(
                        imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashEnabled) Color.Black else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isScanning) {
                // Hint text
                Text(
                    text = "Posisikan daun atau tongkol jagung",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "di dalam bingkai pemindaian",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Scan button
                GradientButton(
                    text = "Mulai Pemindaian",
                    onClick = onScanStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Gallery button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Atau pilih dari Galeri",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Scanning indicator text
                Text(
                    text = "Menganalisis...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AnimatedScanningFrame(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "frame")

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isScanning) {
            // Corner brackets
            val cornerLength = 40.dp
            val strokeWidth = 3.dp

            // Top-left corner
            Box(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .width(cornerLength)
                        .height(strokeWidth)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
                Box(
                    modifier = Modifier
                        .width(strokeWidth)
                        .height(cornerLength)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
            }

            // Top-right corner
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(cornerLength)
                        .height(strokeWidth)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(strokeWidth)
                        .height(cornerLength)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
            }

            // Bottom-left corner
            Box(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .width(cornerLength)
                        .height(strokeWidth)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .width(strokeWidth)
                        .height(cornerLength)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
            }

            // Bottom-right corner
            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(cornerLength)
                        .height(strokeWidth)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(strokeWidth)
                        .height(cornerLength)
                        .background(GreenPrimary.copy(alpha = animatedAlpha))
                )
            }

            // Scanning line animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopStart)
                    .padding(top = (240 * animatedOffset).dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GoldPrimary.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
        } else {
            // Scanning indicator when scanning
            ScanningIndicator(size = 260.dp)
        }
    }
}

@Composable
private fun GalleryPreview(
    uri: Uri,
    isScanning: Boolean,
    onScanStart: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Image preview placeholder (using colored background as demo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GreenDark.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(100.dp)
            )
        }

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            Text(
                text = "Pratinjau Gambar",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.size(44.dp))
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isScanning) {
                Text(
                    text = "Gambar siap dianalisis",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                GradientButton(
                    text = "Analisis Gambar",
                    onClick = onScanStart,
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            } else {
                Text(
                    text = "Menganalisis...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun simulateDetection(
    context: Context,
    onResultReady: (String, Float, Boolean) -> Unit,
    updateScanning: (Boolean) -> Unit
) {
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        val results = listOf(
            Triple("Northern Leaf Blight", 0.94f, false),
            Triple("Common Rust", 0.89f, false),
            Triple("Healthy", 0.97f, true),
            Triple("Gray Leaf Spot", 0.91f, false)
        )
        val result = results.random()

        updateScanning(false)
        onResultReady(result.first, result.second, result.third)
    }, 2500)
}
