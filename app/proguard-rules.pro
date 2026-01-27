# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Room database entities and their constructors
-keep class com.example.habitflow.data.entity.** { *; }

# Keep Room DAOs
-keep interface com.example.habitflow.data.dao.** { *; }

# Keep Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class * { *; }

# Room runtime
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** getDatabase(android.content.Context);
}

# Keep Kotlin metadata for data classes used by Room
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.ColumnInfo <fields>;
}

# Jetpack Compose
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep all Composable functions
-keep @androidx.compose.runtime.Composable class ** { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Kotlin general
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.reflect.jvm.internal.** { *; }

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items)
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Vico Charts (if needed)
-keep class com.patrykandpatrick.vico.** { *; }

# AndroidX and Material Components
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**
