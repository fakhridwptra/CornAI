package com.cornai.data.model

data class ScanHistory(
    val id: String = "",
    val userId: String = "",
    val diseaseName: String = "",
    val displayName: String = "",
    val confidence: Float = 0f,
    val isHealthy: Boolean = true,
    val imageUri: String = "",
    val symptoms: List<String> = emptyList(),
    val treatment: String = "",
    val prevention: List<String> = emptyList(),
    val severity: String = "",
    val recoveryTime: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val weather: String = "",
    val isSynced: Boolean = false
)

data class DiseaseInfo(
    val name: String,
    val displayName: String,
    val isHealthy: Boolean,
    val symptoms: List<String>,
    val treatment: String,
    val prevention: List<String>,
    val severity: String = "",
    val recoveryTime: String = ""
)