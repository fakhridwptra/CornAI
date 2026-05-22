package com.cornai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.repository.CornAIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CornAIRepository(application)

    val userName: StateFlow<String> = repository.currentUserName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guest")

    val userEmail: StateFlow<String> = repository.currentUserEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val isGuest: StateFlow<Boolean> = repository.isGuest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _stats = MutableStateFlow(Triple(0, 0, 0))
    val stats: StateFlow<Triple<Int, Int, Int>> = _stats.asStateFlow()

    init {
        observeStats()
    }

    private fun observeStats() {
        viewModelScope.launch {
            repository.getLocalHistoryFlow().collect { history ->
                val total = history.size
                val healthy = history.count { it.isHealthy }
                val disease = history.count { !it.isHealthy }
                _stats.value = Triple(total, healthy, disease)
            }
        }
    }

    fun loadStats() {
        // Observers handle data in real-time now, keeping signature for compatibility
        observeStats()
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}