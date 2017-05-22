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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.models

import android.support.v4.app.Fragment
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.fragments.EmptyFragment
import jahirfiquitiva.libs.iconshowcase.fragments.HomeFragment

enum class NavigationItem(val stringId:String, val id:Int, val text:Int, val icon:Int) {
    HOME("Home", 0, R.string.section_home, R.drawable.ic_home),
    PREVIEWS("Previews", 1, R.string.section_icons, R.drawable.ic_previews),
    WALLPAPERS("Wallpapers", 2, R.string.section_wallpapers, R.drawable.ic_wallpapers),
    APPLY("Apply", 3, R.string.section_apply, R.drawable.ic_apply),
    REQUESTS("Requests", 4, R.string.section_icon_request, R.drawable.ic_request),
    ZOOPER("Zooper", 5, R.string.section_zooper, R.drawable.ic_zooper_kustom),
    KUSTOM("Kustom", 6, R.string.section_kustom, R.drawable.ic_zooper_kustom),
    FAQS("FAQs", 7, R.string.section_help, - 1),
    ABOUT("About", 8, R.string.section_about, - 1),
    SETTINGS("Settings", 9, R.string.title_settings, - 1);

    fun getFragment():Fragment {
        when (id) {
            0 -> return HomeFragment()
            else -> return EmptyFragment()
        }
    }

    override fun toString():String {
        return "NavigationItem: [StringId: $stringId, Id: $id]"
    }

    companion object {
        @JvmStatic fun getItemWithId(id:Int):NavigationItem? {
            values().forEach {
                if (it.id == id) return it
            }
            return null
        }

        @JvmStatic fun getItemWithId(id:String):NavigationItem? {
            values().forEach {
                if (it.stringId == id) return it
            }
            return null
        }
    }

}