import os
from ultralytics import YOLO

def convert():
    model_path = r"C:\CornAI\DATA SEMUA\Hasil Model Terbaru (perbaikan)\YOLOv11_Classification_Result\yolov11_classification_best.pt"
    print(f"Loading YOLOv11 model: {model_path}")
    model = YOLO(model_path)
    print("Exporting model to TFLite...")
    # Export model. By default, classification models export to tflite float16 or float32.
    # YOLO exports will create a directory containing the tflite file.
    export_path = model.export(format="tflite")
    print(f"Export finished. Export result: {export_path}")

if __name__ == "__main__":
    convert()
