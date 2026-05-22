plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

// NOTE: Uncomment the next line and add google-services.json to enable Firebase
// id("com.google.gms.google-services")

android {
    namespace = "com.cornai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cornai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Compose extras (versions managed by BOM)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")

    // ===== CAMERAX =====
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ===== TENSORFLOW LITE =====
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // ===== IMAGE LOADING =====
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ===== ACCOMPANIST (Permissions) =====
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ===== DATASTORE (Local Storage) =====
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ===== ROOM DATABASE =====
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

/*
 * ============================================================
 * FIREBASE SETUP (Optional - For Cloud Features)
 * ============================================================
 *
 * App works in DEMO MODE without Firebase.
 * To enable cloud features, follow these steps:
 *
 * 1. Create Firebase project at https://console.firebase.google.com/
 *
 * 2. Add Android app:
 *    - Package name: com.cornai
 *    - Download google-services.json
 *    - Place it in: app/google-services.json
 *
 * 3. Enable these services in Firebase Console:
 *    - Authentication (Email/Password)
 *    - Firestore Database
 *    - Storage
 *
 * 4. Uncomment the plugin line at top of this file:
 *    id("com.google.gms.google-services")
 *
 * 5. Also uncomment in build.gradle.kts (root):
 *    id("com.google.gms.google-services") version "4.4.0" apply false
 *
 * 6. Add these dependencies:
 *    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
 *    implementation("com.google.firebase:firebase-auth-ktx")
 *    implementation("com.google.firebase:firebase-firestore-ktx")
 *    implementation("com.google.firebase:firebase-storage-ktx")
 *
 * 7. Sync Gradle and run!
 *
 * ============================================================
 */