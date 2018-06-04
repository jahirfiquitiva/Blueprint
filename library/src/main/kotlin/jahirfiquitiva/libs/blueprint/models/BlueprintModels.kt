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
package jahirfiquitiva.libs.blueprint.models

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_SECTION_ID

data class HomeItem(
    val title: String, val description: String, val url: String,
    val icon: Drawable?, val openIcon: Drawable?,
    val isAnApp: Boolean, val isInstalled: Boolean,
    val intent: Intent?
                   )

data class Icon(val name: String, @DrawableRes val icon: Int) : Comparable<Icon> {
    override fun compareTo(other: Icon): Int = this.name.compareTo(other.name)
}

data class IconsCategory(val title: String) {
    private val icons: ArrayList<Icon> = ArrayList()
    
    fun getIcons(): ArrayList<Icon> = ArrayList(icons.distinctBy { it.name }.sortedBy { it.name })
    
    fun setIcons(newIcons: ArrayList<Icon>) {
        icons.clear()
        icons.addAll(newIcons)
    }
    
    fun addIcon(icon: Icon) {
        icons.add(icon)
    }
    
    fun hasIcons(): Boolean = icons.isNotEmpty()
}

@Suppress("ArrayInDataClass")
internal data class Launcher(
    val key: String,
    val name: String,
    val packageNames: Array<String>,
    @ColorInt val color: Int,
    val isActuallySupported: Boolean = true
                            ) {
    fun hasPackage(packageName: String): Boolean {
        packageNames.forEach {
            if (it.equals(packageName, true)) return true
        }
        return false
    }
}

enum class NavigationItem(
    private val tag: String, val id: Int, @StringRes val title: Int,
    @DrawableRes val icon: Int
                         ) {
    HOME("Home", DEFAULT_HOME_SECTION_ID, R.string.section_home, R.drawable.ic_home),
    ICONS(
             "Previews", DEFAULT_ICONS_SECTION_ID, R.string.section_icons,
             R.drawable.ic_icons_preview),
    WALLPAPERS(
                  "Wallpapers", DEFAULT_WALLPAPERS_SECTION_ID, R.string.section_wallpapers,
                  R.drawable.ic_wallpapers),
    APPLY("Apply", DEFAULT_APPLY_SECTION_ID, R.string.section_apply, R.drawable.ic_apply),
    REQUESTS(
                "Requests", DEFAULT_REQUEST_SECTION_ID, R.string.section_icon_request,
                R.drawable.ic_request);
    
    override fun toString(): String = "NavigationItem[$tag - $id]"
}

data class Filter(val title: String, @ColorInt val color: Int, var selected: Boolean)