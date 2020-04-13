package dev.jahir.blueprint.extensions

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.hasContent


@DrawableRes
fun Context.getAppIconResId(pkg: String): Int = getAppInfo(pkg)?.icon ?: 0

fun Context.getAppIcon(pkg: String): Drawable? {
    if (!pkg.hasContent()) return null
    return try {
        loadIcon(pkg)
            ?: drawable(getAppIconResId(pkg))
            ?: packageManager.getApplicationIcon(pkg)
            ?: drawable(string(R.string.icons_placeholder))
            ?: drawable(R.drawable.ic_na_launcher)
    } catch (e: Exception) {
        null
    }
}

private fun Context.loadIcon(pkg: String): Drawable? {
    try {
        val ai = getAppInfo(pkg)
        if (ai != null) {
            var icon = ai.loadIcon(packageManager)
            if (icon == null) icon = getResources(ai)?.getAppIcon(ai.icon)
            return icon
        }
    } catch (e: Exception) {
    }
    return null
}

private fun Resources.getAppIcon(iconId: Int): Drawable? {
    return try {
        ResourcesCompat.getDrawableForDensity(this, iconId, DisplayMetrics.DENSITY_XXXHIGH, null)
    } catch (e: Exception) {
        null
    }
}

private fun Context.getAppInfo(pkg: String): ApplicationInfo? {
    return try {
        packageManager.getApplicationInfo(pkg, 0)
    } catch (e: Exception) {
        null
    }
}

private fun Context.getResources(ai: ApplicationInfo): Resources? {
    return try {
        packageManager.getResourcesForApplication(ai)
    } catch (e: Exception) {
        null
    }
}