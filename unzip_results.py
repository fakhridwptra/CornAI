import zipfile
import os

files_to_check = [
    "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/YOLOv11_Classification_Result (1)",
    "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/YOLOv8_Classification_Result (1)"
]

for file_path in files_to_check:
    print(f"Checking if zip: {file_path}")
    try:
        with zipfile.ZipFile(file_path, 'r') as z:
            print(f"Yes! {os.path.basename(file_path)} is a ZIP file.")
            print("Contents:")
            for name in z.namelist():
                if 'tflite' in name or 'txt' in name:
                    print(f"  - {name}")
                else:
                    print(f"  (other) {name}")
    except zipfile.BadZipFile:
        print(f"No! {os.path.basename(file_path)} is NOT a ZIP file.")
    except Exception as e:
        print(f"Error checking {file_path}: {e}")
