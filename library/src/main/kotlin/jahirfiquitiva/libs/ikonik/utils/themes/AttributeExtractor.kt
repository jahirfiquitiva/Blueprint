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
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.utils.themes

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.util.TypedValue
import jahirfiquitiva.libs.ikonik.R

object AttributeExtractor {
    private val PRIMARY_DARK = intArrayOf(R.attr.colorPrimaryDark)
    private val PRIMARY = intArrayOf(R.attr.colorPrimary)
    private val ACCENT = intArrayOf(R.attr.colorAccent)
    private val CARD_BG = intArrayOf(R.attr.cardBackgroundColor)

    /**
     * Extracts the colorPrimary color attribute of the passing Context's theme
     */
    @ColorInt
    fun getPrimaryColorFrom(context:Context):Int = extractIntAttribute(context, PRIMARY)

    /**
     * Extracts the colorPrimaryDark color attribute of the passing Context's theme
     */
    @ColorInt
    fun getPrimaryDarkColorFrom(context:Context):Int = extractIntAttribute(context, PRIMARY_DARK)

    /**
     * Extracts the colorAccent color attribute of the passing Context's theme
     */
    @ColorInt
    fun getAccentColorFrom(context:Context):Int = extractIntAttribute(context, ACCENT)

    /**
     * Extracts the cardBackgroundColor color attribute of the passing Context's theme
     */
    @ColorInt
    fun getCardBgColorFrom(context:Context):Int = extractIntAttribute(context, CARD_BG)

    /**
     * Extracts the drawable of the passing Context's theme
     */
    @ColorInt
    private fun extractIntAttribute(context:Context, attribute:IntArray):Int {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data, attribute)
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    fun extractDrawable(context:Context, @AttrRes drawableAttributeId:Int):Drawable {
        val typedValue = TypedValue()
        val a = context.obtainStyledAttributes(typedValue.data,
                                               intArrayOf(drawableAttributeId))
        val drawable = a.getDrawable(0)
        a.recycle()
        return drawable
    }
}