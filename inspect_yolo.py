import sys
from ultralytics import YOLO

def inspect_model(model_path):
    print(f"\nInspecting model: {model_path}")
    try:
        model = YOLO(model_path)
        print("Model loaded successfully!")
        print("Task:", model.task)
        print("Classes (model.names):")
        for idx, name in model.names.items():
            print(f"  Index {idx}: {name}")
    except Exception as e:
        print(f"Error loading model: {e}")

if __name__ == "__main__":
    inspect_model(r"C:\CornAI\DATA SEMUA\Hasil Model Terbaru (perbaikan)\YOLOv11_Classification_Result\yolov11_classification_best.pt")
    inspect_model(r"C:\CornAI\DATA SEMUA\Hasil Model Terbaru (perbaikan)\YOLOv8_Classification_Result\yolov8_classification_best.pt")
