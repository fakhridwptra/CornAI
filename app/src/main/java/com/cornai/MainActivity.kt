// Activity utama aplikasi, lokasi start UI dan navigasi utama.
// File: java\com\cornai\MainActivity.kt

package com.cornai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cornai.data.local.PreferencesManager
import com.cornai.ui.navigation.CornAINavHost
import com.cornai.ui.theme.CornAITheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(applicationContext)

        enableEdgeToEdge()

        setContent {
            android.util.Log.d("CornAI_Main", "setContent started")
            val isDarkMode by preferencesManager.isDarkMode.collectAsState(initial = false)
            val navController = rememberNavController()

            android.util.Log.d("CornAI_Main", "isDarkMode collected: $isDarkMode")

            CornAITheme(darkTheme = isDarkMode) {
                android.util.Log.d("CornAI_Main", "CornAITheme content block")
                CornAINavHost(
                    navController = navController,
                    preferencesManager = preferencesManager
                )
            }
        }
    }
}
