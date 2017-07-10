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

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

fun Drawable.tintWithColor(@ColorInt color:Int):Drawable? {
    val nDrawable = DrawableCompat.wrap(mutate())
    DrawableCompat.setTintMode(nDrawable, PorterDuff.Mode.SRC_IN)
    DrawableCompat.setTint(nDrawable, color)
    return nDrawable
}

fun Drawable.tintWithColor(sl:ColorStateList):Drawable? {
    val nDrawable = DrawableCompat.wrap(mutate())
    DrawableCompat.setTintList(nDrawable, sl)
    return nDrawable
}