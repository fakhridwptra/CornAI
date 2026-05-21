package com.cornai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cornai.data.local.PreferencesManager
import com.cornai.ui.navigation.CornAINavHost
import com.cornai.ui.theme.CornAITheme

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize preferences
        preferencesManager = PreferencesManager(applicationContext)

        enableEdgeToEdge()

        setContent {
            CornAITheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    // Observe auth state
                    val isLoggedIn by preferencesManager.isLoggedIn.collectAsState(initial = false)
                    val isGuest by preferencesManager.isGuest.collectAsState(initial = true)
                    val hasSeenOnboarding by preferencesManager.hasSeenOnboarding.collectAsState(initial = false)

                    CornAINavHost(
                        navController = navController,
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}