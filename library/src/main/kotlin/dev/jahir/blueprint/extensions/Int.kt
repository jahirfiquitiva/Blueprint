package dev.jahir.blueprint.extensions

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import kotlin.math.min
import kotlin.math.roundToInt

@ColorInt
fun Int.withMaxAlpha(@IntRange(from = 0, to = 255) alpha: Int): Int =
    Color.argb(min(alpha, Color.alpha(this)), Color.red(this), Color.green(this), Color.blue(this))

@ColorInt
fun Int.withMaxAlpha(@FloatRange(from = 0.0, to = 1.0) alpha: Float): Int = Color.argb(
    min((alpha * 255).roundToInt(), Color.alpha(this)),
    Color.red(this), Color.green(this), Color.blue(this)
)