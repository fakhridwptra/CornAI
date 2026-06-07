import zipfile
import json

model_path = "app/src/main/assets/cornai_model.tflite"

with zipfile.ZipFile(model_path, 'r') as z:
    content = z.read('metadata.json').decode('utf-8')
    try:
        parsed = json.loads(content)
        print(json.dumps(parsed, indent=2))
    except Exception as e:
        print("Raw content:")
        print(content[:2000])
