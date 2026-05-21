# Assets Folder - Model Files

## Required Files

### 1. cornai_model.tflite
Model AI YOLOv8 dalam format TensorFlow Lite.

**Cara dapat:**
1. Pakai Google Colab (recommended):
   - Upload `yolov8_classification_best.pt` ke Colab
   - Run: `model.export(format='tflite')`
   - Download hasil `.tflite`

2. Atau tanya saya untuk bantuan convert

### 2. labels.txt (SUDAH ADA)
Daftar nama kelas untuk inferensi.

## Status

| File | Status | Keterangan |
|------|--------|-------------|
| labels.txt | ✅ Ready | 10 class names |
| cornai_model.tflite | ❌ Needed | Belum ada |

##Kalau belum ada model

App masih bisa jalan dalam **Demo Mode**:
- Scanner akan return hasil random dari predefined list
- Semua fitur UI works
- Data tersimpan di local database

## Demo Classes

App sudah disetup untuk demo mode dengan hasil:
- Common Rust (89%)
- Northern Leaf Blight (94%)
- Healthy Leaf (97%)
- Gray Leaf Spot (91%)
- Healthy Cob (98%)

Jadi app bisa di-test tanpa model TFLite!