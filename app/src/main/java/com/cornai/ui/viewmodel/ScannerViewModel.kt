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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CornAIRepository(application)
    private val aiModel = CornAIModel(application)

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
                val result = withContext(Dispatchers.Default) {
                    if (aiModel.isModelLoaded()) {
                        val singleResult = aiModel.classify(bitmap)
                        val allResults = aiModel.classifyWithMultiplePredictions(bitmap, 3)
                        _topPredictions.value = allResults
                        singleResult
                    } else {
                        // Demo mode
                        getDemoResult()
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

            try {
                val result = withContext(Dispatchers.Default) {
                    if (aiModel.isModelLoaded()) {
                        val singleResult = aiModel.classify(bitmap)
                        val allResults = aiModel.classifyWithMultiplePredictions(bitmap, 3)
                        _topPredictions.value = allResults
                        singleResult
                    } else {
                        // Demo mode - return mock result
                        getDemoResult()
                    }
                }

                _scanState.value = UiState.Success(result)
            } catch (e: Exception) {
                _scanState.value = UiState.Error(e.message ?: "Classification failed")
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

    private fun getDemoResult(): ClassificationResult {
        // Demo result for testing without model
        val demoResults = listOf(
            ClassificationResult(
                className = "Common_Rust",
                displayName = "Common Rust",
                confidence = 0.89f,
                isHealthy = false,
                symptoms = listOf(
                    "Bintik-bintik coklat/orange kecil",
                    "Daun menguning lebih awal"
                ),
                treatment = "Gunakan fungisida propiconazole",
                prevention = listOf("Gunakan varietas tahan", "Hindari tanam terlalu rapat"),
                severity = "Sedang",
                recoveryTime = "2-3 Minggu"
            ),
            ClassificationResult(
                className = "Healthy_Daun",
                displayName = "Healthy Leaf",
                confidence = 0.97f,
                isHealthy = true,
                symptoms = emptyList(),
                treatment = "Tanaman sehat!",
                prevention = listOf("Lanjutkan perawatan normal"),
                severity = "N/A",
                recoveryTime = "N/A"
            )
        )
        return demoResults.random()
    }

    override fun onCleared() {
        super.onCleared()
        aiModel.close()
    }
}