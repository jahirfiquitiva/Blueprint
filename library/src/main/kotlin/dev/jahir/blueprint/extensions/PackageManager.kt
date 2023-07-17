package dev.jahir.blueprint.extensions

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build

fun PackageManager.getApplicationInfoCompat(pkg: String, flags: Int = 0): ApplicationInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getApplicationInfo(pkg, PackageManager.ApplicationInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getApplicationInfo(pkg, flags)
    }

fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int = 0): List<ResolveInfo> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") queryIntentActivities(intent, flags)
    }

fun PackageManager.resolveActivityCompat(intent: Intent, flags: Int = 0): ResolveInfo? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resolveActivity(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") resolveActivity(intent, flags)
    }
