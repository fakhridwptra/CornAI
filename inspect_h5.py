import tensorflow as tf

h5_path = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/model_mobilenetv2.h5"

print("Loading model...")
try:
    # Disable oneDNN opts just in case
    import os
    os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
    
    model = tf.keras.models.load_model(h5_path)
    print("Model loaded successfully!")
    print("=== MODEL SUMMARY ===")
    model.summary()
    
    print("\nInput shape:", model.input_shape)
    print("Output shape:", model.output_shape)
    
    # Try converting to TFLite
    print("\nConverting to TFLite...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    
    tflite_out_path = "C:/Users/Fakhr/Downloads/model_mobilenetv2.tflite"
    with open(tflite_out_path, 'wb') as f:
        f.write(tflite_model)
    print(f"Success! Converted model saved to: {tflite_out_path} ({len(tflite_model)} bytes)")
    
except Exception as e:
    print(f"Error: {e}")
