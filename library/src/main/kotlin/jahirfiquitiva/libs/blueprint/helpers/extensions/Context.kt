/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.AttrRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.setPaddingTop
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.ctxt
import jahirfiquitiva.libs.kext.extensions.getStatusBarHeight
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Suppress("DEPRECATION", "UNCHECKED_CAST")
internal val Fragment.configs: BPKonfigs
    get() = (activity as? ThemedActivity<BPKonfigs>)?.configs
        ?: activity?.let { BPKonfigs(it) }
        ?: context?.let { BPKonfigs(it) }
        ?: BPKonfigs(ctxt)

fun Context.millisToText(millis: Long): String {
    when {
        TimeUnit.MILLISECONDS.toSeconds(millis) < 60 ->
            return (TimeUnit.MILLISECONDS.toSeconds(millis).toString() + " "
                + getString(R.string.seconds).toLowerCase())
        TimeUnit.MILLISECONDS.toMinutes(millis) < 60 ->
            return (TimeUnit.MILLISECONDS.toMinutes(millis).toString() + " "
                + getString(R.string.minutes).toLowerCase())
        TimeUnit.MILLISECONDS.toHours(millis) < 24 ->
            return (TimeUnit.MILLISECONDS.toHours(millis).toString() + " "
                + getString(R.string.hours).toLowerCase())
        TimeUnit.MILLISECONDS.toDays(millis) < 7 ->
            return (TimeUnit.MILLISECONDS.toDays(millis).toString() + " "
                + getString(R.string.days)).toLowerCase()
        millis.toWeeks() < 4 -> return (millis.toWeeks().toString() + " "
            + getString(R.string.weeks).toLowerCase())
        else -> return (millis.toMonths().toString() + " "
            + getString(R.string.months).toLowerCase())
    }
}

@SuppressLint("PrivateResource")
internal fun Context.setOptimalDrawerHeaderHeight(headerView: View) {
    val ratio = 9.0 / 16.0
    val defaultHeaderMinHeight = resources.getDimensionPixelSize(R.dimen.nav_header_height)
    val statusBarHeight = getStatusBarHeight(true)
    var height = getOptimalDrawerWidth() * ratio
    
    if (Build.VERSION.SDK_INT < 19) {
        val tempHeight = height - statusBarHeight
        if (tempHeight > (defaultHeaderMinHeight - 8.dpToPx)) {
            height = tempHeight
        }
    }
    
    if (Build.VERSION.SDK_INT >= 21) {
        headerView.setPaddingTop(headerView.paddingTop + statusBarHeight)
        if ((height - statusBarHeight) <= defaultHeaderMinHeight) {
            height = (defaultHeaderMinHeight + statusBarHeight).toDouble()
        }
    }
    
    val finalHeight =
        (if (Build.VERSION.SDK_INT >= 19) (height - statusBarHeight) else height).roundToInt()
    headerView.post {
        val params = headerView.layoutParams
        params?.height = finalHeight
        headerView.layoutParams = params
        
        val headerBg: View? by headerView.bind(R.id.nav_header)
        val bgParams = headerBg?.layoutParams
        bgParams?.height = finalHeight
        headerBg?.layoutParams = bgParams
    }
}

@SuppressLint("PrivateResource")
internal fun Context.getOptimalDrawerWidth(): Int {
    var actionBarHeight: Int = getThemeAttributeDimensionSize(R.attr.actionBarSize)
    if (actionBarHeight == 0) {
        actionBarHeight =
            resources.getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)
    }
    
    val possibleMinDrawerWidth = resources.displayMetrics.widthPixels - actionBarHeight
    val maxDrawerWidth = resources.getDimensionPixelSize(R.dimen.nav_drawer_width)
    return Math.min(possibleMinDrawerWidth, maxDrawerWidth)
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

private fun Long.toWeeks() = TimeUnit.MILLISECONDS.toDays(this) / 7
private fun Long.toMonths() = toWeeks() / 4

internal fun FloatingActionButton.showIf(show: Boolean) =
    if (show) show() else hide()