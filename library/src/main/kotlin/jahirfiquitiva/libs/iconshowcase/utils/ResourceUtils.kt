/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat

object ResourceUtils {
    fun getString(context:Context, @StringRes res:Int):String = context.resources.getString(res)

    fun getString(context:Context, @StringRes res:Int,
                  vararg args:String):String = context.resources.getString(res, *args)

    fun getDrawable(context:Context, @DrawableRes res:Int):Drawable = ContextCompat.getDrawable(
            context, res)

    @ColorInt
    fun getColor(context:Context, @ColorRes res:Int):Int = ContextCompat.getColor(context, res)

    fun getBoolean(context:Context, @BoolRes res:Int):Boolean = getBoolean(context, res, false)

    fun getBoolean(context:Context, @BoolRes res:Int, defaultValue:Boolean):Boolean {
        try {
            return context.resources.getBoolean(res)
        } catch (e:Exception) {
            return defaultValue
        }
    }

    fun getInteger(context:Context, @IntegerRes res:Int):Int {
        try {
            return context.resources.getInteger(res)
        } catch (e:Exception) {
            return -1
        }
    }

    fun getStringArray(context:Context, @ArrayRes res:Int):Array<String> {
        try {
            return context.resources.getStringArray(res)
        } catch (e:Exception) {
            return arrayOf()
        }

    }
}