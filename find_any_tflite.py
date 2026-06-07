import os

downloads_dir = "C:/Users/Fakhr/Downloads"
print(f"Searching for .tflite files in: {downloads_dir}")

tflite_files = []
for root, dirs, files in os.walk(downloads_dir):
    # Skip deep directories to speed it up
    if 'AppData' in root or '.android' in root or '.gradle' in root:
        continue
    for file in files:
        if file.endswith('.tflite'):
            path = os.path.join(root, file)
            print(f"Found: {path} ({os.path.getsize(path)} bytes)")
            tflite_files.append(path)

if not tflite_files:
    print("No .tflite files found in Downloads.")
