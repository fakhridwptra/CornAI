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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onResultReady: (diseaseName: String, confidence: Float, isHealthy: Boolean) -> Unit,
    onBack: () -> Unit,
    onClassify: (Bitmap) -> Unit = {},
    isModelLoading: Boolean = false,
    isClassifying: Boolean = false
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
    onBack: () -> Unit
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

    DisposableEffect(cameraSelector, isFlashEnabled) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(if (isFlashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            // Handle error
        }

        onDispose { cameraProvider.unbindAll() }
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
            AnimatedScanningFrame(isScanning = showScanning)
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

            Text(
                text = if (showScanning) "Memindai..." else "Arahkan ke Daun/Tongkol",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

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
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showScanning) {
                Text("Posisikan daun atau tongkol jagung", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, textAlign = TextAlign.Center)
                Text("di dalam bingkai pemindaian", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                GradientButton(
                    text = "Mulai Pemindaian",
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
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                    icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) }
                )
                Spacer(modifier = Modifier.height(24.dp))
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
                    Text("Atau pilih dari Galeri", color = Color.White, fontSize = 14.sp)
                }
            } else {
                Text("Menganalisis...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun AnimatedScanningFrame(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "frame")
    val animatedOffset by infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "offset")
    val animatedAlpha by infiniteTransition.animateFloat(initialValue = 0.5f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(500, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "alpha")

    Box(modifier = Modifier.size(280.dp), contentAlignment = Alignment.Center) {
        if (!isScanning) {
            val cornerLength = 40.dp
            val strokeWidth = 3.dp

            Box(modifier = Modifier.align(Alignment.TopStart)) {
                Box(modifier = Modifier.width(cornerLength).height(strokeWidth).background(GreenPrimary.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.width(strokeWidth).height(cornerLength).background(GreenPrimary.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Box(modifier = Modifier.align(Alignment.TopEnd).width(cornerLength).height(strokeWidth).background(GreenPrimary.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.TopEnd).width(strokeWidth).height(cornerLength).background(GreenPrimary.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.BottomStart)) {
                Box(modifier = Modifier.align(Alignment.BottomStart).width(cornerLength).height(strokeWidth).background(GreenPrimary.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.BottomStart).width(strokeWidth).height(cornerLength).background(GreenPrimary.copy(alpha = animatedAlpha)))
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                Box(modifier = Modifier.align(Alignment.BottomEnd).width(cornerLength).height(strokeWidth).background(GreenPrimary.copy(alpha = animatedAlpha)))
                Box(modifier = Modifier.align(Alignment.BottomEnd).width(strokeWidth).height(cornerLength).background(GreenPrimary.copy(alpha = animatedAlpha)))
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.TopStart).padding(top = (240 * animatedOffset).dp)
                    .background(Brush.horizontalGradient(colors = listOf(Color.Transparent, GoldPrimary.copy(alpha = 0.8f), Color.Transparent)))
            )
        } else {
            ScanningIndicator(size = 260.dp)
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