package com.cornai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.model.ScanHistory
import com.cornai.data.repository.CornAIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CornAIRepository(application)

    val userName: StateFlow<String> = repository.currentUserName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guest")

    val isGuest: StateFlow<Boolean> = repository.isGuest
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _recentScans = MutableStateFlow<List<ScanHistory>>(emptyList())
    val recentScans: StateFlow<List<ScanHistory>> = _recentScans.asStateFlow()

    private val _totalScans = MutableStateFlow(0)
    val totalScans: StateFlow<Int> = _totalScans.asStateFlow()

    private val _healthyScans = MutableStateFlow(0)
    val healthyScans: StateFlow<Int> = _healthyScans.asStateFlow()

    private val _diseaseScans = MutableStateFlow(0)
    val diseaseScans: StateFlow<Int> = _diseaseScans.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getLocalHistoryFlow().collect { history ->
                _recentScans.value = history.take(3)
                _totalScans.value = history.size
                _healthyScans.value = history.count { it.isHealthy }
                _diseaseScans.value = history.count { !it.isHealthy }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                repository.syncToCloud()
                repository.syncFromCloud()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}