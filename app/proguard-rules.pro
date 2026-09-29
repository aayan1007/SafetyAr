# SafetyAR Proguard Rules
-keep class com.google.ar.core.** { *; }
-dontwarn com.google.ar.core.**

-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

-keep class com.safetyar.app.data.local.entity.** { *; }
-keep class com.safetyar.app.data.remote.dto.** { *; }
-keep class com.safetyar.app.domain.model.** { *; }
