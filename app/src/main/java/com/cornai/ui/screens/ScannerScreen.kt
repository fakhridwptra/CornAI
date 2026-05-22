package com.cornai.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
    onToggleLiveScanning: (Boolean) -> Unit = {}
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
                            simulateDetection(onResultReady) { scanning ->
                                isScanning = scanning
                            }
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
                    isScanning = isScanning || isClassifying,
                    onScanStart = { bitmap ->
                        onClassify(bitmap)
                    },
                    onScanFallback = {
                        isScanning = true
                        simulateDetection(onResultReady) { scanning ->
                            isScanning = scanning
                        }
                    },
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onBack = onBack,
                    liveResult = liveResult,
                    topPredictions = topPredictions,
                    isLiveScanning = isLiveScanning,
                    onLiveFrameAnalyzed = onLiveFrameAnalyzed,
                    onLockResult = onLockResult,
                    onToggleLiveScanning = onToggleLiveScanning
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
    var isFlashEnabled by remember { mutableStateOf(false) }
    val previewView = remember { PreviewView(context) }

    var isCapturing by remember { mutableStateOf(false) }
    val showScanning = isScanning || isCapturing

    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val lastAnalysisTime = remember { java.util.concurrent.atomic.AtomicLong(0L) }

    val isLiveScanningRef = rememberUpdatedState(isLiveScanning)
    val onLiveFrameAnalyzedRef = rememberUpdatedState(onLiveFrameAnalyzed)

    DisposableEffect(cameraSelector, isFlashEnabled, isLiveScanning) {
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
                    } catch (e: Exception) {
                        null
                    }
                    if (bitmap != null) {
                        onLiveFrameAnalyzedRef.value(bitmap)
                    }
                }
            } catch (e: Exception) {
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
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                cameraProvider.unbindAll()
                if (isLiveScanningRef.value) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCaptureUseCase, imageAnalysis
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCaptureUseCase
                    )
                }
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

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedScanningFrame(isScanning = showScanning, liveResult = liveResult)
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
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isLiveScanning) GreenPrimary.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
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
                            .background(if (isLiveScanning) Color.Green else Color.Gray)
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

            Row {
                IconButton(
                    onClick = { isFlashEnabled = !isFlashEnabled },
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isFlashEnabled) GoldPrimary.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff, contentDescription = "Flash", tint = if (isFlashEnabled) Color.Black else Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA },
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
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
                                CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Mengamati daun jagung...",
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
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(prediction.displayName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                        Text("${(prediction.confidence * 100).toInt()}%", color = barColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = prediction.confidence,
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
                                onClick = { onLockResult(liveResult) },
                                modifier = Modifier.fillMaxWidth(),
                                icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) }
                            )
                        }
                    }
                }
            } else {
                if (!showScanning) {
                    Text("Posisikan daun atau tongkol jagung", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, textAlign = TextAlign.Center)
                    Text("di dalam bingkai pemindaian", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(20.dp))
                    GradientButton(
                        text = "Ambil Foto & Analisis",
                        onClick = {
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
                                            } catch (e: Exception) {
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
                            .clickable { onGalleryClick() }
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
    liveResult: com.cornai.ml.ClassificationResult? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "frame")
    val animatedOffset by infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "offset")
    val animatedAlpha by infiniteTransition.animateFloat(initialValue = 0.5f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "alpha")

    val frameColor = when {
        liveResult == null -> GreenPrimary
        liveResult.isHealthy -> HealthyGreen
        else -> DiseaseRed
    }

    Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
        if (!isScanning) {
            val cornerLength = 40.dp
            val strokeWidth = 3.dp

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

            Box(
                modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.TopStart).padding(top = (240 * animatedOffset).dp)
                    .background(Brush.horizontalGradient(colors = listOf(Color.Transparent, frameColor.copy(alpha = 0.8f), Color.Transparent)))
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
    onResultReady: (String, Float, Boolean) -> Unit,
    updateScanning: (Boolean) -> Unit
) {
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        val results = listOf(
            Triple("Common_Rust", 0.89f, false),
            Triple("Blight", 0.94f, false),
            Triple("Healthy_Daun", 0.97f, true),
            Triple("Gray_Leaf_Spot", 0.91f, false),
            Triple("Healthy_Tongkol", 0.98f, true)
        )
        val result = results.random()
        onResultReady(result.first, result.second, result.third)
    }, 2500)
}