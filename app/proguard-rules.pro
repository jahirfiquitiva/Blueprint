# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class dev.jahir.frames.** { *; }
-keep class dev.jahir.kuper.** { *; }
-keep class dev.jahir.blueprint.** { *; }

-keep class androidx.core.app.CoreComponentFactory { *; }
-keep class com.google.**
-keep class autovalue.shaded.com.google.**
-keep class com.android.vending.billing.**
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn org.apache.**
-dontwarn com.google.**
-dontwarn autovalue.shaded.com.google.**
-dontwarn com.android.vending.billing.**

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keep public final class * extends android.view.AbsSavedState
-keepclassmembers public final class * extends android.view.AbsSavedState { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn
-ignorewarnings
