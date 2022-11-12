package dev.jahir.blueprint.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.frames.extensions.resources.hasContent
import sarsamurmu.adaptiveicon.AdaptiveIcon

private fun getPathOption(context: Context? = null): Int {
    context ?: return -1
    return BlueprintPreferences(context).iconShape - 1
}

private fun getSystemAdaptiveIconsPath(): String =
    try {
        val resId = Resources.getSystem().getIdentifier("config_icon_mask", "string", "android")
        Resources.getSystem().getString(resId)
            .split(".").joinToString(",")
            .replace("M", " M ", true)
            .replace("Z", " Z ", true)
            .replace("L", " L ", true)
            .replace("H", " H ", true)
            .replace("V", " V ", true)
            .replace("C", " C ", true)
            .replace("S", " S ", true)
            .replace("Q", " Q ", true)
            .replace("T", " T ", true)
            .replace("A", " A ", true)
            .trim()
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }

private fun AdaptiveIcon.setRightPath(context: Context? = null) {
    val pathOption = getPathOption(context)
    val systemPath = getSystemAdaptiveIconsPath()
    if (pathOption < 0) {
        if (systemPath.hasContent()) {
            setScale(1.3)
            setPath(systemPath)
        }
    } else setPath(pathOption)
}

internal fun Drawable.asAdaptive(context: Context? = null): Pair<Drawable?, Boolean> {
    context ?: return Pair(this, false)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (this as? AdaptiveIconDrawable)?.let { ad ->
            return@let try {
                val bitmap = AdaptiveIcon().apply {
                    setForeground(ad.foreground)
                    setBackground(ad.background)
                    setRightPath(context)
                }.render()
                Pair(BitmapDrawable(context.resources, bitmap), true)
            } catch (e: Exception) {
                Pair(this, true)
            }
        } ?: Pair(this, false)
    } else Pair(this, false)
}