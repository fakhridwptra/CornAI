// File Kotlin aplikasi CornAI.
// File: java\com\cornai\ui\navigation\CornAINavHost.kt

package com.cornai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.cornai.data.local.PreferencesManager
import com.cornai.data.model.UiState
import com.cornai.ui.screens.*
import com.cornai.ui.theme.*
import com.cornai.ui.viewmodel.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

@Composable
fun CornAINavHost(
    navController: NavHostController,
    preferencesManager: PreferencesManager,
    startDestination: String = Screen.Splash.route
) {
    // ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val scannerViewModel: ScannerViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    // Auth State from DataStore
    val isLoggedIn by preferencesManager.isLoggedIn.collectAsState(initial = false)
    val isGuest by preferencesManager.isGuest.collectAsState(initial = false)
    val hasSeenOnboarding by preferencesManager.hasSeenOnboarding.collectAsState(initial = false)

    // Scanner State
    val scanState by scannerViewModel.scanState.collectAsState()
    val liveResult by scannerViewModel.liveResult.collectAsState()
    val topPredictions by scannerViewModel.topPredictions.collectAsState()
    val isLiveScanning by scannerViewModel.isLiveScanning.collectAsState()
    val scanMode by scannerViewModel.scanMode.collectAsState()

    // Navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    android.util.Log.d("CornAI_Nav", "currentRoute updated: $currentRoute")

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Scanner.route,
        Screen.History.route,
        Screen.Profile.route
    )

    // Auth state for UI
    val authState by authViewModel.authState.collectAsState()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding: PaddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            // ===== SPLASH =====
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    isLoggedIn = isLoggedIn,
                    hasSeenOnboarding = hasSeenOnboarding
                )
            }

            // ===== ONBOARDING =====
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        authViewModel.completeOnboarding()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // ===== LOGIN =====
            composable(Screen.Login.route) {
                val errorMessage = when (authState) {
                    is UiState.Error -> (authState as UiState.Error).message
                    else -> null
                }
                val isLoading = authState is UiState.Loading

                LaunchedEffect(authState) {
                    if (authState is UiState.Success) {
                        authViewModel.resetState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    onBack = { 
                        authViewModel.resetState()
                        navController.popBackStack() 
                    },
                    onLoginSuccess = {
                        authViewModel.resetState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { 
                        authViewModel.resetState()
                        navController.navigate(Screen.Register.route) 
                    },
                    onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                    onGuestClick = {
                        authViewModel.signInAsGuest()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLoginClick = { email, password ->
                        authViewModel.updateLoginEmail(email)
                        authViewModel.updateLoginPassword(password)
                        authViewModel.signIn()
                    },
                    onResetError = { authViewModel.resetState() },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }

            // ===== REGISTER =====
            composable(Screen.Register.route) {
                val registerErrorMessage = when (authState) {
                    is UiState.Error -> (authState as UiState.Error).message
                    else -> null
                }
                val isRegisterLoading = authState is UiState.Loading

                LaunchedEffect(authState) {
                    if (authState is UiState.Success) {
                        authViewModel.resetState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                }

                RegisterScreen(
                    onBack = { 
                        authViewModel.resetState()
                        navController.popBackStack() 
                    },
                    onRegisterClick = { name, email, phone, password ->
                        authViewModel.signUp(name, email, phone, password)
                    },
                    onLoginClick = { 
                        authViewModel.resetState()
                        navController.popBackStack() 
                    },
                    isLoading = isRegisterLoading,
                    errorMessage = registerErrorMessage
                )
            }

            // ===== FORGOT PASSWORD =====
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() },
                    onSendSuccess = { navController.popBackStack() }
                )
            }

            // ===== HOME =====
            composable(Screen.Home.route) {
                val homeUserName by homeViewModel.userName.collectAsState()
                val homeTotalScans by homeViewModel.totalScans.collectAsState()
                val homeHealthyScans by homeViewModel.healthyScans.collectAsState()
                val homeDiseaseScans by homeViewModel.diseaseScans.collectAsState()
                val weatherState by homeViewModel.weatherState.collectAsState()

                HomeScreen(
                    onScanClick = { navController.navigate(Screen.Scanner.createRoute(false)) },
                    onGalleryClick = { navController.navigate(Screen.Scanner.createRoute(true)) },
                    onHistoryClick = { navController.navigate(Screen.History.route) },
                    onRefreshWeather = { homeViewModel.refreshWeather() },
                    userName = homeUserName,
                    totalScans = homeTotalScans,
                    healthyScans = homeHealthyScans,
                    diseaseScans = homeDiseaseScans,
                    isGuest = isGuest,
                    weatherState = weatherState
                )
            }

            // ===== SCANNER =====
            composable(
                route = Screen.Scanner.route,
                arguments = listOf(
                    navArgument("openGallery") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val openGallery = backStackEntry.arguments?.getBoolean("openGallery") ?: false

                // Reset state when entering the Scanner screen
                DisposableEffect(Unit) {
                    scannerViewModel.resetState()
                    onDispose { scannerViewModel.setLiveScanning(false) }
                }

                // Handle classification result (from manual capture or lock-and-save)
                LaunchedEffect(scanState) {
                    when (val state = scanState) {
                        is UiState.Success -> {
                            val result = state.data
                            navController.navigate(
                                Screen.Result.createRoute(result.displayName, result.confidence, result.isHealthy)
                            )
                            scannerViewModel.resetState()
                        }
                        else -> {}
                    }
                }

                ScannerScreen(
                    scanMode = scanMode,
                    onScanModeChange = { mode ->
                        scannerViewModel.setScanMode(mode)
                    },
                    onResultReady = { diseaseName, confidence, isHealthy ->
                        navController.navigate(
                            Screen.Result.createRoute(diseaseName, confidence, isHealthy)
                        )
                    },
                    onBack = { navController.popBackStack() },
                    onClassify = { bitmap ->
                        scannerViewModel.classifyImage(bitmap)
                    },
                    isClassifying = scanState is UiState.Loading,
                    liveResult = liveResult,
                    topPredictions = topPredictions,
                    isLiveScanning = isLiveScanning,
                    onLiveFrameAnalyzed = { bitmap ->
                        scannerViewModel.classifyLiveFrame(bitmap)
                    },
                    onLockResult = { result ->
                        scannerViewModel.lockAndSaveResult(result)
                    },
                    onToggleLiveScanning = { enabled ->
                        scannerViewModel.setLiveScanning(enabled)
                    },
                    startInGalleryMode = openGallery
                )
            }

            // ===== RESULT =====
            composable(
                route = Screen.Result.route,
                arguments = listOf(
                    navArgument("diseaseName") { type = NavType.StringType },
                    navArgument("confidence") { type = NavType.FloatType },
                    navArgument("isHealthy") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val diseaseName = backStackEntry.arguments?.getString("diseaseName") ?: ""
                val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0f
                val isHealthy = backStackEntry.arguments?.getBoolean("isHealthy") ?: true
                val allPredictions by scannerViewModel.allPredictions.collectAsState()

                ResultScreen(
                    diseaseName = diseaseName,
                    confidence = confidence,
                    isHealthy = isHealthy,
                    onScanAgain = {
                        navController.navigate(Screen.Scanner.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBackToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onViewDetail = {
                        navController.navigate(
                            Screen.ResultDetail.createRoute(diseaseName, confidence, isHealthy)
                        )
                    },
                    allPredictions = allPredictions
                )
            }

            // ===== HISTORY =====
            composable(Screen.History.route) {
                val historyList by historyViewModel.historyList.collectAsState()
                val historyStats by historyViewModel.stats.collectAsState()
                val historyIsLoading by historyViewModel.isLoading.collectAsState()

                LaunchedEffect(Unit) {
                    historyViewModel.loadHistory()
                }

                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onHistoryEnhancedClick = { navController.navigate(Screen.HistoryEnhanced.route) },
                    historyList = historyList.map { history ->
                        ScanHistoryData(
                            id = history.id,
                            diseaseName = history.diseaseName,
                            displayName = history.displayName,
                            confidence = history.confidence,
                            isHealthy = history.isHealthy,
                            timestamp = history.timestamp,
                            symptoms = history.symptoms,
                            treatment = history.treatment
                        )
                    },
                    stats = HistoryStatsData(
                        totalScans = historyStats.totalScans,
                        healthyCount = historyStats.healthyCount,
                        diseaseCount = historyStats.diseaseCount
                    ),
                    isLoading = historyIsLoading
                )
            }

            // ===== PROFILE =====
            composable(Screen.Profile.route) {
                val profileUserName by profileViewModel.userName.collectAsState()
                val profileUserEmail by profileViewModel.userEmail.collectAsState()
                val profileStats by profileViewModel.stats.collectAsState()
                val isProfileDarkMode by preferencesManager.isDarkMode.collectAsState(initial = false)
                val profileLanguage by preferencesManager.language.collectAsState(initial = "id")
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                var profileCacheSize by remember { mutableStateOf("0 B") }
                LaunchedEffect(Unit) { profileCacheSize = getCacheSize(context) }

                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onHelpClick = { navController.navigate(Screen.HelpSupport.route) },
                    onPrivacyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onEditClick = { navController.navigate(Screen.ProfileEdit.route) },
                    onNotificationSettingsClick = { navController.navigate(Screen.NotificationSettings.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onProfileEnhancedClick = { navController.navigate(Screen.ProfileEnhanced.route) },
                    onHistoryEnhancedClick = { navController.navigate(Screen.HistoryEnhanced.route) },
                    onSignOut = {
                        profileViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    userName = profileUserName,
                    userEmail = profileUserEmail,
                    isGuest = isGuest,
                    totalScans = profileStats.first,
                    healthyScans = profileStats.second,
                    diseaseScans = profileStats.third,
                    isDarkMode = isProfileDarkMode,
                    onDarkModeToggle = { enabled ->
                        scope.launch { preferencesManager.setDarkMode(enabled) }
                    },
                    onClearCache = {
                        clearCache(context)
                        profileCacheSize = getCacheSize(context)
                        android.widget.Toast.makeText(context, "Cache berhasil dihapus", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    cacheSize = profileCacheSize,
                    currentLanguage = profileLanguage,
                    onLanguageSelected = { langCode ->
                        scope.launch { preferencesManager.setLanguage(langCode) }
                    }
                )
            }


            // ===== PROFILE EDIT =====
            composable(Screen.ProfileEdit.route) {
                ProfileEditScreen(
                    onBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ===== NOTIFICATION SETTINGS =====
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== HELP & SUPPORT =====
            composable(Screen.HelpSupport.route) {
                HelpSupportScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== PRIVACY POLICY =====
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== SETTINGS =====
            composable(Screen.Settings.route) {
                val isNavDarkMode by preferencesManager.isDarkMode.collectAsState(initial = false)
                val currentLanguage by preferencesManager.language.collectAsState(initial = "id")
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                var cacheSize by remember { mutableStateOf("0 B") }
                var isCheckingUpdate by remember { mutableStateOf(false) }
                var showUpdateDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    cacheSize = getCacheSize(context)
                }

                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    isDarkMode = isNavDarkMode,
                    onDarkModeToggle = { enabled ->
                        scope.launch {
                            preferencesManager.setDarkMode(enabled)
                        }
                    },
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { langCode ->
                        scope.launch {
                            preferencesManager.setLanguage(langCode)
                        }
                    },
                    onClearCache = {
                        clearCache(context)
                        cacheSize = getCacheSize(context)
                        val msg = if (currentLanguage == "id") "Cache berhasil dihapus" else "Cache cleared successfully"
                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                    },
                    cacheSize = cacheSize,
                    onPrivacyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onTermsClick = { },
                    onCheckModelUpdate = {
                        scope.launch {
                            isCheckingUpdate = true
                            kotlinx.coroutines.delay(2000)
                            isCheckingUpdate = false
                            showUpdateDialog = true
                        }
                    }
                )

                if (isCheckingUpdate) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { isCheckingUpdate = false }) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = GreenPrimary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (currentLanguage == "id") "Memeriksa pembaruan model..." else "Checking for model updates...",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                if (showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = { showUpdateDialog = false },
                        title = {
                            Text(
                                text = if (currentLanguage == "id") "Pembaruan Model" else "Model Update",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                text = if (currentLanguage == "id")
                                    "Model AI Anda sudah versi terbaru (v1.2.0). Kinerja deteksi optimal."
                                    else "Your AI model is already at the latest version (v1.2.0). Optimal detection performance."
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showUpdateDialog = false }) {
                                Text("OK", color = GreenPrimary)
                            }
                        }
                    )
                }
            }

            // ===== RESULT DETAIL =====
            composable(
                route = Screen.ResultDetail.route,
                arguments = listOf(
                    navArgument("diseaseName") { type = NavType.StringType },
                    navArgument("confidence") { type = NavType.FloatType },
                    navArgument("isHealthy") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val diseaseName = backStackEntry.arguments?.getString("diseaseName") ?: ""
                val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0f
                val isHealthy = backStackEntry.arguments?.getBoolean("isHealthy") ?: true
                val context = LocalContext.current

                // Look up actual disease details in DiseaseData
                val diseaseInfo = com.cornai.ml.DiseaseData.diseaseInfoMap.values.find {
                    it.displayName.equals(diseaseName, ignoreCase = true) ||
                    it.className.equals(diseaseName, ignoreCase = true)
                }

                val symptoms = diseaseInfo?.symptoms ?: listOf("Gejala tidak tercatat secara spesifik untuk jenis ini")
                val treatment = diseaseInfo?.treatment ?: "Lakukan pemangkasan pada daun yang terkena dan semprotkan fungisida organik jika perlu."
                val severity = diseaseInfo?.severity ?: "Sedang"
                val recoveryTime = diseaseInfo?.recoveryTime ?: "2-3 Minggu"

                ResultDetailScreen(
                    diseaseName = diseaseInfo?.displayName ?: diseaseName,
                    confidence = confidence,
                    isHealthy = isHealthy,
                    symptoms = symptoms,
                    treatment = treatment,
                    severity = severity,
                    recoveryTime = recoveryTime,
                    onBack = { navController.popBackStack() },
                    onScanAgain = {
                        navController.navigate(Screen.Scanner.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onShare = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Hasil Diagnosis Jagung: ${diseaseInfo?.displayName ?: diseaseName}")
                            val shareMessage = """
                                Hasil Diagnosis Corn AI
                                Tanaman: Jagung
                                Status: ${if (isHealthy) "Sehat" else "Penyakit (${diseaseInfo?.displayName ?: diseaseName})"}
                                Akurasi: ${(confidence * 100).toInt()}%
                                
                                Tingkat Keparahan: $severity
                                Waktu Pemulihan: $recoveryTime
                                
                                Rekomendasi Penanganan:
                                $treatment
                                
                                Dibagikan via Corn AI App.
                            """.trimIndent()
                            putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Bagikan Hasil Diagnosis"))
                    }
                )
            }

            // ===== PROFILE ENHANCED =====
            composable(Screen.ProfileEnhanced.route) {
                ProfileEnhancedScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(Screen.ProfileEdit.route) }
                )
            }

            // ===== HISTORY ENHANCED =====
            composable(Screen.HistoryEnhanced.route) {
                val historyList by historyViewModel.historyList.collectAsState()
                val context = LocalContext.current

                HistoryEnhancedScreen(
                    onBack = { navController.popBackStack() },
                    historyList = historyList,
                    onExportData = {
                        if (historyList.isEmpty()) {
                            android.widget.Toast.makeText(context, "Tidak ada data untuk diekspor", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            try {
                                val csvHeader = "ID,Disease Name,Confidence,Is Healthy,Timestamp,Location,Weather\n"
                                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
                                val csvBody = historyList.joinToString("\n") { history ->
                                    val dateStr = sdf.format(java.util.Date(history.timestamp))
                                    "${history.id},\"${history.displayName.ifEmpty { history.diseaseName }}\",${(history.confidence * 100).toInt()}%,${history.isHealthy},\"$dateStr\",\"${history.location}\",\"${history.weather}\""
                                }
                                val csvData = csvHeader + csvBody
                                
                                val shareIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    type = "text/csv"
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Laporan Riwayat Scan Corn AI")
                                    putExtra(android.content.Intent.EXTRA_TEXT, csvData)
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Ekspor Laporan Scan"))
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "Gagal mengekspor data", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onDeleteItems = { ids ->
                        ids.forEach { id ->
                            historyViewModel.deleteHistory(id)
                        }
                        val msg = "Berhasil menghapus ${ids.size} scan"
                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

private fun getCacheSize(context: android.content.Context): String {
    return try {
        val size = getFolderSize(context.cacheDir)
        if (size <= 0) "0 B"
        else if (size < 1024) "$size B"
        else if (size < 1024 * 1024) String.format("%.1f KB", size.toFloat() / 1024f)
        else String.format("%.1f MB", size.toFloat() / (1024f * 1024f))
    } catch (e: Exception) {
        "0 B"
    }
}

private fun getFolderSize(file: java.io.File): Long {
    var size: Long = 0
    if (file.isDirectory) {
        val files = file.listFiles()
        if (files != null) {
            for (f in files) {
                size += getFolderSize(f)
            }
        }
    } else {
        size = file.length()
    }
    return size
}

private fun clearCache(context: android.content.Context) {
    try {
        context.cacheDir.deleteRecursively()
    } catch (e: Exception) {
        // ignore
    }
}
