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

package jahirfiquitiva.libs.blueprint.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import jahirfiquitiva.libs.blueprint.R
import java.util.Collections

object ColorUtils {

    @ColorInt
    fun blendColors(@ColorInt color1:Int,
                    @ColorInt color2:Int,
                    @FloatRange(from = 0.0, to = 1.0) ratio:Float):Int {
        val inverseRatio = 1f - ratio
        val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color:Int,
                    @FloatRange(from = 0.0, to = 1.0) factor:Float):Int {
        val a = Color.alpha(color) * factor
        val r = Color.red(color).toFloat()
        val g = Color.green(color).toFloat()
        val b = Color.blue(color).toFloat()
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    @ColorInt
    fun changeAlpha(@ColorInt color:Int,
                    @FloatRange(from = 0.0, to = 1.0) newAlpha:Float):Int {
        val a = 255 * newAlpha
        val r = Color.red(color).toFloat()
        val g = Color.green(color).toFloat()
        val b = Color.blue(color).toFloat()
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    @ColorInt
    fun shiftColor(@ColorInt color:Int, @FloatRange(from = 0.0, to = 2.0) by:Float):Int {
        if (by == 1f) return color
        val alpha = Color.alpha(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= by // value component
        return (alpha shl 24) + (0x00ffffff and Color.HSVToColor(hsv))
    }

    @ColorInt
    fun stripAlpha(@ColorInt color:Int):Int =
            Color.rgb(Color.red(color), Color.green(color), Color.blue(color))

    fun isDarkColor(bitmap:Bitmap):Boolean = !isLightColor(bitmap)

    fun isDarkColor(palette:Palette):Boolean = !isLightColor(palette)

    fun isDarkColor(@ColorInt color:Int):Boolean = !isLightColor(color)

    fun isLightColor(bitmap:Bitmap):Boolean {
        val palette = Palette.from(bitmap).generate()
        if (palette.swatches.size > 0) {
            return isLightColor(palette)
        }
        return isLightColor(palette)
    }

    private fun isLightColor(palette:Palette):Boolean = isLightColor(
            getPaletteSwatch(palette)!!.rgb)

    fun isLightColor(@ColorInt color:Int):Boolean = getColorDarkness(color) < 0.45

    private fun getColorDarkness(@ColorInt color:Int):Double {
        if (color == Color.BLACK)
            return 1.0
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return 0.0
        return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
                color)) / 255
    }

    fun getPaletteSwatch(bitmap:Bitmap):Palette.Swatch? = //Test areas of 10 and 50*50
            getPaletteSwatch(Palette.from(bitmap).resizeBitmapArea(50 * 50).generate())

    fun getPaletteSwatch(palette:Palette?):Palette.Swatch? {
        if (palette != null) {
            if (palette.vibrantSwatch != null) {
                return palette.vibrantSwatch
            } else if (palette.mutedSwatch != null) {
                return palette.mutedSwatch
            } else if (palette.darkVibrantSwatch != null) {
                return palette.darkVibrantSwatch
            } else if (palette.darkMutedSwatch != null) {
                return palette.darkMutedSwatch
            } else if (palette.lightVibrantSwatch != null) {
                return palette.lightVibrantSwatch
            } else if (palette.lightMutedSwatch != null) {
                return palette.lightMutedSwatch
            } else if (!palette.swatches.isEmpty()) {
                return getPaletteSwatch(palette.swatches)
            }
        }
        return null
    }

    private fun getPaletteSwatch(swatches:List<Palette.Swatch>?):Palette.Swatch? {
        if (swatches == null) return null
        return Collections.max<Palette.Swatch>(swatches) { opt1, opt2 ->
            val a = opt1?.population ?: 0
            val b = opt2?.population ?: 0
            a - b
        }
    }

    @ColorRes
    fun getAccentColor(darkTheme:Boolean):Int =
            if (darkTheme) R.color.dark_theme_accent else R.color.light_theme_accent

    @ColorInt
    fun getMaterialPrimaryTextColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#de000000")

    @ColorInt
    fun getMaterialSecondaryTextColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#b3ffffff") else Color.parseColor("#8a000000")

    @ColorInt
    fun getMaterialDisabledHintTextColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#80ffffff") else Color.parseColor("#61000000")

    @ColorInt
    fun getMaterialDividerColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#1fffffff") else Color.parseColor("#1f000000")

    @ColorInt
    fun getMaterialActiveIconsColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#ffffffff") else Color.parseColor("#8a000000")

    @ColorInt
    fun getMaterialInactiveIconsColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#80ffffff") else Color.parseColor("#61000000")

    @SuppressLint("PrivateResource")
    @ColorInt
    fun getDefaultRippleColor(context:Context, useDarkRipple:Boolean):Int =
            ContextCompat.getColor(context,
                                   if (useDarkRipple) R.color.ripple_material_light else R.color.ripple_material_dark)

    @ColorInt
    fun getOverlayColor(darkTheme:Boolean):Int =
            if (darkTheme) Color.parseColor("#40ffffff") else Color.parseColor("#4d000000")
}