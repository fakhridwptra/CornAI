package com.cornai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.local.PreferencesManager
import com.cornai.data.model.UiState
import com.cornai.data.repository.CornAIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = CornAIRepository(application)

    // State flows from preferences
    val isLoggedIn: StateFlow<Boolean> = preferencesManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isGuest: StateFlow<Boolean> = preferencesManager.isGuest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guest")

    val userEmail: StateFlow<String> = preferencesManager.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val hasSeenOnboarding: StateFlow<Boolean> = preferencesManager.hasSeenOnboarding
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // UI state for auth operations
    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    fun updateLoginEmail(email: String) {
        _loginEmail.value = email
    }

    fun updateLoginPassword(password: String) {
        _loginPassword.value = password
    }

    fun signIn() {
        if (_loginEmail.value.isBlank() || _loginPassword.value.isBlank()) {
            _authState.value = UiState.Error("Email dan password harus diisi")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val user = repository.authenticateUser(_loginEmail.value, _loginPassword.value)
                if (user != null) {
                    preferencesManager.saveUserSession(
                        userId = user.email,
                        userName = user.name,
                        userEmail = user.email,
                        userPhoto = "",
                        isGuest = false
                    )
                    _authState.value = UiState.Success(Unit)
                } else {
                    _authState.value = UiState.Error("Email atau password salah")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun signUp(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val success = repository.registerUser(name, email, phone, password)
                if (success) {
                    preferencesManager.saveUserSession(
                        userId = email,
                        userName = name,
                        userEmail = email,
                        userPhoto = "",
                        isGuest = false
                    )
                    _authState.value = UiState.Success(Unit)
                } else {
                    _authState.value = UiState.Error("Email sudah terdaftar")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Registrasi gagal")
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val userId = "guest_${System.currentTimeMillis()}"
                preferencesManager.saveUserSession(
                    userId = userId,
                    userName = "Guest",
                    userEmail = "",
                    userPhoto = "",
                    isGuest = true
                )
                _authState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Gagal masuk sebagai tamu")
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                // Simulate sending reset email
                kotlinx.coroutines.delay(1000)
                _authState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.message ?: "Gagal kirim reset password")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            preferencesManager.clearUserSession()
            _authState.value = UiState.Idle
            _loginEmail.value = ""
            _loginPassword.value = ""
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingComplete()
        }
    }

    fun resetState() {
        _authState.value = UiState.Idle
    }
}