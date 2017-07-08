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

@ColorInt
fun Context.getPrimaryColor():Int =
        getColorFromRes(
                if (usesDarkTheme) R.color.dark_theme_primary else R.color.light_theme_primary)

@ColorInt
fun Context.getPrimaryDarkColor():Int =
        getColorFromRes(
                if (usesDarkTheme) R.color.dark_theme_primary_dark else R.color.light_theme_primary_dark)

@ColorInt
fun Context.getAccentColor():Int =
        getColorFromRes(
                if (usesDarkTheme) R.color.dark_theme_accent else R.color.light_theme_accent)

@ColorInt
fun Context.getCardBackgroundColor():Int = extractColor(intArrayOf(R.attr.cardBackgroundColor))

@ColorInt
fun Context.getPrimaryTextColor():Int =
        if (usesDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

@ColorInt
fun Context.getPrimaryTextColorInverse():Int =
        if (usesLightTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

@ColorInt
fun Context.getSecondaryTextColor():Int =
        if (usesDarkTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getSecondaryTextColorInverse():Int =
        if (usesLightTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getDisabledTextColor():Int =
        if (usesDarkTheme) Color.parseColor("#80ffffff") else Color.parseColor("#61000000")

@ColorInt
fun Context.getHintTextColor():Int = getDisabledTextColor()

@ColorInt
fun Context.getDividerColor() =
        if (usesDarkTheme) Color.parseColor("#1fffffff") else Color.parseColor("#1f000000")

@ColorInt
fun Context.getActiveIconsColor():Int =
        if (usesDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getInactiveIconsColor():Int = getDisabledTextColor()

@ColorInt
fun Context.getRippleColor():Int =
        getColorFromRes(
                if (usesDarkTheme) R.color.ripple_material_light else R.color.ripple_material_dark)

@ColorInt
fun Context.getOverlayColor():Int =
        if (usesDarkTheme) Color.parseColor("#40ffffff") else Color.parseColor("#4d000000")

@ColorInt
fun Context.getPrimaryTextColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

@ColorInt
fun Context.getSecondaryTextColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getActiveIconsColorFor(@ColorInt color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getChipsColor():Int =
        if (usesDarkTheme) Color.parseColor("#000") else Color.parseColor("#e0e0e0")

@ColorInt
fun Context.getChipsIconsColor():Int = getActiveIconsColor()