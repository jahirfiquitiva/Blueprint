# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-keep class !android.support.v7.internal.view.menu**,** {*;}
-keep class android.support.v7.graphics.** {*;}

-keep public class * implements com.bumptech.glide.module.GlideModule

-keep class com.github.javiersantos.**
-dontwarn com.github.javiersantos.**
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn com.fasterxml.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn java.lang.invoke.**

-dontwarn
-ignorewarnings