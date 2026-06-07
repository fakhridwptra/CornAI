import tensorflow as tf
import os

model_path = "app/src/main/assets/cornai_model.tflite"

# Check if there is metadata
try:
    from tflite_support import metadata as _metadata
    print("tflite_support is installed, checking metadata...")
    displayer = _metadata.MetadataDisplayer.with_model_file(model_path)
    metadata_json = displayer.get_metadata_json()
    print("=== MODEL METADATA ===")
    print(metadata_json)
    
    # Extract associated files
    export_dir = "C:/Users/Fakhr/Downloads/CornAI-main (1)/CornAI-main/extracted_metadata"
    os.makedirs(export_dir, exist_ok=True)
    displayer.unpack_associated_files(export_dir)
    print(f"Associated files unpacked to: {export_dir}")
    print("Files:", os.listdir(export_dir))
except ImportError:
    print("tflite_support is not installed. Let's try inspecting using basic zip tools or print general info.")
    # Many tflite models are also zipped or contain metadata at the end.
    with open(model_path, 'rb') as f:
        data = f.read()
    # Search for labels in the file data
    print("Searching for labels in binary data...")
    import re
    # Look for common label patterns
    labels = re.findall(rb'[a-zA-Z0-9_-]+', data)
    # Filter out very short or non-relevant strings
    potential_labels = [l.decode('utf-8') for l in labels if len(l) > 3 and rb'_' in l or l in [rb'Blight', rb'Bipolaris', rb'Stenocarpella', rb'Common_Rust']]
    print("Potential labels found in binary:", set(potential_labels))
