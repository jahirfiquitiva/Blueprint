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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange

@ColorInt
fun Int.blendWith(@ColorInt color:Int, @FloatRange(from = 0.0, to = 1.0) ratio:Float):Int {
    val inverseRatio = 1f - ratio
    val a = Color.alpha(this) * inverseRatio + Color.alpha(color) * ratio
    val r = Color.red(this) * inverseRatio + Color.red(color) * ratio
    val g = Color.green(this) * inverseRatio + Color.green(color) * ratio
    val b = Color.blue(this) * inverseRatio + Color.blue(color) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt
fun Int.adjustAlpha(@FloatRange(from = 0.0, to = 1.0) factor:Float):Int {
    val a = Color.alpha(this) * factor
    val r = Color.red(this).toFloat()
    val g = Color.green(this).toFloat()
    val b = Color.blue(this).toFloat()
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt
fun Int.changeAlpha(@FloatRange(from = 0.0, to = 1.0) newAlpha:Float):Int {
    val a = 255 * newAlpha
    val r = Color.red(this).toFloat()
    val g = Color.green(this).toFloat()
    val b = Color.blue(this).toFloat()
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

@ColorInt
fun Int.shiftColor(@FloatRange(from = 0.0, to = 2.0) by:Float):Int {
    if (by == 1f) return this
    val alpha = Color.alpha(this)
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] *= by // value component
    return (alpha shl 24) + (0x00ffffff and Color.HSVToColor(hsv))
}

@ColorInt
fun Int.stripAlpha():Int = Color.rgb(Color.red(this), Color.green(this), Color.blue(this))

fun Int.isColorDark():Boolean = !this.isColorLight()

fun Int.isColorLight():Boolean = this.getColorDarkness() < 0.45

fun Int.getColorDarkness():Double {
    if (this == Color.BLACK) return 1.0
    else if (this == Color.WHITE || this == Color.TRANSPARENT) return 0.0
    return 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 *
                Color.blue(this)) / 255
}

fun Int.getUriFromResource(context:Context):Uri =
        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                  context.resources.getResourcePackageName(this) + '/' +
                  context.resources.getResourceTypeName(this) + '/' +
                  context.resources.getResourceEntryName(this))