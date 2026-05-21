"""
YOLOv8 to TFLite Converter for CornAI
=====================================
Cara pakai:
1. Pastikan Python sudah terinstall dengan PyTorch
2. pip install ultralytics torch onnx onnx-tflite tensorflow
3. python convert_model.py

Output: cornai_model.tflite di folder yang sama
"""

import os
import torch
import shutil
from pathlib import Path

# ========================
# KONFIGURASI
# ========================
MODEL_PATH = "../Hasil Model Terbaru/YOLOv8_Classification_Result/yolov8_classification_best.pt"
OUTPUT_DIR = "./"
INPUT_SIZE = 224
NUM_CLASSES = 10

# Nama kelas sesuai training
CLASS_NAMES = [
    "Healthy_Daun",
    "Healthy_Tongkol",
    "Common_Rust",
    "Gray_Leaf_Spot",
    "Blight",
    "Bipolaris",
    "Stenocarpella",
    "Bacterial_Leaf_Streak",
    "Asphalt_stain",
    "Unhealthy_Tongkol"
]

def print_step(step, message):
    print(f"\n{'='*50}")
    print(f"STEP {step}: {message}")
    print(f"{'='*50}")

def main():
    print("""
    ╔══════════════════════════════════════════════════════════╗
    ║       YOLOv8 to TFLite Converter for CornAI               ║
    ║       Corn Disease Classification Model                  ║
    ╚══════════════════════════════════════════════════════════╝
    """)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # ============ STEP 1: Load Model ============
    print_step(1, "Loading PyTorch Model")

    try:
        from ultralytics import YOLO
    except ImportError:
        print("❌ ultralytics not installed")
        print("\nInstall dengan command:")
        print("  pip install ultralytics torch")
        return

    if not os.path.exists(MODEL_PATH):
        print(f"❌ Model not found at: {MODEL_PATH}")
        print("\nPastikan file model ada di lokasi tersebut")
        print("Atau ubah MODEL_PATH di script ini")
        return

    print(f"Loading: {MODEL_PATH}")
    model = YOLO(MODEL_PATH)
    print("✅ Model loaded successfully")

    # ============ STEP 2: Export to ONNX ============
    print_step(2, "Exporting to ONNX")

    onnx_path = os.path.join(OUTPUT_DIR, "cornai_model.onnx")

    try:
        # Export to ONNX
        model.export(format='onnx', imgsz=INPUT_SIZE)
        # Rename if needed
        if os.path.exists("yolov8_classification_best.onnx"):
            shutil.move("yolov8_classification_best.onnx", onnx_path)

        if os.path.exists(onnx_path):
            size_mb = os.path.getsize(onnx_path) / (1024*1024)
            print(f"✅ ONNX model saved: {onnx_path}")
            print(f"   Size: {size_mb:.2f} MB")
        else:
            print("⚠️ ONNX export did not create expected file")
            # Try alternative location
            for f in os.listdir('.'):
                if f.endswith('.onnx'):
                    shutil.copy(f, onnx_path)
                    break
    except Exception as e:
        print(f"❌ ONNX export failed: {e}")
        print("Trying alternative method...")

        # Alternative: Direct PyTorch to ONNX
        try:
            torch_model = model.model
            torch_model.eval()

            dummy_input = torch.randn(1, 3, INPUT_SIZE, INPUT_SIZE)
            torch.onnx.export(
                torch_model,
                dummy_input,
                onnx_path,
                export_params=True,
                opset_version=12,
                do_constant_folding=True,
                input_names=['input'],
                output_names=['output']
            )
            print(f"✅ ONNX model saved (alternative): {onnx_path}")
        except Exception as e2:
            print(f"❌ Alternative also failed: {e2}")
            return

    # ============ STEP 3: Convert to TFLite ============
    print_step(3, "Converting to TensorFlow Lite")

    try:
        import tensorflow as tf

        # Load ONNX model
        print("Loading ONNX model...")
        onnx_model = tf.saved_model.load(onnx_path) if os.path.exists(onnx_path.replace('.onnx', '')) else None

        # Alternative: Use onnx-tf
        try:
            import onnx
            from onnx_tf.backend import prepare

            onnx_model = onnx.load(onnx_path)
            tf_rep = prepare(onnx_model)

            tf_dir = os.path.join(OUTPUT_DIR, "tf_model")
            tf_rep.export_graph(tf_dir)

            # Convert to TFLite
            converter = tf.lite.TFLiteConverter.from_saved_model(tf_dir)
            converter.optimizations = [tf.lite.Optimize.DEFAULT]

            print("Converting to TFLite...")
            tflite_model = converter.convert()

            tflite_path = os.path.join(OUTPUT_DIR, "cornai_model.tflite")
            with open(tflite_path, 'wb') as f:
                f.write(tflite_model)

            size_mb = os.path.getsize(tflite_path) / (1024*1024)
            print(f"✅ TFLite model saved: {tflite_path}")
            print(f"   Size: {size_mb:.2f} MB")

        except Exception as e:
            print(f"⚠️ onnx-tf conversion failed: {e}")
            print("\nAlternatively, Anda bisa:")
            print("1. Jalankan di Google Colab: onnx-tf convert")
            print("2. Atau pakai script terpisah")
            print("\nSaya akan buat labels.txt saja dulu...")

    except ImportError:
        print("❌ tensorflow not installed")
        print("\nInstall dengan: pip install tensorflow")

    # ============ STEP 4: Create Labels ============
    print_step(4, "Creating Labels File")

    labels_path = os.path.join(OUTPUT_DIR, "labels.txt")
    with open(labels_path, 'w') as f:
        for name in CLASS_NAMES:
            f.write(f"{name}\n")

    print(f"✅ labels.txt created: {labels_path}")

    # ============ STEP 5: Create Class Info ============
    print_step(5, "Creating Class Info JSON")

    import json

    class_info = {
        "num_classes": NUM_CLASSES,
        "input_size": INPUT_SIZE,
        "classes": {}
    }

    for i, name in enumerate(CLASS_NAMES):
        class_info["classes"][name] = {
            "index": i,
            "display_name": name.replace("_", " ").title(),
            "is_healthy": "Healthy" in name
        }

    info_path = os.path.join(OUTPUT_DIR, "class_info.json")
    with open(info_path, 'w') as f:
        json.dump(class_info, f, indent=2)

    print(f"✅ class_info.json created: {info_path}")

    # ============ DONE ============
    print("""
    ╔══════════════════════════════════════════════════════════╗
    ║                    CONVERSION COMPLETE!                  ║
    ╚══════════════════════════════════════════════════════════╝

    Files created:
    ├── labels.txt         (Class labels)
    └── class_info.json    (Model info)

    ⚠️ Note: TFLite conversion may require additional setup.
    Jika convert gagal, coba di Google Colab:

    ```python
    !pip install ultralytics onnx onnx-tf tensorflow

    from ultralytics import YOLO
    model = YOLO('yolov8_classification_best.pt')
    model.export(format='tflite')

    # Download file .tflite dari Colab
    # Letakkan di app/src/main/assets/
    ```

    Setelah dapat file .tflite:
    1. Copy ke: app/src/main/assets/cornai_model.tflite
    2. Copy labels.txt ke: app/src/main/assets/labels.txt
    3. Build & Run!
    """)

if __name__ == "__main__":
    main()