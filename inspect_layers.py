import tensorflow as tf

h5_path = "C:/Users/Fakhr/Downloads/Hasil Model Terbaru (perbaikan)-20260602T123451Z-3-001/Hasil Model Terbaru (perbaikan)/model_mobilenetv2.h5"

model = tf.keras.models.load_model(h5_path)

print("=== LAYERS ===")
for i, layer in enumerate(model.layers[:10]):
    print(f"Layer {i}: {layer.name} ({layer.__class__.__name__})")
    # If it's a Rescaling or Normalization layer, print its configuration
    if hasattr(layer, 'scale') or hasattr(layer, 'mean'):
        print(f"  Scale: {getattr(layer, 'scale', None)}")
        print(f"  Offset: {getattr(layer, 'offset', None)}")
        print(f"  Mean: {getattr(layer, 'mean', None)}")
        print(f"  Variance: {getattr(layer, 'variance', None)}")
