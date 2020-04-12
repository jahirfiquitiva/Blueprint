package dev.jahir.blueprint.extensions

import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

internal val bnwFilter: ColorFilter
    get() = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })