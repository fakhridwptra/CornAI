package com.cornai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    // Navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Scanner.route,
        Screen.History.route,
        Screen.Profile.route
    )

    // Auth state for UI
    val authState by authViewModel.authState.collectAsState()


    Scaffold(
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
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
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

                LaunchedEffect(Unit) {
                    homeViewModel.loadData()
                }

                HomeScreen(
                    onScanClick = { navController.navigate(Screen.Scanner.route) },
                    onHistoryClick = { navController.navigate(Screen.History.route) },
                    userName = homeUserName,
                    totalScans = homeTotalScans,
                    healthyScans = homeHealthyScans,
                    diseaseScans = homeDiseaseScans,
                    isGuest = isGuest
                )
            }

            // ===== SCANNER =====
            composable(Screen.Scanner.route) {
                // Reset state when entering the Scanner screen
                DisposableEffect(Unit) {
                    scannerViewModel.resetState()
                    onDispose {}
                }

                // Handle classification result
                LaunchedEffect(scanState) {
                    when (val state = scanState) {
                        is UiState.Success -> {
                            val result = state.data
                            scannerViewModel.saveScanResult(result)
                            navController.navigate(
                                Screen.Result.createRoute(result.displayName, result.confidence, result.isHealthy)
                            )
                            scannerViewModel.resetState()
                        }
                        else -> {}
                    }
                }

                ScannerScreen(
                    onResultReady = { diseaseName, confidence, isHealthy ->
                        navController.navigate(
                            Screen.Result.createRoute(diseaseName, confidence, isHealthy)
                        )
                    },
                    onBack = { navController.popBackStack() },
                    onClassify = { bitmap ->
                        scannerViewModel.classifyImage(bitmap)
                    },
                    isClassifying = scanState is UiState.Loading
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
                    }
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

                LaunchedEffect(Unit) {
                    profileViewModel.loadStats()
                }

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
                    diseaseScans = profileStats.third
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
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onPrivacyClick = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onDarkModeToggle = { /* Handle dark mode */ }
                )
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

                ResultDetailScreen(
                    diseaseName = diseaseName,
                    confidence = confidence,
                    isHealthy = isHealthy,
                    symptoms = listOf("Gejala 1", "Gejala 2"),
                    treatment = "Penanganan yang disarankan",
                    severity = "Sedang",
                    recoveryTime = "2-3 Minggu",
                    onBack = { navController.popBackStack() },
                    onScanAgain = {
                        navController.navigate(Screen.Scanner.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onShare = { }
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
                HistoryEnhancedScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}