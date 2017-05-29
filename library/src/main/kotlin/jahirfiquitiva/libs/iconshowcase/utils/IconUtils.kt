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

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream

object IconUtils {
    fun getBitmapWithName(context:Context, name:String):Bitmap? = getBitmapDrawableWithName(context,
            name)?.bitmap

    fun getBitmapDrawableWithName(context:Context, name:String):BitmapDrawable? {
        try {
            return ResourcesCompat.getDrawable(context.resources,
                    getIconResourceWithName(context, name), null) as BitmapDrawable?
        } catch (e:Exception) {
            throw Resources.NotFoundException("Icon with name \'" + name + "\' could not " +
                                              "be found")
        }
    }

    fun getDrawableWithName(context:Context, name:String):Drawable {
        try {
            return ContextCompat.getDrawable(context, getIconResourceWithName(context, name))
        } catch (e:Exception) {
            throw Resources.NotFoundException("Icon with name \'" + name + "\' could not " +
                                              "be found")
        }
    }

    fun getIconResourceWithName(context:Context, name:String):Int {
        val res = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (res != 0) res else 0
    }

    fun getUriForIcon(context:Context, name:String, icon:Bitmap):Uri? {
        var uri:Uri? = null
        val iconFile = File(context.cacheDir, name + ".png")
        val fos:FileOutputStream
        try {
            fos = FileOutputStream(iconFile)
            icon.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            uri = getUriFromFile(context, iconFile)
            if (uri == null) uri = Uri.fromFile(iconFile)
        } catch (ignored:Exception) {
        }
        if (uri == null) {
            val resId = getIconResourceWithName(context, name)
            try {
                uri = getUriFromResource(context, resId)
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

    private fun getUriFromResource(context:Context, resID:Int):Uri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                      context.resources.getResourcePackageName(resID) + '/' +
                      context.resources.getResourceTypeName(resID) + '/' +
                      context.resources.getResourceEntryName(resID))

    private fun getUriFromFile(context:Context, file:File):Uri? {
        try {
            return FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
        } catch (e:Exception) {
            return null
        }
    }

}