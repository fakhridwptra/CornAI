package com.cornai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.model.ScanHistory
import com.cornai.data.repository.CornAIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryStats(
    val totalScans: Int = 0,
    val healthyCount: Int = 0,
    val diseaseCount: Int = 0
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CornAIRepository(application)

    private val _historyList = MutableStateFlow<List<ScanHistory>>(emptyList())
    val historyList: StateFlow<List<ScanHistory>> = _historyList.asStateFlow()

    private val _stats = MutableStateFlow(HistoryStats())
    val stats: StateFlow<HistoryStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredHistory: StateFlow<List<ScanHistory>> = combine(
        _historyList,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.diseaseName.contains(query, ignoreCase = true) ||
                    it.displayName.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getLocalHistoryFlow().collect { history ->
                    _historyList.value = history
                    updateStats(history)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.syncFromCloud()
                repository.syncToCloud()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteHistory(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteLocalScan(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun updateStats(history: List<ScanHistory>) {
        _stats.value = HistoryStats(
            totalScans = history.size,
            healthyCount = history.count { it.isHealthy },
            diseaseCount = history.count { !it.isHealthy }
        )
    }
}