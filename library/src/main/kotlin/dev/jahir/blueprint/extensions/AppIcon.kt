package dev.jahir.blueprint.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.res.ResourcesCompat
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.hasContent

fun getDefaultAppIcon(): Drawable? {
    return Resources.getSystem().getAppDrawable(android.R.mipmap.sym_def_app_icon)
}

fun Context.getAppIcon(pkg: String, preferBanner: Boolean = false): Drawable? {
    if (!pkg.hasContent()) return null
    var icon: Drawable? = null
    try {
        // If we should prefer the app's banner, try to get it first so that if it fails we can fallback to the icon
        if (preferBanner) {
            icon = loadBanner(pkg)
            if (icon == null) icon = packageManager.getApplicationBanner(pkg)
        }
        if (icon == null) icon = loadIcon(pkg)
        if (icon == null) icon = packageManager.getApplicationIcon(pkg)
    } catch (e: Exception) {
        if (icon == null) icon = drawable(string(R.string.icons_placeholder))
        if (icon == null) icon = drawable(R.drawable.ic_na_launcher)
        if (icon == null) icon = getDefaultAppIcon()
    }
    return icon
}

private fun Context.loadIcon(pkg: String): Drawable? {
    return try {
        val ai = getAppInfo(pkg)
        if (ai != null) {
            var icon = ai.loadIcon(packageManager)
            if (icon == null) icon = getResources(ai)?.getAppDrawable(ai.icon)
            return icon
        } else null
    } catch (e: Exception) {
        null
    }
}

private fun Context.loadBanner(pkg: String): Drawable? {
    return try {
        val ai = getAppInfo(pkg)
        if (ai != null) {
            var icon = ai.loadBanner(packageManager)
            if (icon == null) icon = getResources(ai)?.getAppDrawable(ai.banner)
            return icon
        } else null
    } catch (e: Exception) {
        null
    }
}

private fun Resources.getAppDrawable(iconId: Int): Drawable? {
    return try {
        ResourcesCompat.getDrawableForDensity(
            this,
            iconId,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) DisplayMetrics.DENSITY_DEVICE_STABLE
            else DisplayMetrics.DENSITY_DEFAULT,
            null
        )
    } catch (e: Exception) {
        null
    }
}

private fun Context.getAppInfo(pkg: String): ApplicationInfo? =
    try {
        packageManager.getApplicationInfoCompat(pkg, 0)
    } catch (e: Exception) {
        null
    }

private fun Context.getResources(ai: ApplicationInfo): Resources? {
    return try {
        packageManager.getResourcesForApplication(ai)
    } catch (e: Exception) {
        null
    }
}
