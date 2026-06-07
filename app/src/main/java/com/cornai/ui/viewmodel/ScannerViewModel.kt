package com.cornai.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cornai.data.model.ScanHistory
import com.cornai.data.model.UiState
import com.cornai.data.repository.CornAIRepository
import com.cornai.ml.ClassificationResult
import com.cornai.ml.CornAIModel
import com.cornai.ml.DiseaseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CornAIRepository(application)
    private val aiModel = CornAIModel(application)

    private val _scanMode = MutableStateFlow("daun")
    val scanMode: StateFlow<String> = _scanMode.asStateFlow()

    private val _scanState = MutableStateFlow<UiState<ClassificationResult>>(UiState.Idle)
    val scanState: StateFlow<UiState<ClassificationResult>> = _scanState.asStateFlow()

    private val _topPredictions = MutableStateFlow<List<ClassificationResult>>(emptyList())
    val topPredictions: StateFlow<List<ClassificationResult>> = _topPredictions.asStateFlow()

    private val _savedHistoryId = MutableStateFlow<String?>(null)
    val savedHistoryId: StateFlow<String?> = _savedHistoryId.asStateFlow()

    private val _liveResult = MutableStateFlow<ClassificationResult?>(null)
    val liveResult: StateFlow<ClassificationResult?> = _liveResult.asStateFlow()

    private val _isLiveScanning = MutableStateFlow(true)
    val isLiveScanning: StateFlow<Boolean> = _isLiveScanning.asStateFlow()

    val isModelLoaded: Boolean
        get() = aiModel.isModelLoaded()

    init {
        loadModel()
    }

    private fun loadModel() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val loaded = aiModel.loadModel()
                if (!loaded) {
                    // Model not found - will work with demo mode
                }
            }
        }
    }

    fun setScanMode(mode: String) {
        if (_scanMode.value != mode) {
            _scanMode.value = mode
            resetState()
        }
    }

    fun setLiveScanning(enabled: Boolean) {
        _isLiveScanning.value = enabled
        if (!enabled) {
            _liveResult.value = null
            _topPredictions.value = emptyList()
        }
    }

    fun classifyLiveFrame(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val currentMode = _scanMode.value
                val result = withContext(Dispatchers.Default) {
                    if (aiModel.isModelLoaded()) {
                        val singleResult = aiModel.classify(bitmap, currentMode)
                        android.util.Log.d("ScannerViewModel", "Live Scan ($currentMode) - Prediksi: ${singleResult.className}, Confidence: ${singleResult.confidence}")
                        if (singleResult.confidence >= 0.40f) {
                            val allResults = aiModel.classifyWithMultiplePredictions(bitmap, 3, currentMode)
                            _topPredictions.value = allResults
                            singleResult
                        } else {
                            _topPredictions.value = emptyList()
                            null
                        }
                    } else {
                        // Demo mode
                        getDemoResult(currentMode)
                    }
                }
                if (_isLiveScanning.value) {
                    _liveResult.value = result
                }
            } catch (e: Exception) {
                // Ignore live errors
            }
        }
    }

    fun lockAndSaveResult(result: ClassificationResult) {
        viewModelScope.launch {
            _scanState.value = UiState.Loading
            try {
                val id = repository.saveLocalScan(
                    diseaseName = result.className,
                    displayName = result.displayName,
                    confidence = result.confidence,
                    isHealthy = result.isHealthy,
                    symptoms = result.symptoms,
                    treatment = result.treatment,
                    prevention = result.prevention,
                    severity = result.severity,
                    recoveryTime = result.recoveryTime
                )
                _savedHistoryId.value = id
                _scanState.value = UiState.Success(result)
            } catch (e: Exception) {
                _scanState.value = UiState.Error(e.message ?: "Gagal menyimpan hasil")
            }
        }
    }

    fun classifyImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _scanState.value = UiState.Loading
            val currentMode = _scanMode.value

            try {
                val result = withContext(Dispatchers.Default) {
                    if (aiModel.isModelLoaded()) {
                        val singleResult = aiModel.classify(bitmap, currentMode)
                        android.util.Log.d("ScannerViewModel", "Gallery/Manual ($currentMode) - Prediksi: ${singleResult.className}, Confidence: ${singleResult.confidence}")
                        if (singleResult.confidence >= 0.40f) {
                            val allResults = aiModel.classifyWithMultiplePredictions(bitmap, 3, currentMode)
                            _topPredictions.value = allResults
                            singleResult
                        } else {
                            throw Exception("Objek tidak dikenali (Keyakinan: ${(singleResult.confidence * 100).toInt()}%). Pastikan Anda memindai daun atau tongkol jagung dengan jelas.")
                        }
                    } else {
                        // Demo mode - return mock result
                        getDemoResult(currentMode)
                    }
                }

                _scanState.value = UiState.Success(result)
            } catch (e: Exception) {
                _scanState.value = UiState.Error(e.message ?: "Klasifikasi gagal")
            }
        }
    }

    fun saveScanResult(result: ClassificationResult) {
        viewModelScope.launch {
            try {
                val id = repository.saveLocalScan(
                    diseaseName = result.className,
                    displayName = result.displayName,
                    confidence = result.confidence,
                    isHealthy = result.isHealthy,
                    symptoms = result.symptoms,
                    treatment = result.treatment,
                    prevention = result.prevention,
                    severity = result.severity,
                    recoveryTime = result.recoveryTime
                )
                _savedHistoryId.value = id
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun resetState() {
        _scanState.value = UiState.Idle
        _savedHistoryId.value = null
        _topPredictions.value = emptyList()
        _liveResult.value = null
    }

    private fun getDemoResult(mode: String): ClassificationResult {
        val diseaseName = if (mode == "tongkol") {
            listOf("Healthy_Tongkol", "Unhealthy_Tongkol").random()
        } else {
            listOf("Healthy_Daun", "Common_Rust", "Blight", "Gray_Leaf_Spot", "Bipolaris", "Stenocarpella", "Bacterial_Leaf_Streak", "Asphalt_stain").random()
        }
        val confidence = (75..98).random() / 100.0f
        val diseaseInfo = DiseaseData.getDiseaseInfo(diseaseName)

        return ClassificationResult(
            className = diseaseName,
            displayName = diseaseInfo.displayName,
            confidence = confidence,
            isHealthy = diseaseInfo.isHealthy,
            symptoms = diseaseInfo.symptoms,
            treatment = diseaseInfo.treatment,
            prevention = diseaseInfo.prevention,
            severity = diseaseInfo.severity,
            recoveryTime = diseaseInfo.recoveryTime
        )
    }

    override fun onCleared() {
        super.onCleared()
        aiModel.close()
    }
}