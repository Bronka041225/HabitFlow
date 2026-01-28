# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\26211\AppData\Local\Android\Sdk/tools/proguard/proguard-android-optimize.txt
# while the flags in this file as well as the documentation
# can be found at http://proguard.sourceforge.net/index.html#manual/ref/usage.html

# Keep Hilt classes
-keep class com.example.habitflow.HabitFlowApplication { *; }
-keep class com.example.habitflow.di.** { *; }

# Keep Room entities and DAOs (sometimes needed if reflection is used, though KSP generates code)
-keep class com.example.habitflow.data.entity.** { *; }
-keep class com.example.habitflow.data.dao.** { *; }

# Keep ViewModel if needed (Hilt usually handles this)
-keep class com.example.habitflow.ui.viewmodel.** { *; }

# Retrofit/Gson rules (if used later)
# -keepattributes Signature
# -keepattributes *Annotation*
# -keep class com.google.gson.** { *; }
