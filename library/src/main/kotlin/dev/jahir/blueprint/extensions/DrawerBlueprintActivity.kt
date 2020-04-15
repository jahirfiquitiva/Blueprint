package dev.jahir.blueprint.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.AttrRes
import dev.jahir.blueprint.R
import dev.jahir.blueprint.ui.activities.DrawerBlueprintActivity
import dev.jahir.frames.extensions.context.dimenPixelSize
import dev.jahir.frames.extensions.context.statusBarColor
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.views.findView
import dev.jahir.frames.extensions.views.setPaddingTop
import kotlin.math.min
import kotlin.math.roundToInt


internal fun DrawerBlueprintActivity.enableTranslucentStatusBar(enable: Boolean = true) {
    if (Build.VERSION.SDK_INT >= 21) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    val params: WindowManager.LayoutParams = window.attributes
    if (enable) {
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
    } else {
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    }
    window.attributes = params
    if (Build.VERSION.SDK_INT >= 21) statusBarColor = Color.TRANSPARENT
}

@SuppressLint("PrivateResource")
internal fun DrawerBlueprintActivity.setOptimalDrawerHeaderHeight(headerView: View) {
    val ratio = 9.0 / 16.0
    val defaultHeaderMinHeight = 180.dpToPx
    val statusBarHeight = 24.dpToPx
    var height = getOptimalDrawerWidth() * ratio

    if (Build.VERSION.SDK_INT >= 21) {
        headerView.setPaddingTop(headerView.paddingTop + statusBarHeight)
        if ((height - statusBarHeight) <= defaultHeaderMinHeight) {
            height = (defaultHeaderMinHeight + statusBarHeight).toDouble()
        }
    }

    val finalHeight = ((height - statusBarHeight)).roundToInt()
    headerView.post {
        val params = headerView.layoutParams
        params?.height = finalHeight
        headerView.layoutParams = params

        val headerBg: View? by headerView.findView(R.id.navigation_header)
        val bgParams = headerBg?.layoutParams
        bgParams?.height = finalHeight
        headerBg?.layoutParams = bgParams
    }
}

@SuppressLint("PrivateResource")
internal fun DrawerBlueprintActivity.getOptimalDrawerWidth(): Int {
    var actionBarHeight: Int = getThemeAttributeDimensionSize(R.attr.actionBarSize)
    if (actionBarHeight == 0) {
        actionBarHeight = dimenPixelSize(R.dimen.abc_action_bar_default_height_material)
    }
    val possibleMinDrawerWidth = resources.displayMetrics.widthPixels - actionBarHeight
    return min(possibleMinDrawerWidth, 320.dpToPx)
}

internal fun Context.getThemeAttributeDimensionSize(@AttrRes attr: Int): Int {
    var a: TypedArray? = null
    try {
        a = theme.obtainStyledAttributes(intArrayOf(attr))
        return a?.getDimensionPixelSize(0, 0) ?: 0
    } finally {
        a?.recycle()
    }
}