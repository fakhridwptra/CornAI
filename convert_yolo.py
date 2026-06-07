from ultralytics import YOLO
import os

model8_path = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/YOLOv8_Classification_Result/yolov8_classification_best.pt"
model11_path = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/YOLOv11_Classification_Result/yolov11_classification_best.pt"

print("Converting YOLOv8 model to TFLite...")
try:
    model8 = YOLO(model8_path)
    # Export to tflite
    out8 = model8.export(format="tflite", imgsz=224)
    print(f"YOLOv8 conversion success! Output path: {out8}")
except Exception as e:
    print(f"Error converting YOLOv8: {e}")

print("\nConverting YOLOv11 model to TFLite...")
try:
    model11 = YOLO(model11_path)
    # Export to tflite
    out11 = model11.export(format="tflite", imgsz=224)
    print(f"YOLOv11 conversion success! Output path: {out11}")
except Exception as e:
    print(f"Error converting YOLOv11: {e}")
