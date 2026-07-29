// Composable UI untuk satu halaman tampilan aplikasi.
// File: java\com\cornai\ui\screens\ScannerScreen.kt

package com.cornai.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cornai.ui.components.GradientButton
import com.cornai.ui.components.ScanningIndicator
import com.cornai.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    scanMode: String = "daun",
    onScanModeChange: (String) -> Unit = {},
    onResultReady: (diseaseName: String, confidence: Float, isHealthy: Boolean) -> Unit,
    onBack: () -> Unit,
    onClassify: (Bitmap) -> Unit = {},
    isModelLoading: Boolean = false,
    isClassifying: Boolean = false,
    liveResult: com.cornai.ml.ClassificationResult? = null,
    topPredictions: List<com.cornai.ml.ClassificationResult> = emptyList(),
    isLiveScanning: Boolean = true,
    onLiveFrameAnalyzed: (Bitmap) -> Unit = {},
    onLockResult: (com.cornai.ml.ClassificationResult) -> Unit = {},
    onToggleLiveScanning: (Boolean) -> Unit = {},
    startInGalleryMode: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var isScanning by remember { mutableStateOf(false) }
    var isGalleryMode by remember { mutableStateOf(startInGalleryMode) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        android.util.Log.d("ScannerScreen", "Gallery callback - Uri yang dipilih: $uri")
        uri?.let {
            selectedImageUri = it
            isGalleryMode = true
        }
    }

    LaunchedEffect(startInGalleryMode) {
        if (startInGalleryMode) {
            android.util.Log.d("ScannerScreen", "Auto launching gallery picker from startInGalleryMode")
            try {
                galleryLauncher.launch("image/*")
            } catch (e: Exception) {
                android.util.Log.e("ScannerScreen", "Failed to launch gallery: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Handle classification result
    LaunchedEffect(isClassifying) {
        if (isClassifying) {
            // Wait for classification to complete
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
                    isScanning = isScanning || isClassifying,
                    onScanStart = {
                        val bitmap = getBitmapFromUri(context, selectedImageUri!!)
                        if (bitmap != null) {
                            onClassify(bitmap)
                        } else {
                            isScanning = true
                            simulateDetection(scanMode, onResultReady) { scanning ->
                                isScanning = scanning
                            }
                        }
                    },
                    onBack = {
                        selectedImageUri = null
                        isGalleryMode = false
                    }
                )
                ScanModeSelector(
                    scanMode = scanMode,
                    onScanModeChange = onScanModeChange,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp)
                )
            }
            else -> {
                CameraPreview(
                    scanMode = scanMode,
                    isScanning = isScanning || isClassifying,
                    onScanStart = { bitmap ->
                        onClassify(bitmap)
                    },
                    onScanFallback = {
                        isScanning = true
                        simulateDetection(scanMode, onResultReady) { scanning ->
                            isScanning = scanning
                        }
                    },
                    onGalleryClick = {
                        android.util.Log.d("ScannerScreen", "Tombol galeri diklik, meluncurkan...")
                        try {
                            galleryLauncher.launch("image/*")
                        } catch (e: Exception) {
                            android.util.Log.e("ScannerScreen", "Gagal membuka galeri: ${e.message}", e)
                        }
                    },
                    onBack = onBack,
                    liveResult = liveResult,
                    topPredictions = topPredictions,
                    isLiveScanning = isLiveScanning,
                    onLiveFrameAnalyzed = onLiveFrameAnalyzed,
                    onLockResult = onLockResult,
                    onToggleLiveScanning = onToggleLiveScanning
                )
                ScanModeSelector(
                    scanMode = scanMode,
                    onScanModeChange = onScanModeChange,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp)
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
            .background(Brush.verticalGradient(colors = listOf(GreenDark, Background)))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Izinkan Akses Kamera", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Untuk memindai penyakit jagung, kami memerlukan akses kamera.", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(48.dp))
        GradientButton(text = "Berikan Izin", onClick = onRequestPermission, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("Kembali", color = Color.White.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun CameraPreview(
    scanMode: String,
    isScanning: Boolean,
    onScanStart: (Bitmap) -> Unit,
    onScanFallback: () -> Unit,
    onGalleryClick: () -> Unit,
    onBack: () -> Unit,
    liveResult: com.cornai.ml.ClassificationResult?,
    topPredictions: List<com.cornai.ml.ClassificationResult>,
    isLiveScanning: Boolean,
    onLiveFrameAnalyzed: (Bitmap) -> Unit,
    onLockResult: (com.cornai.ml.ClassificationResult) -> Unit,
    onToggleLiveScanning: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    var isCapturing by remember { mutableStateOf(false) }
    val showScanning = isScanning || isCapturing

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val lastAnalysisTime = remember { java.util.concurrent.atomic.AtomicLong(0L) }

    val isLiveScanningRef = rememberUpdatedState(isLiveScanning)
    val onLiveFrameAnalyzedRef = rememberUpdatedState(onLiveFrameAnalyzed)

    // Dynamic states for quality, connectivity, and smart flash control
    val isConnected by rememberConnectivityState()
    var isImageBlurry by remember { mutableStateOf(false) }
    var flashState by remember { mutableStateOf("off") } // "off", "on", "auto"
    var isAutoFlashActive by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isGridActive by remember { mutableStateOf(false) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    val isFlashEnabled = (flashState == "on") || (flashState == "auto" && isAutoFlashActive)

    val infiniteTransition = rememberInfiniteTransition(label = "autoScan")
    val pulsingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    DisposableEffect(cameraSelector, isFlashEnabled, isLiveScanning, previewView) {
        val currentPreviewView = previewView ?: return@DisposableEffect onDispose {}

        val imageCaptureUseCase = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(if (isFlashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
            .build()
        imageCapture = imageCaptureUseCase

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
            try {
                val currentTime = System.currentTimeMillis()
                if (isLiveScanningRef.value && (currentTime - lastAnalysisTime.get() >= 400L)) {
                    lastAnalysisTime.set(currentTime)
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
                    val bitmap = try {
                        imageProxy.toBitmap().rotate(rotationDegrees)
                    } catch (e: Throwable) {
                        null
                    }
                    if (bitmap != null) {
                        // Analyze image quality on the analyzer thread
                        val quality = analyzeBitmapQuality(bitmap)
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            if (flashState == "auto") {
                                if (quality.brightness < 65.0) {
                                    isAutoFlashActive = true
                                } else if (quality.brightness > 90.0) {
                                    isAutoFlashActive = false
                                }
                            } else {
                                isAutoFlashActive = false
                            }

                            // If variance is less than 150.0, the image is likely blurry
                            isImageBlurry = quality.variance < 150.0
                        }
                        onLiveFrameAnalyzedRef.value(bitmap)
                    }
                }
            } catch (e: Throwable) {
                // Ignore frame errors silently
            } finally {
                imageProxy.close()
            }
        }

        // Non-blocking: addListener fires when camera is ready
        val listenerExecutor = ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(currentPreviewView.surfaceProvider)
                }
                cameraProvider.unbindAll()
                val camera = if (isLiveScanningRef.value) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCaptureUseCase, imageAnalysis
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCaptureUseCase
                    )
                }
                cameraControl = camera.cameraControl
            } catch (e: Exception) {
                // Camera bind failure - handle gracefully
            }
        }, listenerExecutor)

        onDispose {
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: Exception) { /* ignore */ }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
        }
    }

    LaunchedEffect(flashState, isAutoFlashActive, cameraControl) {
        val active = (flashState == "on") || (flashState == "auto" && isAutoFlashActive)
        try {
            cameraControl?.enableTorch(active)
        } catch (e: Exception) {
            // ignore torch failures on non-supporting devices/emulators
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }.also {
                    previewView = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isGridActive) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Vertical grid lines
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = androidx.compose.ui.geometry.Offset(width / 3f, 0f),
                    end = androidx.compose.ui.geometry.Offset(width / 3f, height),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = androidx.compose.ui.geometry.Offset(width * 2f / 3f, 0f),
                    end = androidx.compose.ui.geometry.Offset(width * 2f / 3f, height),
                    strokeWidth = 1.dp.toPx()
                )
                
                // Horizontal grid lines
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = androidx.compose.ui.geometry.Offset(0f, height / 3f),
                    end = androidx.compose.ui.geometry.Offset(width, height / 3f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = androidx.compose.ui.geometry.Offset(0f, height * 2f / 3f),
                    end = androidx.compose.ui.geometry.Offset(width, height * 2f / 3f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Quick Zoom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var selectedZoom by remember { mutableStateOf("1x") }
            listOf("1x" to 0.0f, "2x" to 0.4f, "3x" to 0.8f).forEach { (label, zoomVal) ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (selectedZoom == label) GreenPrimary.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.15f))
                        .border(
                            BorderStroke(1.dp, Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                            )),
                            shape = CircleShape
                        )
                        .clickable {
                            try {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } catch (e: Exception) {}
                            selectedZoom = label
                            cameraControl?.setLinearZoom(zoomVal)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedScanningFrame(isScanning = showScanning, liveResult = liveResult, scanMode = scanMode)
        }

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
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                    )), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isLiveScanning) GreenPrimary.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.05f))
                    )),
                    modifier = Modifier.clickable { onToggleLiveScanning(!isLiveScanning) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isLiveScanning) Color.Green.copy(alpha = pulsingAlpha) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveScanning) "Auto-Scan ON" else "Auto-Scan OFF",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!isConnected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = HealthyGreen.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.05f))
                        ))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Offline Mode",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mode Offline Aktif (Inference Lokal)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row {
                IconButton(
                    onClick = {
                        try {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } catch (e: Exception) {}
                        isGridActive = !isGridActive
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isGridActive) GreenPrimary.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                        )), CircleShape)
                ) {
                    Icon(imageVector = if (isGridActive) Icons.Default.GridOn else Icons.Default.GridOff, contentDescription = "Toggle Grid", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        try {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } catch (e: Exception) {}
                        flashState = when (flashState) {
                            "off" -> "on"
                            "on" -> "auto"
                            else -> "off"
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when (flashState) {
                                "on" -> GoldPrimary.copy(alpha = 0.8f)
                                "auto" -> GreenPrimary.copy(alpha = 0.8f)
                                else -> Color.White.copy(alpha = 0.15f)
                            }
                        )
                        .border(BorderStroke(1.dp, Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                        )), CircleShape)
                ) {
                    val (icon, tint) = when (flashState) {
                        "on" -> Pair(Icons.Default.FlashOn, Color.Black)
                        "auto" -> Pair(Icons.Default.FlashAuto, Color.Black)
                        else -> Pair(Icons.Default.FlashOff, Color.White)
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Flash Mode",
                        tint = tint
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        try {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        } catch (e: Exception) {}
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                        )), CircleShape)
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                }
            }
        }

        // Blur / Focus Warning overlay
        AnimatedVisibility(
            visible = isImageBlurry && isLiveScanning,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 170.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Red.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning Blur",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gambar Buram, Posisikan Fokus",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Distance Guide Card (Permanent)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.05f))
                ))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Jarak",
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (scanMode == "tongkol") "Jarak Ideal: 10–15 cm dari tongkol" else "Jarak Ideal: 15–20 cm dari daun",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isLiveScanning) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                    border = BorderStroke(
                        1.dp,
                        if (liveResult != null) {
                            if (liveResult.isHealthy) HealthyGreen.copy(alpha = 0.6f) else DiseaseRed.copy(alpha = 0.6f)
                        } else Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (liveResult == null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ScannerCircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (scanMode == "tongkol") "Mengamati tongkol jagung..." else "Mengamati daun jagung...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Hasil Deteksi Live",
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = liveResult.displayName,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (liveResult.isHealthy) HealthyGreen else DiseaseRed
                                ) {
                                    Text(
                                        text = if (liveResult.isHealthy) "Sehat" else "Penyakit",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            topPredictions.forEach { prediction ->
                                val barColor = if (prediction.isHealthy) HealthyGreen else DiseaseRed
                                val animatedProgress by animateFloatAsState(
                                    targetValue = prediction.confidence,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "progressAnimation"
                                )
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(prediction.displayName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                        Text("${(animatedProgress * 100).toInt()}%", color = barColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = animatedProgress,
                                        color = barColor,
                                        trackColor = Color.White.copy(alpha = 0.1f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            GradientButton(
                                text = "Kunci & Simpan Hasil",
                                onClick = {
                                    try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
                                    onLockResult(liveResult)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) }
                            )
                        }
                    }
                }
            } else {
                if (!showScanning) {
                    Text(
                        text = if (scanMode == "tongkol") "Posisikan tongkol jagung" else "Posisikan daun jagung",
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
                    Spacer(modifier = Modifier.height(20.dp))
                    GradientButton(
                        text = "Ambil Foto & Analisis",
                        onClick = {
                            try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
                            val capture = imageCapture
                            if (capture != null) {
                                isCapturing = true
                                capture.takePicture(
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {
                                            val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()
                                            val bitmap = try {
                                                image.toBitmap().rotate(rotationDegrees)
                                            } catch (e: Throwable) {
                                                null
                                            }
                                            image.close()
                                            isCapturing = false
                                            if (bitmap != null) {
                                                onScanStart(bitmap)
                                            } else {
                                                onScanFallback()
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            isCapturing = false
                                            onScanFallback()
                                        }
                                    }
                                )
                            } else {
                                onScanFallback()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable {
                                try { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } catch (e: Exception) {}
                                onGalleryClick()
                            }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih dari Galeri", color = Color.White, fontSize = 14.sp)
                    }
                } else {
                    Text("Menganalisis Snapshot...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun AnimatedScanningFrame(
    isScanning: Boolean,
    liveResult: com.cornai.ml.ClassificationResult? = null,
    scanMode: String = "daun"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "frame")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val frameColor = when {
        liveResult == null -> GreenPrimary
        liveResult.isHealthy -> HealthyGreen
        else -> DiseaseRed
    }

    Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
        if (!isScanning) {
            val cornerLength = 44.dp
            val strokeWidth = 4.dp

            // Corner borders
            Box(modifier = Modifier.align(Alignment.TopStart)) {
                Box(modifier = Modifier.width(cornerLength).height(strokeWidth).background(frameColor.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.width(strokeWidth).height(cornerLength).background(frameColor.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Box(modifier = Modifier.align(Alignment.TopEnd).width(cornerLength).height(strokeWidth).background(frameColor.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.TopEnd).width(strokeWidth).height(cornerLength).background(frameColor.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.BottomStart)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).width(cornerLength).height(strokeWidth).background(frameColor.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.BottomStart).width(strokeWidth).height(cornerLength).background(frameColor.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).width(cornerLength).height(strokeWidth).background(frameColor.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.BottomEnd).width(strokeWidth).height(cornerLength).background(frameColor.copy(alpha = animatedAlpha)))
            }

            // 1. Leaf Alignment Guide Overlay (Dashed path)
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .scale(pulseScale)
            ) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    if (scanMode == "tongkol") {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                left = size.width * 0.25f,
                                top = 10f,
                                right = size.width * 0.75f,
                                bottom = size.height - 10f,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.25f)
                            )
                        )
                    } else {
                        moveTo(size.width / 2, size.height - 10f)
                        quadraticBezierTo(
                            size.width * 0.15f, size.height * 0.55f,
                            size.width / 2, 10f
                        )
                        quadraticBezierTo(
                            size.width * 0.85f, size.height * 0.55f,
                            size.width / 2, size.height - 10f
                        )
                    }
                }
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.25f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(15f, 15f), 0f
                        )
                    )
                )
            }

            // 2. Center Focus Target (Pulsing circle reticle)
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .size(60.dp)
                    .scale(pulseScale)
            ) {
                drawCircle(
                    color = frameColor.copy(alpha = 0.5f),
                    radius = size.minDimension / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(6f, 6f), 0f
                        )
                    )
                )
                drawCircle(
                    color = frameColor.copy(alpha = 0.7f),
                    radius = 3.dp.toPx()
                )
            }

            // 3. Glowing Laser Scanner Line (Horizontal sweeping)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .align(Alignment.TopStart)
                    .padding(top = (260 * animatedOffset).dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                frameColor.copy(alpha = 0.8f),
                                frameColor.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        } else {
            ScanningIndicator(size = 260.dp, primaryColor = frameColor)
        }
    }
}

@Composable
private fun GalleryPreview(uri: Uri, isScanning: Boolean, onScanStart: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = uri,
                contentDescription = "Gallery Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    ScanningIndicator(size = 260.dp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text("Pratinjau Gambar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.size(44.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).background(Color.Black.copy(alpha = 0.8f)).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isScanning) {
                Text("Gambar siap dianalisis", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))
                GradientButton(text = "Analisis Gambar", onClick = onScanStart, modifier = Modifier.fillMaxWidth(), icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) })
            } else {
                Text("Menganalisis...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

private fun simulateDetection(
    scanMode: String,
    onResultReady: (String, Float, Boolean) -> Unit,
    updateScanning: (Boolean) -> Unit
) {
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        val results = if (scanMode == "tongkol") {
            listOf(
                Triple("Healthy_Tongkol", 0.98f, true),
                Triple("Unhealthy_Tongkol", 0.92f, false)
            )
        } else {
            listOf(
                Triple("Common_Rust", 0.89f, false),
                Triple("Blight", 0.94f, false),
                Triple("Healthy_Daun", 0.97f, true),
                Triple("Gray_Leaf_Spot", 0.91f, false),
                Triple("Bipolaris", 0.88f, false)
            )
        }
        val result = results.random()
        onResultReady(result.first, result.second, result.third)
    }, 2500)
}

@Composable
private fun ScannerCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = GreenPrimary,
    strokeWidth: androidx.compose.ui.unit.Dp = 2.dp
) {
    val transition = rememberInfiniteTransition(label = "loading")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    androidx.compose.foundation.Canvas(modifier = modifier) {
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 270f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

@Composable
fun rememberConnectivityState(): State<Boolean> {
    val context = LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val isConnected = remember { mutableStateOf(checkConnection(connectivityManager)) }

    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                isConnected.value = true
            }
            override fun onLost(network: android.net.Network) {
                isConnected.value = false
            }
        }
        try {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } catch (e: Exception) {
            // fallback
        }
        onDispose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) { /* ignore */ }
        }
    }
    return isConnected
}

private fun checkConnection(manager: ConnectivityManager): Boolean {
    return try {
        val active = manager.activeNetwork ?: return false
        val caps = manager.getNetworkCapabilities(active) ?: return false
        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } catch (e: Exception) {
        false
    }
}

data class BitmapQuality(val brightness: Double, val variance: Double)

fun analyzeBitmapQuality(bitmap: Bitmap): BitmapQuality {
    return try {
        val scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, false)
        var sumLuminance = 0.0
        var sumSquaredDiff = 0.0
        val pixels = IntArray(scaled.width * scaled.height)
        scaled.getPixels(pixels, 0, scaled.width, 0, 0, scaled.width, scaled.height)

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val lum = 0.299 * r + 0.587 * g + 0.114 * b
            sumLuminance += lum
        }
        val avgLuminance = sumLuminance / pixels.size

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val lum = 0.299 * r + 0.587 * g + 0.114 * b
            sumSquaredDiff += (lum - avgLuminance) * (lum - avgLuminance)
        }
        val variance = sumSquaredDiff / pixels.size

        BitmapQuality(avgLuminance, variance)
    } catch (e: Exception) {
        BitmapQuality(128.0, 1000.0) // fallback to normal
    }
}

@Composable
fun ScanModeSelector(
    scanMode: String,
    onScanModeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.6f),
        border = BorderStroke(
            1.dp, 
            Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
            )
        ),
        modifier = modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val modes = listOf("daun" to "Daun", "tongkol" to "Tongkol")
            modes.forEach { (modeKey, label) ->
                val isSelected = scanMode == modeKey
                
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) GreenPrimary else Color.Transparent,
                    animationSpec = tween(durationMillis = 250, easing = EaseInOutQuad),
                    label = "modeBgColor"
                )
                
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    animationSpec = tween(durationMillis = 200),
                    label = "modeTextColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(backgroundColor)
                        .clickable {
                            if (!isSelected) {
                                try {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                } catch (e: Exception) {}
                                onScanModeChange(modeKey)
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
