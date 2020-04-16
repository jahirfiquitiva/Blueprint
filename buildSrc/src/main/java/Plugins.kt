@file:Suppress("unused")

object Plugins {
    // Android
    const val android = "com.android.tools.build:gradle:${Versions.gradle}"
    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    // Jitpack Publisher
    const val jitpack = "com.github.dcendents:android-maven-gradle-plugin:${Versions.jitpack}"
    // OneSignal
    const val oneSignal =
        "gradle.plugin.com.onesignal:onesignal-gradle-plugin:${Versions.oneSignalPlugin}"
}