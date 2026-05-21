# Langkah Setup CornAI

## 📋 Langkah 1: Setup Firebase

### 1.1 Buka Firebase Console
Buka: https://console.firebase.google.com/

### 1.2 Buat Project Baru
1. Klik **"Add project"**
2. Nama project: `CornAI` (atau sesuai keinginan)
3. Matikan **"Enable Google Analytics"** (optional, untuk simplifikasi)
4. Klik **"Create project"**
5. Tunggu sampai selesai

### 1.3 Register Android App
1. Di Dashboard, klik ikon **Android** (atau **Add app** → **Android**)
2. Masukkan:
   - **Android package name**: `com.cornai`
   - **App nickname**: `Corn AI`
   - **Debug signing certificate**: Optional (lewati aja dulu)
3. Klik **"Register app"**

### 1.4 Download google-services.json
1. Klik **"Download google-services.json"**
2. Copy file tersebut ke folder: `CornAI/app/google-services.json`
3. Klik **"Next"**

### 1.5 Add Firebase SDK
Di `build.gradle.kts` (root), sudah ada plugin-nya:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

Di `app/build.gradle.kts`, sudah ada dependency-nya:
```kotlin
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
```

### 1.6 Enable Firebase Services
Klik **"Go to console"** atau buka tab **Build → Authentication**

1. **Enable Email/Password Authentication**
   - Pilih **Authentication** → **Get started**
   - Klik tab **Sign-in method**
   - Enable **Email/Password**

2. **Create Firestore Database**
   - Pilih **Firestore Database** → **Create database**
   - Pilih **Start in test mode** (untuk development)
   - Pilih lokasi datacenter (terserah, misal Singapore)

3. **Enable Storage**
   - Pilih **Storage** → **Get started**
   - Pilih **Start in test mode**

### 1.7 Verifikasi
Di Android Studio, sync Gradle. Kalau ga ada error, Firebase udah connected.

---

## 📋 Langkah 2: Setup AI Model (TFLite)

### Option A: Pakai Google Colab (Recommended)

1. Buka https://colab.research.google.com/
2. Buat notebook baru
3. Paste kode berikut:

```python
# Install dependencies
!pip install ultralytics onnx-tf tensorflow

# Download model dari Google Drive (kalau ada link)
# Atau upload langsung ke Colab

# Load model
from ultralytics import YOLO
model = YOLO('/content/yolov8_classification_best.pt')

# Export ke TFLite
model.export(format='tflite', imgsz=224)

# Download file
from google.colab import files
files.download('/content/yolov8_classification_best.tflite')
```

4. Upload file `yolov8_classification_best.pt` ke Colab
5. Run cell
6. Download file `.tflite` hasil conversion

### Option B: Di Komputer Lokal

1. Pastikan Python 3.8+ terinstall
2. Install dependencies:
```bash
pip install ultralytics torch onnx onnx-tf tensorflow
```

3. Jalankan script:
```bash
cd CornAI/app/src/main/assets
python convert_model.py
```

### Option C: Pakai Model yang Sudah Ada

Kalau gagal convert, kita bisa pakai pendekatan lain:
- Pakai model Torch langsung (butuh PyTorch Android)
- Atau download model .tflite yang sudah jadi dari internet

---

## 📋 Langkah 3: Copy Model Files

1. Kalau sudah dapat `cornai_model.tflite`:
   - Copy ke: `CornAI/app/src/main/assets/cornai_model.tflite`

2. Buat file `labels.txt` di `CornAI/app/src/main/assets/`:
```
Healthy_Daun
Healthy_Tongkol
Common_Rust
Gray_Leaf_Spot
Blight
Bipolaris
Stenocarpella
Bacterial_Leaf_Streak
Asphalt_stain
Unhealthy_Tongkol
```

---

## 📋 Langkah 4: Build & Test

### 4.1 Sync Gradle
Di Android Studio:
- File → Sync Project with Gradle Files

### 4.2 Run App
1. Pilih device (emulator atau HP)
2. Klik Run (▶️) atau Shift+F10

### 4.3 Test Flow
1. **Splash** → otomatis ke Onboarding
2. **Onboarding** → swipe, click "Mulai"
3. **Login** → bisa:
   - Login dengan email/password (kalau sudah register)
   - Click "Masuk sebagai Tamu" (tanpa login)
4. **Home** → click "Mulai Scan Sekarang"
5. **Scanner** → camera terbuka, click "Mulai Pemindaian"
6. **Result** → muncul hasil (demo mode kalau model belum ada)

---

## 🆘 Troubleshooting

### Error: "Plugin [id: 'com.google.gms.google-services'] not found"
Pastikan `build.gradle.kts` (root) ada plugin-nya:
```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

### Error: "google-services.json is missing"
1. Buka Firebase Console
2. Project Settings → General
3. Download google-services.json lagi
4. Copy ke folder app/

### Error: "Firebase connection failed"
1. Pastikan internet stabil
2. Pastikan package name sama persis: `com.cornai`
3. Cek SHA-1 fingerprint (kalau ada)

### Error: "TFLite model not found"
App masih bisa jalan dalam **demo mode**:
- Scanner akan return hasil random
- Semua fitur lain tetap works

---

## ✅ Checklist Sebelum Run

- [ ] google-services.json ada di folder app/
- [ ] Firebase Authentication enabled
- [ ] Firestore Database created
- [ ] Gradle sync success (tanpa error merah)
- [ ] Device/emulator ready

---

## 📞 Perlu Bantuan?

Kalau ada error, screenshot error-nya dan chat saya.

Waktu paling cepet untuk solve masalah:
1. Copy error message
2. Paste ke Google
3. Klik first result StackOverflow