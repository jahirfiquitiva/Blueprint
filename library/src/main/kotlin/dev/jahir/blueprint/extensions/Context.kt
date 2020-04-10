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
