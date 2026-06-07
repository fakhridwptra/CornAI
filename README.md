# Corn AI - Android Application

Aplikasi mobile untuk deteksi penyakit tanaman jagung menggunakan AI (YOLOv11 + TFLite).

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM-ready
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Features

### Authentication
- [x] Splash Screen (animasi logo)
- [x] Onboarding (3 langkah swipe)
- [x] Login (Email + Password + Guest)
- [x] Register (Nama + Email + HP + Password)
- [x] Forgot Password

### Main Screens
- [x] Home (Weather Widget + Tips Harian)
- [x] Scanner (Kamera + Flash + Flip Kamera)
- [x] Result (Diagnosis + Rekomendasi)
- [x] History (Detail Bottom Sheet + Share)
- [x] Profile (Stats + Settings)
- [x] Help/FAQ
- [x] Privacy Policy

### Navigation
- [x] Bottom Navigation Bar (Home, Scan, History, Profile)

### Components
- [x] Gradient Button
- [x] Corn Icon (Custom Vector)
- [x] Badges (Status, Confidence)
- [x] Scanning Indicator (Animasi)
- [x] State Components (Empty, Error, Loading)
- [x] Shimmer Loading Effects
- [x] Empty States (History, Search, Notification, Profile)
- [x] CornSnackbar (Custom branded snackbar)
- [x] Pull-to-Refresh (History screen)
- [x] Search & Filter (History screen)

### Additional Screens
- [x] Profile Edit (Nama, Email, HP, Lokasi)
- [x] Notification Settings (Pengingat penyiraman, semprot, penyakit)
- [x] 3D Animations Demo (Showcase all 3D animations)

### New Screens
- [x] WelcomeScreen - Welcome page
- [x] FeatureShowcaseScreen - Feature showcase
- [x] SettingsScreen - Full settings
- [x] ResultDetailScreen - Detailed result
- [x] ProfileEnhancedScreen - Enhanced profile
- [x] HelpSupportScreen - Help & support
- [x] HistoryEnhancedScreen - Enhanced history

## User Flow

```
Splash → Onboarding → Login → Home
                              ↓
              Bottom Nav: Home | Scanner | History | Profile
                              ↓
                      Scanner → Result → Home/Scanner
```

## Color Palette (A2 - Premium)

| Color      | Hex       | Usage           |
|------------|-----------|----------------|
| Dark Green | `#1B5E20` | Primary        |
| Green      | `#2E7D32` | Primary Variant|
| Emerald    | `#00897B` | Secondary      |
| Gold       | `#FFB300` | Accent         |

## Disease Classes (10 Kelas)

### Daun (8):
1. Common Rust
2. Northern Leaf Blight
3. Gray Leaf Spot
4. Common Smut
5. Eyespot
6. Maize Streak
7. Asphalt Stain
8. Healthy Leaf

### Tongkol (2):
9. Healthy Cob
10. Cob Rot (Tidak Sehat)

## Setup

1. Buka Android Studio
2. Open Project → pilih folder `CornAI`
3. Tunggu Gradle sync selesai
4. Run di emulator atau device

## Project Structure

```
CornAI/
├── app/src/main/
│   ├── java/com/cornai/
│   │   ├── MainActivity.kt
│   │   └── ui/
│   │       ├── components/     # Reusable UI components
│   │       ├── navigation/     # Navigation setup
│   │       ├── screens/        # Screen composables
│   │       └── theme/          # Colors, Typography, Theme
│   └── res/
└── build.gradle.kts
```
