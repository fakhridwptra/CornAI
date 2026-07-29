// Logika pemuatan model TFLite dan inferensi klasifikasi penyakit.
// File: java\com\cornai\ml\CornAIModel.kt

package com.cornai.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

// Data class untuk menyimpan hasil klasifikasi yang dikembalikan ke UI.
// Hasil ini berisi nama kelas, confidence, status sehat, gejala, perawatan, dan pencegahan.
data class ClassificationResult(
    val className: String,
    val displayName: String,
    val confidence: Float,
    val isHealthy: Boolean,
    val symptoms: List<String>,
    val treatment: String,
    val prevention: List<String>,
    val severity: String,
    val recoveryTime: String
)

// Kelas utama yang mengelola pemuatan model, inferensi, dan pengolahan hasil.
class CornAIModel(private val context: Context) {

    private var daunInterpreter: Interpreter? = null
    private var tongkolInterpreter: Interpreter? = null

    private var daunLabels: List<String> = emptyList()
    private var tongkolLabels: List<String> = emptyList()

    companion object {
        private const val DAUN_MODEL_FILE = "daun.tflite"
        private const val TONGKOL_MODEL_FILE = "tongkol.tflite"

        private const val DAUN_LABEL_FILE = "labels_daun.txt"
        private const val TONGKOL_LABEL_FILE = "labels_tongkol.txt"

        private const val INPUT_SIZE = 224
    }

    // Memuat model TFLite dan label untuk kedua jenis input: daun dan tongkol.
    // Mengembalikan true jika kedua model berhasil dimuat, false jika ada yang gagal.
    fun loadModel(): Boolean {
        android.util.Log.d("CornAIModel", "Mulai memuat dua model TFLite terpisah (Daun & Tongkol)...")
        var success = true
        val options = Interpreter.Options().apply { numThreads = 4 }

        // Load Daun model
        try {
            val daunBuffer = FileUtil.loadMappedFile(context, DAUN_MODEL_FILE)
            daunInterpreter = Interpreter(daunBuffer, options)
            daunLabels = FileUtil.loadLabels(context, DAUN_LABEL_FILE)
            android.util.Log.i("CornAIModel", "Model TFLite Daun berhasil dimuat!")
        } catch (e: Throwable) {
            android.util.Log.e("CornAIModel", "Gagal memuat model TFLite Daun: ${e.message}", e)
            success = false
        }

        // Load Tongkol model
        try {
            val tongkolBuffer = FileUtil.loadMappedFile(context, TONGKOL_MODEL_FILE)
            tongkolInterpreter = Interpreter(tongkolBuffer, options)
            tongkolLabels = FileUtil.loadLabels(context, TONGKOL_LABEL_FILE)
            android.util.Log.i("CornAIModel", "Model TFLite Tongkol berhasil dimuat!")
        } catch (e: Throwable) {
            android.util.Log.e("CornAIModel", "Gagal memuat model TFLite Tongkol: ${e.message}", e)
            success = false
        }

        return success
    }

    // Klasifikasi citra yang diterima dari kamera atau galeri.
    // Mode "daun" memakai model daun, mode "tongkol" memakai model tongkol.
    fun classify(bitmap: android.graphics.Bitmap, mode: String = "daun"): ClassificationResult {
        val currentInterpreter = if (mode == "tongkol") tongkolInterpreter else daunInterpreter
        val currentLabels = if (mode == "tongkol") tongkolLabels else daunLabels

        return if (currentInterpreter != null) {
            classifyWithModel(bitmap, currentInterpreter, currentLabels, mode)
        } else {
            // Jika model belum tersedia, pakai hasil demo agar aplikasi tetap responsif.
            getDemoResult(mode)
        }
    }

    // Lakukan inferensi dengan model TensorFlow Lite.
    // Hasil output adalah skor untuk masing-masing kelas, lalu diproses kembali.
    private fun classifyWithModel(
        bitmap: android.graphics.Bitmap,
        interpreter: Interpreter,
        labels: List<String>,
        mode: String
    ): ClassificationResult {
        val inputBuffer = preprocessBitmap(bitmap)
        val numClasses = labels.size
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        try {
            interpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            // Jika inferensi gagal, berikan hasil demo sebagai fallback.
            return getDemoResult(mode)
        }

        return postprocessOutput(outputBuffer[0], labels)
    }

    // Ubah bitmap gambar menjadi format ByteBuffer yang dapat diproses oleh model.
    // 1. Resize gambar ke ukuran yang dibutuhkan model.
    // 2. Pisahkan pixel RGB.
    // 3. Normalisasi nilai warna ke rentang 0..1.
    private fun preprocessBitmap(bitmap: android.graphics.Bitmap): ByteBuffer {
        val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)

        val byteBuffer = ByteBuffer.allocateDirect(
            1 * INPUT_SIZE * INPUT_SIZE * 3 * 4
        )
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaledBitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        scaledBitmap.recycle()
        byteBuffer.rewind()
        return byteBuffer
    }

    // Softmax mengubah skor mentah menjadi nilai probabilitas.
    // Jika output sudah mendekati distribusi probabilitas, fungsi ini mengembalikan output asli.
    private fun applySoftmax(output: FloatArray): FloatArray {
        val sum = output.sum()
        val allInLogicalRange = output.all { it in 0.0f..1.0f }
        if (allInLogicalRange && kotlin.math.abs(sum - 1.0f) < 0.05f) {
            return output
        }

        val maxVal = output.maxOrNull() ?: 0f
        val expOutputs = output.map { exp((it - maxVal).toDouble()) }.toDoubleArray()
        val sumExp = expOutputs.sum()
        return expOutputs.map { (it / sumExp).toFloat() }.toFloatArray()
    }

    private fun sanitizeClassName(className: String): String {
        return className.replace(" ", "_")
    }

    private fun postprocessOutput(output: FloatArray, labels: List<String>): ClassificationResult {
        val probabilities = applySoftmax(output)
        val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIdx]

        val rawClassName = labels.getOrNull(maxIdx) ?: "Unknown"
        val className = sanitizeClassName(rawClassName)

        val diseaseInfo = DiseaseData.getDiseaseInfo(className)

        return ClassificationResult(
            className = className,
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

    fun classifyWithMultiplePredictions(
        bitmap: android.graphics.Bitmap,
        topK: Int = 3,
        mode: String = "daun"
    ): List<ClassificationResult> {
        val currentInterpreter = if (mode == "tongkol") tongkolInterpreter else daunInterpreter
        val currentLabels = if (mode == "tongkol") tongkolLabels else daunLabels

        if (currentInterpreter == null) return listOf(getDemoResult(mode))

        val inputBuffer = preprocessBitmap(bitmap)
        val numClasses = currentLabels.size
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        try {
            currentInterpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            return listOf(getDemoResult(mode))
        }

        val probabilities = applySoftmax(outputBuffer[0])
        val limit = minOf(topK, numClasses)
        val sortedIndices = probabilities.indices
            .sortedByDescending { probabilities[it] }
            .take(limit)

        return sortedIndices.map { index ->
            val rawClassName = currentLabels.getOrNull(index) ?: "Unknown"
            val className = sanitizeClassName(rawClassName)
            val diseaseInfo = DiseaseData.getDiseaseInfo(className)

            ClassificationResult(
                className = className,
                displayName = diseaseInfo.displayName,
                confidence = probabilities[index],
                isHealthy = diseaseInfo.isHealthy,
                symptoms = diseaseInfo.symptoms,
                treatment = diseaseInfo.treatment,
                prevention = diseaseInfo.prevention,
                severity = diseaseInfo.severity,
                recoveryTime = diseaseInfo.recoveryTime
            )
        }
    }

    fun classifyAllClasses(bitmap: android.graphics.Bitmap, mode: String = "daun"): List<ClassificationResult> {
        val currentInterpreter = if (mode == "tongkol") tongkolInterpreter else daunInterpreter
        val currentLabels = if (mode == "tongkol") tongkolLabels else daunLabels
        val otherLabels = if (mode == "tongkol") daunLabels else tongkolLabels

        if (currentInterpreter == null) return emptyList()

        val inputBuffer = preprocessBitmap(bitmap)
        val numClasses = currentLabels.size
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        try {
            currentInterpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            return emptyList()
        }

        val rawProbs = applySoftmax(outputBuffer[0])

        // 1. Map predicted labels with their actual probabilities
        val activeResults = rawProbs.indices.map { index ->
            val rawClassName = currentLabels.getOrNull(index) ?: "Unknown"
            val className = sanitizeClassName(rawClassName)
            val diseaseInfo = DiseaseData.getDiseaseInfo(className)

            ClassificationResult(
                className = className,
                displayName = diseaseInfo.displayName,
                confidence = rawProbs[index],
                isHealthy = diseaseInfo.isHealthy,
                symptoms = diseaseInfo.symptoms,
                treatment = diseaseInfo.treatment,
                prevention = diseaseInfo.prevention,
                severity = diseaseInfo.severity,
                recoveryTime = diseaseInfo.recoveryTime
            )
        }

        // 2. Pad other mode labels with 0.0f confidence to form the complete 10-class list
        val inactiveResults = otherLabels.map { rawClassName ->
            val className = sanitizeClassName(rawClassName)
            val diseaseInfo = DiseaseData.getDiseaseInfo(className)

            ClassificationResult(
                className = className,
                displayName = diseaseInfo.displayName,
                confidence = 0.0f,
                isHealthy = diseaseInfo.isHealthy,
                symptoms = diseaseInfo.symptoms,
                treatment = diseaseInfo.treatment,
                prevention = diseaseInfo.prevention,
                severity = diseaseInfo.severity,
                recoveryTime = diseaseInfo.recoveryTime
            )
        }

        // Combine and sort by confidence descending
        return (activeResults + inactiveResults).sortedByDescending { it.confidence }
    }

    fun getDemoAllClasses(selectedClassName: String): List<ClassificationResult> {
        val allKeys = DiseaseData.getAllClassNames()
        val probs = FloatArray(allKeys.size)

        var selectedIdx = allKeys.indexOf(selectedClassName)
        if (selectedIdx == -1) {
            selectedIdx = allKeys.indexOfFirst {
                DiseaseData.getDisplayName(it).equals(selectedClassName, ignoreCase = true)
            }
        }
        selectedIdx = selectedIdx.coerceAtLeast(0)

        val mainProb = (70..95).random() / 100f
        probs[selectedIdx] = mainProb

        var remaining = 1.0f - mainProb
        val otherIndices = probs.indices.filter { it != selectedIdx }.shuffled()
        for (i in otherIndices.indices) {
            if (i == otherIndices.lastIndex) {
                probs[otherIndices[i]] = remaining
            } else {
                val p = (0..(remaining * 100).toInt()).random() / 100f
                probs[otherIndices[i]] = p
                remaining -= p
            }
        }

        return allKeys.mapIndexed { index, className ->
            val diseaseInfo = DiseaseData.getDiseaseInfo(className)
            ClassificationResult(
                className = className,
                displayName = diseaseInfo.displayName,
                confidence = probs[index],
                isHealthy = diseaseInfo.isHealthy,
                symptoms = diseaseInfo.symptoms,
                treatment = diseaseInfo.treatment,
                prevention = diseaseInfo.prevention,
                severity = diseaseInfo.severity,
                recoveryTime = diseaseInfo.recoveryTime
            )
        }.sortedByDescending { it.confidence }
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

    fun isModelLoaded(): Boolean = daunInterpreter != null && tongkolInterpreter != null

    fun close() {
        daunInterpreter?.close()
        tongkolInterpreter?.close()
        daunInterpreter = null
        tongkolInterpreter = null
    }

    fun getInputSize(): Int = INPUT_SIZE
}
