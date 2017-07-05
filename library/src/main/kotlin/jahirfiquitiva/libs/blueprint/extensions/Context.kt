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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Looper
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import jahirfiquitiva.libs.blueprint.utils.Konfigurations
import jahirfiquitiva.libs.blueprint.utils.PREFERENCES_NAME

fun Context.getStringFromRes(@StringRes stringRes:Int, fallback:String) =
        if (stringRes > 0) getString(stringRes) else fallback

fun Context.getColorFromRes(@ColorRes colorRes:Int) = ContextCompat.getColor(this, colorRes)

fun Context.getInteger(@IntegerRes id:Int):Int = resources.getInteger(id)

fun Context.getDimension(@DimenRes id:Int):Float = resources.getDimension(id)

fun Context.getDimensionPixelSize(@DimenRes id:Int):Int = resources.getDimensionPixelSize(id)

fun Context.getDrawable(@DrawableRes id:Int, fallback:Drawable? = null):Drawable? =
        if (id > 0) ContextCompat.getDrawable(this, id) else fallback

fun Context.isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun Context.getSharedPrefs() = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

fun Context.hasReadStoragePermission() =
        ContextCompat.checkSelfPermission(this,
                                          Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

fun Context.hasWriteStoragePermission() =
        ContextCompat.checkSelfPermission(this,
                                          Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

val Context.konfigs:Konfigurations
    get() = Konfigurations.newInstance(this)