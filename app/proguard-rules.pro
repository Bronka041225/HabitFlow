# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\26211\AppData\Local\Android\Sdk/tools/proguard/proguard-android-optimize.txt
# while the flags in this file as well as the documentation
# can be found at http://proguard.sourceforge.net/index.html#manual/ref/usage.html

# Keep attributes for debugging
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep Hilt classes
-keep class com.example.habitflow.HabitFlowApplication { *; }
-keep class com.example.habitflow.di.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities and DAOs
-keep class com.example.habitflow.data.entity.** { *; }
-keep class com.example.habitflow.data.dao.** { *; }
-keep class com.example.habitflow.data.repository.** { *; }
-keep class com.example.habitflow.data.AppDatabase { *; }

# Keep ViewModel
-keep class com.example.habitflow.ui.viewmodel.** { *; }
-keep class androidx.lifecycle.** { *; }

# Gson rules - CRITICAL for data export
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes for Gson serialization
-keep class com.example.habitflow.ui.viewmodel.HabitExportData { *; }
-keepclassmembers class com.example.habitflow.data.entity.** {
    <fields>;
    <init>(...);
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-dontwarn androidx.compose.**

# Vico Chart Library
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Prevent stripping of generic signatures
-keepattributes Signature

# For native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
