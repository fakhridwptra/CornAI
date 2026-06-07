import yaml
import os

yaml_path = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/YOLOv8_Classification_Result/full_logs/args.yaml"

if os.path.exists(yaml_path):
    print("=== YOLOv8 args.yaml ===")
    with open(yaml_path, 'r') as f:
        try:
            data = yaml.safe_load(f)
            # Print a subset of key arguments
            for k, v in data.items():
                if k in ['model', 'data', 'epochs', 'imgsz', 'names']:
                    print(f"{k}: {v}")
        except Exception as e:
            print(f"Error parsing: {e}")
else:
    print("args.yaml not found!")
