# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }

# TensorFlow Lite
-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# Keep model classes
-keep class com.cornai.data.model.** { *; }
-keep class com.cornai.ml.** { *; }

# Keep TFLite model
-keep class com.cornai.ml.CornAIModel { *; }
-keep class com.cornai.ml.DiseaseData { *; }
-keep class com.cornai.ml.ClassificationResult { *; }