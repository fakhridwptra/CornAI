import zipfile
import json
import os

models = [
    "C:/Users/Fakhr/Downloads/model_daun.tflite",
    "C:/Users/Fakhr/Downloads/model_penyakit_jagung.tflite",
    "C:/Users/Fakhr/Downloads/model_tongkol.tflite"
]

for model_path in models:
    print(f"\n=========================================")
    print(f"MODEL: {model_path} ({os.path.getsize(model_path)} bytes)")
    try:
        with zipfile.ZipFile(model_path, 'r') as z:
            print("Zip contents:", z.namelist())
            for name in z.namelist():
                if name.endswith('.json') or name.endswith('.txt'):
                    content = z.read(name).decode('utf-8')
                    print(f"=== CONTENT OF {name} ===")
                    try:
                        parsed = json.loads(content)
                        # Print names or first 20 lines of JSON
                        if 'names' in parsed:
                            print("Classes:", parsed['names'])
                        else:
                            print(json.dumps(parsed, indent=2)[:1000])
                    except:
                        print(content[:500])
    except zipfile.BadZipFile:
        print("Model has no embedded metadata zip.")
    except Exception as e:
        print("Error:", e)
