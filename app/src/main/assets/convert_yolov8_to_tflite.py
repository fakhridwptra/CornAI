"""
Script untuk convert YOLOv8 Classification model ke TFLite
===========================================================
Usage:
    python convert_yolov8_to_tflite.py

Dependencies:
    pip install ultralytics torch tf2onnx onnx onnx-tflite
"""

import os
import torch
import numpy as np
from pathlib import Path

# ========================
# KONFIGURASI
# ========================
MODEL_PATH = "../Hasil Model Terbaru/YOLOv8_Classification_Result/yolov8_classification_best.pt"
OUTPUT_DIR = "./output"
INPUT_SIZE = 224
NUM_CLASSES = 10

# Nama kelas (sesuai urutan training)
CLASS_NAMES = [
    "Healthy_Daun",       # 0
    "Healthy_Tongkol",    # 1
    "Common_Rust",        # 2
    "Gray_Leaf_Spot",     # 3
    "Blight",             # 4
    "Bipolaris",          # 5
    "Stenocarpella",      # 6
    "Bacterial_Leaf_Streak", # 7
    "Asphalt_stain",      # 8
    "Unhealthy_Tongkol"   # 9
]

# ========================
# DISPLAY NAMES
# ========================
DISPLAY_NAMES = {
    "Healthy_Daun": "Healthy Leaf",
    "Healthy_Tongkol": "Healthy Cob",
    "Common_Rust": "Common Rust",
    "Gray_Leaf_Spot": "Gray Leaf Spot",
    "Blight": "Northern Leaf Blight",
    "Bipolaris": "Bipolaris",
    "Stenocarpella": "Stenocarpella",
    "Bacterial_Leaf_Streak": "Bacterial Leaf Streak",
    "Asphalt_stain": "Asphalt Stain",
    "Unhealthy_Tongkol": "Unhealthy Cob"
}

def create_class_labels_file():
    """Buat file labels untuk Android"""
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # File untuk TensorFlow Lite (hanya nama kelas)
    with open(f"{OUTPUT_DIR}/labels.txt", "w") as f:
        for name in CLASS_NAMES:
            f.write(f"{name}\n")

    # File JSON untuk display names
    import json
    with open(f"{OUTPUT_DIR}/class_info.json", "w") as f:
        info = {
            "num_classes": NUM_CLASSES,
            "input_size": INPUT_SIZE,
            "class_names": CLASS_NAMES,
            "display_names": DISPLAY_NAMES,
            "disease_info": {
                "Healthy_Daun": {
                    "is_healthy": True,
                    "symptoms": [],
                    "treatment": "Tanaman sehat! Lanjutkan perawatan normal.",
                    "prevention": ["Penyiraman teratur", "Pupuk berkala", "Pastikan sinar matahari cukup"]
                },
                "Healthy_Tongkol": {
                    "is_healthy": True,
                    "symptoms": [],
                    "treatment": "Tongkol jagung sehat! Panen pada waktu yang tepat.",
                    "prevention": ["Lindungi dari hama", "Jaga kelembaban"]
                },
                "Common_Rust": {
                    "is_healthy": False,
                    "symptoms": [
                        "Bintik-bintik coklat/orange kecil di kedua permukaan daun",
                        "Bintik membesar dan berubah warna coklat gelap",
                        "Daun menguning lebih awal",
                        "Tanaman terlihat kerdil"
                    ],
                    "treatment": "Gunakan fungisida sistemik seperti propiconazole atau triademefon. Perbaiki drainase dan pastikan sirkulasi udara baik.",
                    "prevention": [
                        "Gunakan varietas tahan karat",
                        "Hindari penanaman terlalu rapat",
                        "Buang daun yang terinfeksi berat",
                        "Semprot fungisida preventif saat gejala pertama"
                    ]
                },
                "Gray_Leaf_Spot": {
                    "is_healthy": False,
                    "symptoms": [
                        "Bercak kecil berwarna coklat kemerahan",
                        "Bercak berkembang menjadi abu-abu rectangular",
                        "Pelepah daun membusuk",
                        "Penurunan hasil yang signifikan"
                    ],
                    "treatment": "Gunakan fungisida golongan strobilurin. Kurangi residu tanaman dan atur pengairan untuk menghindari genangan.",
                    "prevention": [
                        "Gunakan varietas tahan penyakit",
                        "Praktikkan crop rotation",
                        "Bakar atau kubur sisa tanaman",
                        "Hindari penanaman berulang di lahan yang sama"
                    ]
                },
                "Blight": {
                    "is_healthy": False,
                    "symptoms": [
                        "Bercak berbentuk oval memanjang",
                        "Lesi dimulai dari daun bawah",
                        "Bercak dapat mencapai 2-15 cm",
                        "Daun menguning dan mati prematur"
                    ],
                    "treatment": "Semprotkan fungisida berbasis mancozeb atau azoxystrobin. Buang dan musnahkan sisa tanaman yang terinfeksi.",
                    "prevention": [
                        "Pilih varietas tahan hawar daun",
                        "Terapkan jarak tanam yang tepat",
                        "Buang sisa tanaman setelah panen",
                        "Pastikan drainase tanah baik"
                    ]
                },
                "Bipolaris": {
                    "is_healthy": False,
                    "symptoms": [
                        "Bercak coklat dengan halo kuning",
                        "Lesi berbentuk elips atau V",
                        "Daun mengering dari ujung",
                        "Penurunan fotosintesis"
                    ],
                    "treatment": "Aplikasi fungisida efektif seperti mancozeb, chlorothalonil, atau strobilurin. Sanitasi lahan.",
                    "prevention": [
                        "Gunakan varietas tahan",
                        "Rotasi tanaman",
                        "Hindari stres air",
                        "Buang sisa tanaman terinfeksi"
                    ]
                },
                "Stenocarpella": {
                    "is_healthy": False,
                    "symptoms": [
                        "Busuk pada pangkal tongkol",
                        "Benjolan hitam pada batang",
                        "Batang mudah patah",
                        "Tongkol menjadi hitam dan busuk"
                    ],
                    "treatment": "Cabut dan musnahkan tanaman yang terinfeksi. Gunakan fungisida jika diperlukan.",
                    "prevention": [
                        "Gunakan benih sehat bersertifikat",
                        "Rotasi tanaman 2-3 musim",
                        "Buang sisa tanaman sakit",
                        "Tanam di musim yang tepat"
                    ]
                },
                "Bacterial_Leaf_Streak": {
                    "is_healthy": False,
                    "symptoms": [
                        "Garis-garis coklat pada daun",
                        "Lesi panjang mengikuti tulang daun",
                        "Daun terlihat seperti terbakar",
                        "Sekresi bakteri berwarna kuning"
                    ],
                    "treatment": "Tidak ada fungisida yang efektif untuk bakteri. Fokus pada pencegahan dan sanitasi.",
                    "prevention": [
                        "Gunakan varietas tahan",
                        "Hindari kerja di sawah saat daun basah",
                        "Sanitasi peralatan",
                        "Buang tanaman yang terinfeksi berat"
                    ]
                },
                "Asphalt_stain": {
                    "is_healthy": False,
                    "symptoms": [
                        "Bercak hitam seperti aspal di permukaan daun",
                        "Biasanya muncul setelah hujan deras",
                        "Tidak menyebar seperti penyakit fungal",
                        "Mostly cosmetic, tidak fatal"
                    ],
                    "treatment": "Tidak memerlukan treatment khusus. Bercak bersifat kosmetik dan tidak mempengaruhi pertumbuhan.",
                    "prevention": [
                        "Tidak ada pencegahan spesifik",
                        "Ini bukan penyakit, hanya noda",
                        "Tanaman tetap sehat"
                    ]
                },
                "Unhealthy_Tongkol": {
                    "is_healthy": False,
                    "symptoms": [
                        "Tongkol berwarna kecoklatan",
                        "Butir tidak terbentuk sempurna",
                        "Serat-serat tongkol membusuk",
                        "bau tidak sedap"
                    ],
                    "treatment": "Cabut tongkol yang terinfeksi dan musnahkan. Jangan gunakan untuk benih.",
                    "prevention": [
                        "Gunakan benih unggul bersertifikat",
                        "Jaga kebersihan lahan",
                        "Panen di waktu yang tepat",
                        "Simpan hasil panen dengan baik"
                    ]
                }
            }
        }
        json.dump(info, f, indent=2)

    print(f"✅ Created labels.txt and class_info.json")


def convert_torch_to_onnx():
    """Convert PyTorch model ke ONNX"""
    print("\n" + "="*50)
    print("Step 1: Load PyTorch model")
    print("="*50)

    try:
        from ultralytics import YOLO
    except ImportError:
        print("❌ ultralytics not installed")
        print("Run: pip install ultralytics")
        return None

    # Load model
    print(f"Loading model from: {MODEL_PATH}")
    model = YOLO(MODEL_PATH)

    # Get the classification model
    if hasattr(model, 'model'):
        torch_model = model.model
        torch_model.eval()
        print("✅ Model loaded successfully")
    else:
        print("❌ Could not extract model")
        return None

    # Create dummy input
    dummy_input = torch.randn(1, 3, INPUT_SIZE, INPUT_SIZE)

    # Output path for ONNX
    onnx_path = f"{OUTPUT_DIR}/yolov8_classification.onnx"

    print("\n" + "="*50)
    print("Step 2: Convert to ONNX")
    print("="*50)

    try:
        torch.onnx.export(
            torch_model,
            dummy_input,
            onnx_path,
            export_params=True,
            opset_version=12,
            do_constant_folding=True,
            input_names=['input'],
            output_names=['output'],
            dynamic_axes={
                'input': {0: 'batch_size'},
                'output': {0: 'batch_size'}
            }
        )
        print(f"✅ ONNX model saved: {onnx_path}")
        print(f"   Size: {os.path.getsize(onnx_path) / (1024*1024):.2f} MB")
        return onnx_path
    except Exception as e:
        print(f"❌ ONNX export failed: {e}")
        return None


def convert_onnx_to_tflite(onnx_path):
    """Convert ONNX to TensorFlow Lite"""
    print("\n" + "="*50)
    print("Step 3: Convert ONNX to TFLite")
    print("="*50)

    try:
        import onnx
        from onnx import tensorflow as onnx_tf
        import tensorflow as tf
    except ImportError:
        print("❌ Required packages not installed")
        print("Run: pip install onnx onnx-tf tensorflow")
        return None

    # Load ONNX model
    print("Loading ONNX model...")
    onnx_model = onnx.load(onnx_path)

    # Convert to TensorFlow
    print("Converting to TensorFlow format...")
    try:
        tf_rep = onnx_tf.backend.prepare(onnx_model)
    except Exception as e:
        print(f"⚠️ Direct conversion failed: {e}")
        print("Trying alternative method...")
        return None

    # Save TensorFlow model
    tf_model_dir = f"{OUTPUT_DIR}/tf_model"
    os.makedirs(tf_model_dir, exist_ok=True)

    # Create a wrapper to save properly
    import tempfile
    import shutil

    with tempfile.TemporaryDirectory() as tmpdir:
        # Export using onnx-tf
        onnx_tf.backend.export_graph(onnx_model, tmpdir)

        # List files
        for f in os.listdir(tmpdir):
            src = os.path.join(tmpdir, f)
            dst = os.path.join(tf_model_dir, f)
            if os.path.isdir(src):
                shutil.copytree(src, dst, dirs_exist_ok=True)
            else:
                shutil.copy2(src, dst)

    print(f"✅ TensorFlow model saved: {tf_model_dir}")

    # Convert to TFLite
    print("\nConverting to TensorFlow Lite...")

    # Convert using tf.lite
    converter = tf.lite.TFLiteConverter.from_saved_model(tf_model_dir)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float32]

    tflite_model = converter.convert()

    # Save TFLite model
    tflite_path = f"{OUTPUT_DIR}/cornai_model.tflite"
    with open(tflite_path, 'wb') as f:
        f.write(tflite_model)

    print(f"✅ TFLite model saved: {tflite_path}")
    print(f"   Size: {os.path.getsize(tflite_path) / (1024*1024):.2f} MB")

    return tflite_path


def verify_tflite_model(tflite_path):
    """Verify TFLite model works"""
    print("\n" + "="*50)
    print("Step 4: Verify TFLite model")
    print("="*50)

    try:
        import tensorflow as tf

        # Load model
        interpreter = tf.lite.Interpreter(model_path=tflite_path)
        interpreter.allocate_tensors()

        # Get input/output details
        input_details = interpreter.get_input_details()
        output_details = interpreter.get_output_details()

        print(f"✅ Model loaded successfully")
        print(f"   Input shape: {input_details[0]['shape']}")
        print(f"   Output shape: {output_details[0]['shape']}")
        print(f"   Input dtype: {input_details[0]['dtype']}")
        print(f"   Output dtype: {output_details[0]['dtype']}")

        # Run test inference
        import numpy as np

        # Create dummy input
        dummy_input = np.random.randn(1, INPUT_SIZE, INPUT_SIZE, 3).astype(np.float32)

        # Set input tensor
        interpreter.set_tensor(input_details[0]['index'], dummy_input)

        # Run inference
        interpreter.invoke()

        # Get output
        output = interpreter.get_tensor(output_details[0]['index'])
        print(f"   Test inference output shape: {output.shape}")
        print(f"   Test inference output sum: {output.sum():.4f}")

        # Get prediction
        predicted_class = np.argmax(output[0])
        confidence = np.max(output[0])

        print(f"   Predicted class index: {predicted_class}")
        print(f"   Confidence: {confidence:.4f}")

        return True

    except Exception as e:
        print(f"❌ Verification failed: {e}")
        return False


def create_inference_test_script(tflite_path):
    """Create a test script for Android"""
    print("\n" + "="*50)
    print("Step 5: Create Android inference helper")
    print("="*50)

    test_script = f'''package com.cornai.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

/**
 * CornAI Classifier
 * YOLOv8 Classification Model for Corn Disease Detection
 */
class CornAIModel(private val context: Context) {{
    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    companion object {{
        private const val MODEL_FILE = "cornai_model.tflite"
        private const val LABEL_FILE = "labels.txt"
        private const val INPUT_SIZE = {INPUT_SIZE}
        private const val NUM_CLASSES = {NUM_CLASSES}
    }}

    fun loadModel(): Boolean {{
        return try {{
            // Load TFLite model
            val modelBuffer = FileUtil.loadMappedFile(context, MODEL_FILE)
            val options = Interpreter.Options().apply {{
                numThreads = 4
            }}
            interpreter = Interpreter(modelBuffer, options)

            // Load labels
            labels = FileUtil.loadLabels(context, LABEL_FILE)

            true
        }} catch (e: Exception) {{
            e.printStackTrace()
            false
        }}
    }}

    fun classify(imageData: FloatArray): ClassificationResult {{
        val interpreter = this.interpreter ?: return ClassificationResult("Error", 0f, true)

        // Preprocess
        val inputBuffer = preprocessImage(imageData)

        // Run inference
        val output = Array(1) {{ FloatArray(NUM_CLASSES) }}
        interpreter.run(inputBuffer, output)

        // Postprocess
        return postprocessOutput(output[0])
    }}

    private fun preprocessImage(imageData: FloatArray): Array<Array<Array<FloatArray>>> {{
        // Convert to [1][224][224][3] format
        val input = Array(1) {{ Array(INPUT_SIZE) {{ Array(INPUT_SIZE) {{ FloatArray(3) }} }} }}

        var idx = 0
        for (y in 0 until INPUT_SIZE) {{
            for (x in 0 until INPUT_SIZE) {{
                input[0][y][x][0] = imageData[idx++] // R
                input[0][y][x][1] = imageData[idx++] // G
                input[0][y][x][2] = imageData[idx++] // B
            }}
        }}

        return input
    }}

    private fun postprocessOutput(output: FloatArray): ClassificationResult {{
        // Apply softmax
        val expOutputs = output.map {{ exp(it.toDouble()) }}.toDoubleArray()
        val sumExp = expOutputs.sum()
        val probabilities = expOutputs.map {{ (it / sumExp).toFloat() }}.toFloatArray()

        // Get top prediction
        val maxIdx = probabilities.indices.maxByOrNull {{ probabilities[it] }} ?: 0
        val confidence = probabilities[maxIdx]
        val className = labels.getOrNull(maxIdx) ?: "Unknown"

        // Determine if healthy
        val isHealthy = className.contains("Healthy")

        return ClassificationResult(className, confidence, isHealthy)
    }}

    fun close() {{
        interpreter?.close()
        interpreter = null
    }}
}}

data class ClassificationResult(
    val className: String,
    val confidence: Float,
    val isHealthy: Boolean
)
'''

    with open(f"{OUTPUT_DIR}/CornAIModel.kt", "w") as f:
        f.write(test_script)

    print(f"✅ Created CornAIModel.kt")
    print(f"   Place in: app/src/main/java/com/cornai/ml/")

    return True


def main():
    print("""
    ╔═══════════════════════════════════════════════════════════╗
    ║       YOLOv8 to TFLite Converter for CornAI             ║
    ║       Corn Disease Classification Model                  ║
    ╚═══════════════════════════════════════════════════════════╝
    """)

    # Create output directory
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Step 1: Create labels and class info
    create_class_labels_file()

    # Step 2: Convert PyTorch to ONNX
    onnx_path = convert_torch_to_onnx()

    if onnx_path is None:
        print("\n⚠️ ONNX conversion failed.")
        print("   Alternative: Use the ONNX model directly or convert manually")
        print("   Run: python -m onnx2tf input.onnx -o output/")
        return

    # Step 3: Convert ONNX to TFLite
    tflite_path = convert_onnx_to_tflite(onnx_path)

    if tflite_path is None:
        print("\n⚠️ TFLite conversion failed.")
        return

    # Step 4: Verify
    verify_tflite_model(tflite_path)

    # Step 5: Create Android helper
    create_inference_test_script(tflite_path)

    print("""
    ╔═══════════════════════════════════════════════════════════╗
    ║                    CONVERSION COMPLETE!                    ║
    ╚═══════════════════════════════════════════════════════════╝

    Output files in: {}
    ├── cornai_model.tflite    (Main model - copy to Android)
    ├── labels.txt             (Class labels)
    ├── class_info.json        (Disease info for app)
    └── CornAIModel.kt         (Android inference code)

    Next steps:
    1. Copy cornai_model.tflite to app/src/main/assets/
    2. Copy labels.txt to app/src/main/assets/
    3. Add CornAIModel.kt to app/src/main/java/com/cornai/ml/
    4. Add TensorFlow Lite dependency to build.gradle

    Dependencies to add:
    implementation 'org.tensorflow:tensorflow-lite:2.14.0'
    implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'

    """.format(OUTPUT_DIR))


if __name__ == "__main__":
    main()