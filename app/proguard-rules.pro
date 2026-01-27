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

# Jetpack Compose - minimal rules, Compose handles obfuscation well
# Only add specific rules if runtime issues occur

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

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
