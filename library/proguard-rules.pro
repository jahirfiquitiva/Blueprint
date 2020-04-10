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

-dontwarn
-ignorewarnings