import zipfile
import json
import tensorflow as tf

models = [
    "C:/Users/Fakhr/Downloads/model_daun.tflite",
    "C:/Users/Fakhr/Downloads/model_penyakit_jagung.tflite",
    "C:/Users/Fakhr/Downloads/model_tongkol.tflite"
]

for model_path in models:
    print(f"\n=========================================")
    print(f"MODEL: {model_path}")
    try:
        interpreter = tf.lite.Interpreter(model_path=model_path)
        interpreter.allocate_tensors()
        
        input_details = interpreter.get_input_details()
        output_details = interpreter.get_output_details()
        print("Input Shape:", input_details[0]['shape'])
        print("Output Shape:", output_details[0]['shape'])
        
        # Check zip contents
        with zipfile.ZipFile(model_path, 'r') as z:
            print("Zip contents:", z.namelist())
            if 'metadata.json' in z.namelist():
                content = z.read('metadata.json').decode('utf-8')
                parsed = json.loads(content)
                print("Classes in metadata:")
                print(parsed.get('names', {}))
    except zipfile.BadZipFile:
        print("Model has no embedded metadata zip.")
    except Exception as e:
        print("Error:", e)
