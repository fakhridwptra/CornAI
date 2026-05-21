package com.cornai.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserPreferences(
    val isDarkMode: Boolean = false,
    val language: String = "id",
    val notificationsEnabled: Boolean = true,
    val reminderWatering: Boolean = true,
    val reminderSpraying: Boolean = true,
    val reminderDays: Int = 3
)