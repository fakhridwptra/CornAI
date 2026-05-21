package com.cornai.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

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

class CornAIModel(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    companion object {
        private const val MODEL_FILE = "cornai_model.tflite"
        private const val LABEL_FILE = "labels.txt"
        private const val INPUT_SIZE = 224
        private const val NUM_CLASSES = 10
    }

    fun loadModel(): Boolean {
        return try {
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            val options = Interpreter.Options().apply {
                numThreads = 4
                useNNAPI = true
            }
            interpreter = Interpreter(modelBuffer, options)

            labels = try {
                FileUtil.loadLabels(context, LABEL_FILE)
            } catch (e: Exception) {
                listOf(
                    "Healthy_Daun", "Healthy_Tongkol", "Common_Rust",
                    "Gray_Leaf_Spot", "Blight", "Bipolaris",
                    "Stenocarpella", "Bacterial_Leaf_Streak",
                    "Asphalt_stain", "Unhealthy_Tongkol"
                )
            }
            true
        } catch (e: Exception) {
            // Model not found - will use demo mode
            false
        }
    }

    fun classify(bitmap: android.graphics.Bitmap): ClassificationResult {
        val interpreter = this.interpreter

        return if (interpreter != null) {
            classifyWithModel(bitmap, interpreter)
        } else {
            // Demo mode - return mock result
            getDemoResult()
        }
    }

    private fun classifyWithModel(bitmap: android.graphics.Bitmap, interpreter: Interpreter): ClassificationResult {
        // Preprocess
        val inputBuffer = preprocessBitmap(bitmap)

        // Run inference
        val outputBuffer = Array(1) { FloatArray(NUM_CLASSES) }

        try {
            interpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            return getDemoResult()
        }

        // Postprocess
        return postprocessOutput(outputBuffer[0])
    }

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

        byteBuffer.rewind()
        return byteBuffer
    }

    private fun applySoftmax(output: FloatArray): FloatArray {
        val maxVal = output.maxOrNull() ?: 0f
        val expOutputs = output.map { exp((it - maxVal).toDouble()) }.toDoubleArray()
        val sumExp = expOutputs.sum()
        return expOutputs.map { (it / sumExp).toFloat() }.toFloatArray()
    }

    private fun postprocessOutput(output: FloatArray): ClassificationResult {
        val probabilities = applySoftmax(output)
        val maxIdx = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val confidence = probabilities[maxIdx]
        val className = labels.getOrNull(maxIdx) ?: "Unknown"

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

    fun classifyWithMultiplePredictions(bitmap: android.graphics.Bitmap, topK: Int = 3): List<ClassificationResult> {
        val interpreter = this.interpreter ?: return listOf(getDemoResult())

        val inputBuffer = preprocessBitmap(bitmap)
        val outputBuffer = Array(1) { FloatArray(NUM_CLASSES) }

        try {
            interpreter.run(inputBuffer, outputBuffer)
        } catch (e: Exception) {
            return listOf(getDemoResult())
        }

        val probabilities = applySoftmax(outputBuffer[0])
        val topIndices = probabilities.indices
            .sortedByDescending { probabilities[it] }
            .take(topK)

        return topIndices.map { index ->
            val className = labels.getOrNull(index) ?: "Unknown"
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

    private fun getDemoResult(): ClassificationResult {
        // Demo results for testing
        val demoClasses = listOf(
            Triple("Common_Rust", 0.89f, false),
            Triple("Blight", 0.94f, false),
            Triple("Healthy_Daun", 0.97f, true),
            Triple("Gray_Leaf_Spot", 0.91f, false),
            Triple("Healthy_Tongkol", 0.98f, true),
            Triple("Bipolaris", 0.88f, false)
        )

        val (diseaseName, confidence, isHealthy) = demoClasses.random()
        val diseaseInfo = DiseaseData.getDiseaseInfo(diseaseName)

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

    fun isModelLoaded(): Boolean = interpreter != null

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    fun getInputSize(): Int = INPUT_SIZE
}