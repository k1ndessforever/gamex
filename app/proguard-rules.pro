# Keep all Compose classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Room entities and DAOs
-keep class com.gamex.data.room.** { *; }
-keep interface com.gamex.data.room.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}