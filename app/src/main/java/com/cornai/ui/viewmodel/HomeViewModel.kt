package com.cornai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.model.ScanHistory
import com.cornai.data.repository.CornAIRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WeatherData(
    val condition: String,
    val temperature: Int,
    val recommendation: String,
    val emoji: String,
    val timeOfDay: String
)

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

    private val _weatherState = MutableStateFlow(
        WeatherData("Cerah", 32, "Ideal untuk menyemprot pestisida", "🌤️", "Siang")
    )
    val weatherState: StateFlow<WeatherData> = _weatherState.asStateFlow()

    init {
        observeHistory()
        startWeatherUpdates()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.getLocalHistoryFlow().collect { history ->
                _recentScans.value = history.take(3)
                _totalScans.value = history.size
                _healthyScans.value = history.count { it.isHealthy }
                _diseaseScans.value = history.count { !it.isHealthy }
            }
        }
    }

    private fun startWeatherUpdates() {
        viewModelScope.launch {
            val conditions = listOf(
                WeatherData("Cerah", 32, "Ideal untuk menyemprot pestisida", "🌤️", "Siang"),
                WeatherData("Cerah Berawan", 30, "Cocok untuk pemupukan nitrogen", "⛅", "Siang"),
                WeatherData("Berawan", 28, "Bagus untuk memeriksa penyakit daun", "☁️", "Siang"),
                WeatherData("Mendung", 27, "Suhu ideal, angin mungkin bertiup kencang", "🌥️", "Sore"),
                WeatherData("Hujan Ringan", 24, "Tunda penyemprotan, cari perlindungan", "🌧️", "Sore"),
                WeatherData("Cerah Pagi", 26, "Waktu terbaik untuk memeriksa embun tepung", "☀️", "Pagi")
            )
            var index = 0
            while (true) {
                kotlinx.coroutines.delay(10000) // update every 10 seconds
                index = (index + 1) % conditions.size
                val base = conditions[index]
                val randomOffset = (-1..1).random()
                _weatherState.value = base.copy(temperature = base.temperature + randomOffset)
            }
        }
    }

    fun loadData() {
        // Observers handle data in real-time now, keeping signature for backwards compatibility
        observeHistory()
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