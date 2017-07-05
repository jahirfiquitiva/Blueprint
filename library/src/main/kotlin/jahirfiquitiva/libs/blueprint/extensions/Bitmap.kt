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
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.graphics.Palette
import java.io.File
import java.io.FileOutputStream

fun Bitmap.isColorDark() = !this.isColorLight()

fun Bitmap.isColorLight():Boolean = this.generatePalette().isColorLight()

fun Bitmap.generatePalette():Palette = Palette.from(this).resizeBitmapArea(50 * 50).generate()

val Bitmap.bestSwatch:Palette.Swatch?
    get() = this.generatePalette().bestSwatch

fun Bitmap.getUri(context:Context, name:String):Uri? {
    var uri:Uri? = null
    val iconFile = File(context.cacheDir, name + ".png")
    val fos:FileOutputStream
    try {
        fos = FileOutputStream(iconFile)
        compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        uri = iconFile.getUri(context)
        if (uri == null) uri = Uri.fromFile(iconFile)
    } catch (ignored:Exception) {
    }
    if (uri == null) {
        val resId = name.getIconResource(context)
        try {
            uri = resId.getUriFromResource(context)
        } catch (e:Exception) {
            try {
                uri = Uri.parse(
                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName
                        + "/" + resId.toString())
            } catch (ignored:Exception) {
            }
        }
    }
    return uri
}