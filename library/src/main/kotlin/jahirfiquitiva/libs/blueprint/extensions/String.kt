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
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import jahirfiquitiva.libs.blueprint.utils.CAPS
import jahirfiquitiva.libs.blueprint.utils.CAPS_LOCK
import jahirfiquitiva.libs.blueprint.utils.SPACE

fun String.getBitmap(context:Context):Bitmap? =
        this.getBitmapDrawable(context)?.bitmap

fun String.getBitmapDrawable(context:Context):BitmapDrawable? {
    try {
        return ResourcesCompat.getDrawable(context.resources,
                                           this.getIconResource(context),
                                           null) as BitmapDrawable?
    } catch (e:Exception) {
        throw Resources.NotFoundException("Icon with name ${this} could not be found")
    }
}

fun String.getDrawable(context:Context):Drawable {
    try {
        return ContextCompat.getDrawable(context, this.getIconResource(context))
    } catch (e:Exception) {
        throw Resources.NotFoundException("Icon with name ${this} could not be found")
    }
}

fun String.getIconResource(context:Context):Int {
    val res = context.resources.getIdentifier(this, "drawable", context.packageName)
    return if (res != 0) res else 0
}

fun String.formatCorrectly() =
        replace("[^\\w\\s]+".toRegex(), " ").trim().replace(" +".toRegex(), " ").replace("\\p{Z}".toRegex(), "_")

/**
 * Kotlin port of the icon names formatting method made by Aidan Follestad (afollestad)
 */
fun String.blueprintFormat():String {
    val sb = StringBuilder()
    var underscoreMode = 0
    var foundFirstLetter = false
    var lastWasLetter = false
    var index = 0
    this.toCharArray().forEach {
        if (Character.isLetterOrDigit(it)) {
            if (underscoreMode == SPACE) {
                sb.append(" ")
                underscoreMode = CAPS
            }
            if (!foundFirstLetter && underscoreMode == CAPS) {
                sb.append(it)
            } else {
                sb.append(
                        if (index == 0 || underscoreMode > 1) Character.toUpperCase(it) else it)
            }
            if (underscoreMode < CAPS_LOCK) underscoreMode = 0
            foundFirstLetter = true
            lastWasLetter = true
        } else if (it == '_') {
            if (underscoreMode == CAPS_LOCK) {
                if (lastWasLetter) {
                    underscoreMode = SPACE
                } else {
                    sb.append(it)
                    underscoreMode = 0
                }
            } else {
                underscoreMode += 1
            }
            lastWasLetter = false
        }
        index += 1
    }
    return sb.toString()
}