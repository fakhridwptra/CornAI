package com.cornai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cornai_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val IS_GUEST = booleanPreferencesKey("is_guest")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PHOTO = stringPreferencesKey("user_photo")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val LANGUAGE = stringPreferencesKey("language")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val REMINDER_WATERING = booleanPreferencesKey("reminder_watering")
        private val REMINDER_SPRAYING = booleanPreferencesKey("reminder_spraying")
        private val REMINDER_DAYS = intPreferencesKey("reminder_days")
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    val isGuest: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[IS_GUEST] ?: false
        }

    val userId: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[USER_ID] ?: ""
        }

    val userName: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[USER_NAME] ?: "Guest"
        }

    val userEmail: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[USER_EMAIL] ?: ""
        }

    val userPhoto: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[USER_PHOTO] ?: ""
        }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            preferences[HAS_SEEN_ONBOARDING] ?: false
        }

    suspend fun saveUserSession(
        userId: String,
        userName: String,
        userEmail: String,
        userPhoto: String = "",
        isGuest: Boolean = false
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[IS_GUEST] = isGuest
            preferences[USER_ID] = userId
            preferences[USER_NAME] = userName
            preferences[USER_EMAIL] = userEmail
            preferences[USER_PHOTO] = userPhoto
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences[IS_GUEST] = false
            preferences[USER_ID] = ""
            preferences[USER_NAME] = ""
            preferences[USER_EMAIL] = ""
            preferences[USER_PHOTO] = ""
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING] = true
        }
    }

    suspend fun saveNotificationSettings(
        notificationsEnabled: Boolean,
        reminderWatering: Boolean,
        reminderSpraying: Boolean,
        reminderDays: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = notificationsEnabled
            preferences[REMINDER_WATERING] = reminderWatering
            preferences[REMINDER_SPRAYING] = reminderSpraying
            preferences[REMINDER_DAYS] = reminderDays
        }
    }
}