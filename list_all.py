import os

search_dir = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001"

print(f"Searching in: {search_dir}")
for root, dirs, files in os.walk(search_dir):
    for file in files:
        path = os.path.join(root, file)
        ext = os.path.splitext(file)[1].lower()
        if ext in ['.tflite', '.txt', '.pt', '.h5', '.zip']:
            print(f"File: {path} ({os.path.getsize(path)} bytes)")
        elif '(' in file and ')' in file: # Check if it has no extension but is a zip/etc.
            print(f"Other File: {path} ({os.path.getsize(path)} bytes)")
