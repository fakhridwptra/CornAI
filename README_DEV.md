# CornAI - Deteksi Penyakit Jagung

Aplikasi Android untuk deteksi penyakit tanaman jagung menggunakan AI (YOLOv8 + TFLite).

## Fitur

- **Deteksi Penyakit** - Identifikasi 10 kelas penyakit jagung
- **Offline Mode** - Berjalan tanpa koneksi internet
- **Guest Mode** - Bisa langsung pakai tanpa login
- **Cloud Sync** - Login untuk backup data ke Firebase
- **Riwayat Scan** - Simpan dan lihat hasil scan sebelumnya
- **Rekomendasi** - Saran penanganan untuk setiap penyakit

## 10 Kelas Deteksi

| # | Kelas | Display Name | Status |
|---|-------|-------------|--------|
| 1 | Healthy_Daun | Healthy Leaf | ✅ Sehat |
| 2 | Healthy_Tongkol | Healthy Cob | ✅ Sehat |
| 3 | Common_Rust | Common Rust | 🌿 Penyakit |
| 4 | Gray_Leaf_Spot | Gray Leaf Spot | 🌿 Penyakit |
| 5 | Blight | Northern Leaf Blight | 🌿 Penyakit |
| 6 | Bipolaris | Bipolaris | 🌿 Penyakit |
| 7 | Stenocarpella | Stenocarpella | 🌿 Penyakit |
| 8 | Bacterial_Leaf_Streak | Bacterial Leaf Streak | 🌿 Penyakit |
| 9 | Asphalt_stain | Asphalt Stain | 🌿 Penyakit |
| 10 | Unhealthy_Tongkol | Unhealthy Cob | ❌ Penyakit |

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **AI Model**: YOLOv8 (TFLite)
- **Backend**: Firebase (Auth, Firestore, Storage)
- **Architecture**: MVVM + Repository Pattern
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Setup

### 1. Konfigurasi Firebase

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Buat project baru atau gunakan project existing
3. Tambahkan Android App dengan package `com.cornai`
4. Download `google-services.json`
5. Letakkan file di folder: `app/google-services.json`

### 2. Enable Firebase Services

Di Firebase Console, enable:
- **Authentication** → Email/Password
- **Firestore Database** → Create database
- **Storage** → Default bucket

### 3. Convert Model (Optional)

Model sudah ada di folder `D:\USER\project semester 6\Hasil Model Terbaru\YOLOv8_Classification_Result\yolov8_classification_best.pt`

Untuk convert ke TFLite, jalankan script:
```bash
cd app/src/main/assets
python convert_yolov8_to_tflite.py
```

Copy hasil `cornai_model.tflite` dan `labels.txt` ke folder `app/src/main/assets/`

### 4. Build & Run

```bash
cd CornAI
./gradlew assembleDebug
```

Atau buka di Android Studio dan Run.

## Struktur Project

```
CornAI/
├── app/src/main/
│   ├── java/com/cornai/
│   │   ├── data/
│   │   │   ├── local/          # Local storage (Room, DataStore)
│   │   │   ├── remote/          # Firebase services
│   │   │   ├── repository/     # Repository pattern
│   │   │   └── model/           # Data classes
│   │   ├── ml/                 # AI model (CornAIModel, DiseaseData)
│   │   ├── ui/
│   │   │   ├── screens/         # All screens
│   │   │   ├── components/       # Reusable components
│   │   │   ├── navigation/       # NavHost & routes
│   │   │   ├── theme/            # Colors, typography
│   │   │   └── viewmodel/       # ViewModels
│   │   └── MainActivity.kt
│   └── res/
├── build.gradle.kts
└── settings.gradle.kts
```

## Navigasi

```
Splash → Onboarding → Login/Register → Home
                              ↓
              Bottom Nav: Home | Scanner | History | Profile
                              ↓
                      Scanner → Result → Home/Scanner
```

## API Reference (Firebase)

### Authentication
- `signInWithEmail(email, password)` - Login
- `signUpWithEmail(name, email, password)` - Register
- `signInAsGuest()` - Masuk sebagai tamu
- `sendPasswordReset(email)` - Reset password

### Firestore Collections
- `users` - User profiles
- `scan_history` - Scan results

## Model Info

- **Model**: YOLOv8 Classification
- **Akurasi**: 96.29%
- **Ukuran**: 2.9 MB
- **Input Size**: 224x224
- **Format**: TFLite (.tflite)

## Troubleshooting

### Gradle Sync Failed
```
File → Invalidate Caches → Invalidate and Restart
```

### Firebase Connection Error
1. Pastikan `google-services.json` ada di folder `app`
2. Pastikan package name sama persis dengan Firebase
3. Cek apakah services sudah di-enable

### Model Not Found
Copy `cornai_model.tflite` ke `app/src/main/assets/`
Copy `labels.txt` ke `app/src/main/assets/`

## TODO

- [ ] Integrasi model TFLite (convert dari .pt)
- [ ] Test auth flow
- [ ] Test cloud sync
- [ ] Build release APK
- [ ] Deploy to Play Store

## License

MIT License