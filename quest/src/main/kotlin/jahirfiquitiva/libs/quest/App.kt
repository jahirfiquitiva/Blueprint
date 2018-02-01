/*
 * Copyright (c) 2018. Jahir Fiquitiva
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
 */
package jahirfiquitiva.libs.quest

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics
import android.widget.ImageView
import jahirfiquitiva.libs.quest.utils.formatCorrectly

/**
 * Created by Allan Wang on 2016-08-20.
 */
class App : Parcelable {
    
    var name: String = ""
        get() = field.formatCorrectly()
        private set
    var code: String = ""
        private set
    var pckg: String = ""
        private set
    
    @Transient private var hiResIcon: Drawable? = null
    @Transient private var icon: Drawable? = null
    
    private val appDefaultIcon: Drawable?
        get() = getAppIconFromRes(Resources.getSystem(), android.R.mipmap.sym_def_app_icon)
    
    constructor(name: String, code: String, pkg: String) {
        this.name = name.formatCorrectly()
        this.code = code
        this.pckg = pkg
    }
    
    @SuppressLint("NewApi")
    fun getHighResIcon(context: Context): Drawable? {
        if (hiResIcon != null) return hiResIcon
        try {
            hiResIcon = getIconFromInfo(context) ?:
                    context.packageManager.getApplicationIcon(pckg) ?: appDefaultIcon
        } catch (e: Exception) {
        }
        return null
    }
    
    private fun getIconFromInfo(context: Context): Drawable? {
        if (icon == null) {
            val ai = getAppInfo(context)
            if (ai != null) {
                icon = ai.loadIcon(context.packageManager)
                if (icon == null) {
                    icon = getAppIconFromRes(getResources(context, ai), ai.icon)
                }
            }
        }
        return icon
    }
    
    private fun getAppIconFromRes(resources: Resources?, iconId: Int): Drawable? {
        val d: Drawable?
        d = try {
            val iconDpi: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                DisplayMetrics.DENSITY_XXXHIGH
            } else {
                DisplayMetrics.DENSITY_XXHIGH
            }
            resources?.let { ResourcesCompat.getDrawableForDensity(it, iconId, iconDpi, null) }
        } catch (e: Exception) {
            null
        }
        return d
    }
    
    fun loadIcon(into: ImageView) {
        into.setImageDrawable(getHighResIcon(into.context))
    }
    
    private fun getAppInfo(context: Context): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(pckg, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    fun getActivityInfo(context: Context): ActivityInfo? {
        return try {
            context.packageManager.getActivityInfo(
                    ComponentName(
                            code.split(
                                    "/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0],
                            code.split(
                                    "/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]),
                    PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    private fun getResources(context: Context, ai: ApplicationInfo): Resources? {
        return try {
            context.packageManager.getResourcesForApplication(ai)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    override fun toString(): String = code
    
    override fun equals(other: Any?): Boolean = other is App && other.code == code
    
    protected constructor(parcel: Parcel) {
        name = parcel.readString()
        code = parcel.readString()
        pckg = parcel.readString()
    }
    
    override fun describeContents(): Int = 0
    
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(code)
        dest.writeString(pckg)
    }
    
    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App = App(parcel)
        override fun newArray(size: Int): Array<App?> = arrayOfNulls(size)
    }
}