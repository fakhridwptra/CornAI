package com.cornai.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cornai.data.local.CornAIDatabase
import com.cornai.data.local.PreferencesManager
import com.cornai.data.local.ScanHistoryEntity
import com.cornai.data.model.ScanHistory
import com.cornai.ml.CornAIModel
import com.cornai.ml.DiseaseData
import com.cornai.ml.ClassificationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "cornai_session")

class CornAIRepository(
    private val context: Context
) {
    private val preferencesManager = PreferencesManager(context)
    private val database = androidx.room.Room.databaseBuilder(
        context,
        CornAIDatabase::class.java,
        "cornai_database"
    ).fallbackToDestructiveMigration().build()
    private val scanHistoryDao = database.scanHistoryDao()
    private val aiModel = CornAIModel(context)

    // Flows
    val isLoggedIn: Flow<Boolean> = preferencesManager.isLoggedIn
    val isGuest: Flow<Boolean> = preferencesManager.isGuest
    val userName: Flow<String> = preferencesManager.userName
    val userEmail: Flow<String> = preferencesManager.userEmail
    val hasSeenOnboarding: Flow<Boolean> = preferencesManager.hasSeenOnboarding

    // ========== USER SESSION ==========

    suspend fun getCurrentUserId(): String {
        return preferencesManager.userId.first().ifEmpty {
            "demo_temp"
        }
    }

    suspend fun saveUserSession(userId: String, name: String, email: String, isGuest: Boolean) {
        preferencesManager.saveUserSession(
            userId = userId,
            userName = name,
            userEmail = email,
            isGuest = isGuest
        )
    }

    suspend fun clearSession() {
        preferencesManager.clearUserSession()
    }

    // ========== AI CLASSIFICATION ==========

    suspend fun classifyImage(bitmap: Bitmap): ClassificationResult = withContext(Dispatchers.Default) {
        if (aiModel.isModelLoaded()) {
            aiModel.classify(bitmap)
        } else {
            // Demo mode - return random result
            getDemoResult()
        }
    }

    suspend fun classifyWithDemo(): ClassificationResult = withContext(Dispatchers.Default) {
        getDemoResult()
    }

    private fun getDemoResult(): ClassificationResult {
        val demoClasses = listOf(
            Triple("Common_Rust", 0.89f, false),
            Triple("Northern Leaf Blight", 0.94f, false),
            Triple("Healthy_Daun", 0.97f, true),
            Triple("Gray_Leaf_Spot", 0.91f, false),
            Triple("Healthy_Tongkol", 0.98f, true),
            Triple("Blight", 0.92f, false),
            Triple("Common_Rust", 0.95f, false)
        )
        val (diseaseName, confidence, isHealthy) = demoClasses.random()
        val diseaseInfo = DiseaseData.getDiseaseInfo(
            demoClasses.find { it.first == diseaseName }?.first
                ?: "Healthy_Daun"
        )

        return ClassificationResult(
            className = diseaseName,
            displayName = diseaseInfo.displayName,
            confidence = confidence,
            isHealthy = isHealthy,
            symptoms = diseaseInfo.symptoms,
            treatment = diseaseInfo.treatment,
            prevention = diseaseInfo.prevention,
            severity = diseaseInfo.severity,
            recoveryTime = diseaseInfo.recoveryTime
        )
    }

    // ========== SCAN HISTORY ==========

    fun getHistoryFlow(): Flow<List<ScanHistory>> {
        return scanHistoryDao.getHistoryByUser("demo").let { flow ->
            kotlinx.coroutines.flow.flow {
                // Simple conversion
                emit(emptyList())
            }
        }
    }

    suspend fun saveScan(result: ClassificationResult): String {
        val userId = getCurrentUserId()
        val id = UUID.randomUUID().toString()

        val entity = ScanHistoryEntity(
            id = id,
            userId = userId,
            diseaseName = result.className,
            displayName = result.displayName,
            confidence = result.confidence,
            isHealthy = result.isHealthy,
            imageUri = "",
            symptoms = JSONArray(result.symptoms).toString(),
            treatment = result.treatment,
            prevention = JSONArray(result.prevention).toString(),
            severity = result.severity,
            recoveryTime = result.recoveryTime,
            timestamp = System.currentTimeMillis(),
            location = "",
            weather = "",
            isSynced = false
        )

        scanHistoryDao.insert(entity)
        return id
    }

    suspend fun getHistory(): List<ScanHistory> {
        val userId = getCurrentUserId()
        val entities = scanHistoryDao.getHistoryByUserSync(userId)
        return entities.map { it.toScanHistory() }
    }

    suspend fun deleteScan(id: String) {
        scanHistoryDao.deleteById(id)
    }

    suspend fun getStats(): Triple<Int, Int, Int> {
        val userId = getCurrentUserId()
        return Triple(
            scanHistoryDao.getCount(userId),
            scanHistoryDao.getHealthyCount(userId),
            scanHistoryDao.getDiseaseCount(userId)
        )
    }

    // ========== ONBOARDING ==========

    suspend fun completeOnboarding() {
        preferencesManager.setOnboardingComplete()
    }

    // ========== HELPER PROPERTIES (aliases for ViewModel compatibility) ==========

    val currentUserName: Flow<String> = userName
    val currentUserEmail: Flow<String> = userEmail

    // ========== HELPER METHODS (aliases for ViewModel compatibility) ==========

    fun getLocalHistoryFlow(): Flow<List<ScanHistory>> {
        return kotlinx.coroutines.flow.flow {
            emit(getHistory())
        }
    }

    suspend fun saveLocalScan(
        diseaseName: String,
        displayName: String,
        confidence: Float,
        isHealthy: Boolean,
        symptoms: List<String>,
        treatment: String,
        prevention: List<String>,
        severity: String,
        recoveryTime: String
    ): String {
        val result = ClassificationResult(
            className = diseaseName,
            displayName = displayName,
            confidence = confidence,
            isHealthy = isHealthy,
            symptoms = symptoms,
            treatment = treatment,
            prevention = prevention,
            severity = severity,
            recoveryTime = recoveryTime
        )
        return saveScan(result)
    }

    suspend fun getLocalStats(): Triple<Int, Int, Int> {
        return getStats()
    }

    suspend fun deleteLocalScan(id: String) {
        deleteScan(id)
    }

    suspend fun syncToCloud() {
        // No-op in demo mode
    }

    suspend fun syncFromCloud() {
        // No-op in demo mode
    }

    suspend fun signOut() {
        clearSession()
    }

    // ========== HELPER ==========

    private fun ScanHistoryEntity.toScanHistory(): ScanHistory {
        val symptomsList = try {
            val jsonArray = JSONArray(symptoms)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }

        val preventionList = try {
            val jsonArray = JSONArray(prevention)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }

        return ScanHistory(
            id = id,
            userId = userId,
            diseaseName = diseaseName,
            displayName = displayName,
            confidence = confidence,
            isHealthy = isHealthy,
            imageUri = imageUri,
            symptoms = symptomsList,
            treatment = treatment,
            prevention = preventionList,
            severity = severity,
            recoveryTime = recoveryTime,
            timestamp = timestamp,
            location = location,
            weather = weather,
            isSynced = isSynced
        )
    }
}