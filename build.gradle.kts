// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false

    // Firebase (Uncomment when google-services.json is added)
    // id("com.google.gms.google-services") version "4.4.0" apply false
}

/*
 * ============================================================
 * FIREBASE SETUP INSTRUCTIONS
 * ============================================================
 *
 * 1. Go to https://console.firebase.google.com/
 * 2. Create a new project
 * 3. Add Android app with package: com.cornai
 * 4. Download google-services.json
 * 5. Place it in: app/google-services.json
 *
 * Then UNCOMMENT these lines:
 *   id("com.google.gms.google-services") version "4.4.0" apply false
 *
 * And in app/build.gradle.kts, UNCOMMENT the plugin line.
 *
 * ============================================================
 */