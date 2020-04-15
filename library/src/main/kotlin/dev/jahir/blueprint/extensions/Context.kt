package dev.jahir.blueprint.extensions

import android.content.Context
import androidx.annotation.DrawableRes
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.resources.lower
import java.util.concurrent.TimeUnit

@DrawableRes
fun Context.drawableRes(name: String): Int =
    try {
        resources.getIdentifier(name, "drawable", packageName)
    } catch (e: Exception) {
        0
    }

internal fun Context.getLocalizedName(packageName: String, defaultName: String): String {
    var appName: String? = null
    try {
        val appInfo = packageManager.getApplicationInfo(
            packageName, android.content.pm.PackageManager.GET_META_DATA
        )
        try {
            val res = packageManager.getResourcesForApplication(packageName)
            val altCntxt =
                createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
            val configuration = res.configuration
            configuration.setLocale(java.util.Locale("en-US"))
            appName =
                altCntxt.createConfigurationContext(configuration).getString(appInfo.labelRes)
        } catch (e: Exception) {
        }
        if (appName == null) appName = packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: Exception) {
    }
    return appName ?: defaultName
}

internal fun Context.millisToText(millis: Long): String =
    when {
        TimeUnit.MILLISECONDS.toSeconds(millis) < 60 ->
            (TimeUnit.MILLISECONDS.toSeconds(millis)
                .toString() + " " + getString(R.string.seconds).lower())
        TimeUnit.MILLISECONDS.toMinutes(millis) < 60 ->
            (TimeUnit.MILLISECONDS.toMinutes(millis)
                .toString() + " " + getString(R.string.minutes).lower())
        TimeUnit.MILLISECONDS.toHours(millis) < 24 ->
            (TimeUnit.MILLISECONDS.toHours(millis)
                .toString() + " " + getString(R.string.hours).lower())
        TimeUnit.MILLISECONDS.toDays(millis) < 7 ->
            (TimeUnit.MILLISECONDS.toDays(millis)
                .toString() + " " + getString(R.string.days)).lower()
        millis.toWeeks() < 4 ->
            (millis.toWeeks().toString() + " " + getString(R.string.weeks).lower())
        else -> (millis.toMonths().toString() + " " + getString(R.string.months).lower())
    }

private fun Long.toWeeks() = TimeUnit.MILLISECONDS.toDays(this) / 7
private fun Long.toMonths() = toWeeks() / 4