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

val Context.primaryColor:Int
    get() = extractColor(intArrayOf(R.attr.colorPrimary))


val Context.primaryDarkColor:Int
    get() = extractColor(intArrayOf(R.attr.colorPrimaryDark))


val Context.accentColor:Int
    get() = extractColor(intArrayOf(R.attr.colorAccent))


val Context.cardBackgroundColor:Int
    get() = extractColor(intArrayOf(R.attr.cardBackgroundColor))


val Context.primaryTextColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")


val Context.primaryTextColorInverse:Int
    get() = if (usesLightTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")


val Context.secondaryTextColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")


val Context.secondaryTextColorInverse:Int
    get() = if (usesLightTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")


val Context.disabledTextColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#80ffffff") else Color.parseColor("#61000000")


val Context.hintTextColor:Int
    get() = disabledTextColor


val Context.dividerColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#1fffffff") else Color.parseColor("#1f000000")


val Context.activeIconsColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")


val Context.inactiveIconsColor:Int
    get() = disabledTextColor


val Context.rippleColor:Int
    get() = getColorFromRes(
            if (usesDarkTheme) R.color.ripple_material_light else R.color.ripple_material_dark)


val Context.overlayColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#40ffffff") else Color.parseColor("#4d000000")

val Context.chipsColor:Int
    get() = if (usesDarkTheme) Color.parseColor("#000") else Color.parseColor("#e0e0e0")


val Context.chipsIconsColor:Int
    get() = activeIconsColor

@ColorInt
fun Context.getPrimaryTextColorFor(color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

@ColorInt
fun Context.getSecondaryTextColorFor(color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

@ColorInt
fun Context.getActiveIconsColorFor(color:Int):Int =
        if (color.isColorDark()) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")