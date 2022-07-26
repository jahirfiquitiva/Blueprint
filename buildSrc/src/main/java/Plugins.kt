@file:Suppress("unused")

object Plugins {
    // Android
    const val android = "com.android.tools.build:gradle:${Versions.gradle}"
    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    // OneSignal
    const val oneSignal =
        "gradle.plugin.com.onesignal:onesignal-gradle-plugin:${Versions.oneSignalPlugin}"
    // KSP
    const val ksp =
        "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${Versions.ksp}"
}
