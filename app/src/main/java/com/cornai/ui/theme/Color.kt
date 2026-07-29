// File Kotlin aplikasi CornAI.
// File: java\com\cornai\ui\theme\Color.kt

package com.cornai.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.MaterialTheme

// Primary - Deep Forest Green (Natural & Premium Agriculture)
val GreenDark = Color(0xFF1A382B)       // Deep Forest/Pine Green
val GreenPrimary = Color(0xFF2D5A43)    // Rich Moss Green
val GreenLight = Color(0xFF5B8A72)      // Sage/Eucalyptus Green

// Secondary - Earthy Clay & Warm Gold
val EmeraldDark = Color(0xFF7D4E2E)     // Warm Terracotta / Soil
val EmeraldPrimary = Color(0xFFB3803E)  // Warm Ochre / Wheat Gold
val EmeraldLight = Color(0xFFE6C594)    // Pale Straw Cream

// Accent - Corn Gold
val GoldDark = Color(0xFFC28E0E)        // Deep Corn Amber
val GoldPrimary = Color(0xFFE5A93C)     // Rich Maize Gold
val GoldLight = Color(0xFFF5D68D)       // Bright Corn Kernel Yellow

// Light Mode Static Colors
val LightBackground = Color(0xFFF6F4EF)      // Warm Linen/Paper background
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEDECE7)  // Soft Warm Stone
val LightTextPrimary = Color(0xFF212523)     // Soft Dark Slate
val LightTextSecondary = Color(0xFF6B726F)   // Muted Sage Grey
val LightDivider = Color(0xFFE3E5E3)

// Dark Mode Static Colors
val DarkBackground = Color(0xFF121614)       // Deep Charcoal Green
val DarkSurface = Color(0xFF1B201D)          // Charcoal Forest
val DarkSurfaceVariant = Color(0xFF262C29)  // Deep Muted Sage
val DarkTextPrimary = Color(0xFFEAECEB)      // Soft White
val DarkTextSecondary = Color(0xFF98A3A0)    // Light Muted Sage Grey
val DarkDivider = Color(0xFF2E3532)          // Deep Slate Border

// Composable Dynamic Colors mapping
val Background: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val Surface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val SurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onBackground

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextHint: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

val CardBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val Divider: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val Success = Color(0xFF2D5A43)
val Warning = Color(0xFFC28E0E)
val Error = Color(0xFFBD3A3A)
val Info = Color(0xFF3B728F)

// Disease Status
val HealthyGreen = Color(0xFF2D5A43)
val DiseaseRed = Color(0xFFBD3A3A)
val DiseaseOrange = Color(0xFFC28E0E)
val DiseaseYellow = Color(0xFFE5A93C)
