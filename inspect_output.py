import tensorflow as tf
import numpy as np

model_path = "app/src/main/assets/cornai_model.tflite"
interpreter = tf.lite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Test with dummy inputs (all zeros, all 0.5, all ones)
for val in [0.0, 0.5, 1.0]:
    dummy_input = np.full((1, 224, 224, 3), val, dtype=np.float32)
    interpreter.set_tensor(input_details[0]['index'], dummy_input)
    interpreter.invoke()
    output_data = interpreter.get_tensor(output_details[0]['index'])
    print(f"Input value: {val}")
    print(f"  Raw output: {output_data[0]}")
    print(f"  Sum: {np.sum(output_data[0])}")
    print(f"  Min: {np.min(output_data[0])}, Max: {np.max(output_data[0])}")
