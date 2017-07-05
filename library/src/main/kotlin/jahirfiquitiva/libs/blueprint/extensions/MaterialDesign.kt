/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.extensions

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import jahirfiquitiva.libs.blueprint.R

fun Context.getPrimaryColor(isDarkTheme:Boolean):Int =
        getColorFromRes(
                if (isDarkTheme) R.color.dark_theme_primary else R.color.light_theme_primary)

fun Context.getPrimaryDarkColor(isDarkTheme:Boolean):Int =
        getColorFromRes(
                if (isDarkTheme) R.color.dark_theme_primary_dark else R.color.light_theme_primary_dark)

fun Context.getAccentColor(isDarkTheme:Boolean):Int =
        getColorFromRes(
                if (isDarkTheme) R.color.dark_theme_accent else R.color.light_theme_accent)

fun Context.getCardBackgroundColor():Int = extractColor(intArrayOf(R.attr.cardBackgroundColor))

fun Context.getPrimaryTextColor(isDarkTheme:Boolean):Int =
        if (isDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

fun Context.getPrimaryTextColorInverse(usesLightTheme:Boolean):Int =
        if (usesLightTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

fun Context.getSecondaryTextColor(isDarkTheme:Boolean):Int =
        if (isDarkTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

fun Context.getSecondaryTextColorInverse(usesLightTheme:Boolean):Int =
        if (usesLightTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

fun Context.getDisabledTextColor(isDarkTheme:Boolean):Int =
        if (isDarkTheme) Color.parseColor("#80ffffff") else Color.parseColor("#61000000")

fun Context.getHintTextColor(isDarkTheme:Boolean):Int = getDisabledTextColor(isDarkTheme)

@ColorInt
fun Context.getDividerColor(isDarkTheme:Boolean) =
        if (isDarkTheme) Color.parseColor("#1fffffff") else Color.parseColor("#1f000000")

fun Context.getActiveIconsColor(isDarkTheme:Boolean):Int =
        if (isDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")

fun Context.getInactiveIconsColor(isDarkTheme:Boolean):Int = getDisabledTextColor(isDarkTheme)

fun Context.getRippleColor(isDarkTheme:Boolean):Int =
        getColorFromRes(
                if (isDarkTheme) R.color.ripple_material_light else R.color.ripple_material_dark)

fun Context.getOverlayColor(isDarkTheme:Boolean):Int =
        if (isDarkTheme) Color.parseColor("#40ffffff") else Color.parseColor("#4d000000")

fun Context.getPrimaryTextColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

fun Context.getSecondaryTextColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

fun Context.getActiveIconsColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")