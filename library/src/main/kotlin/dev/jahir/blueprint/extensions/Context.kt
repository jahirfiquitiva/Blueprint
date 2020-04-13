package dev.jahir.blueprint.extensions

import android.content.Context
import androidx.annotation.DrawableRes

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