/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcomp
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.quest

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics
import jahirfiquitiva.libs.blueprint.quest.utils.formatCorrectly

/**
 * Created by Allan Wang on 2016-08-20.
 */
class App : Parcelable {
    
    var name: String = ""
        get() = field.formatCorrectly()
        private set
    var pkg: String = ""
        private set
    var comp: String = ""
        private set
    
    private var hiResIcon: Drawable? = null
    var icon: Drawable? = null
        private set
    
    private val appDefaultIcon: Drawable?
        get() = getAppIconFromRes(Resources.getSystem(), android.R.mipmap.sym_def_app_icon)
    
    constructor(name: String, pkg: String, comp: String) {
        this.name = name.formatCorrectly()
        this.pkg = pkg
        this.comp = comp
    }
    
    @SuppressLint("NewApi")
    internal fun getHighResIcon(context: Context): Drawable? {
        if (hiResIcon == null) {
            try {
                hiResIcon = loadIcon(context) ?: context.packageManager.getApplicationIcon(pkg) ?:
                    icon ?: appDefaultIcon
            } catch (e: Exception) {
            }
        }
        return hiResIcon
    }
    
    internal fun loadIcon(context: Context): Drawable? {
        if (icon == null) {
            val ai = getAppInfo(context)
            if (ai != null) {
                icon = ai.loadIcon(context.packageManager)
                if (icon == null) {
                    icon = getAppIconFromRes(getResources(context, ai), ai.icon)
                }
            }
        }
        if (icon == null) icon = appDefaultIcon
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
    
    private fun getAppInfo(context: Context): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(pkg, 0)
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
    
    override fun toString(): String = comp
    
    override fun equals(other: Any?): Boolean = other is App && other.comp == comp
    
    override fun hashCode(): Int {
        var result = pkg.hashCode()
        result = 31 * result + comp.hashCode()
        return result
    }
    
    override fun describeContents(): Int = 0
    
    private constructor(parcel: Parcel) {
        name = parcel.readString() ?: ""
        comp = parcel.readString() ?: ""
        pkg = parcel.readString() ?: ""
    }
    
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(comp)
        dest.writeString(pkg)
    }
    
    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App = App(parcel)
        override fun newArray(size: Int): Array<App?> = arrayOfNulls(size)
    }
}