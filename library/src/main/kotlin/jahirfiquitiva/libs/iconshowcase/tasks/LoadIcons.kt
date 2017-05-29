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

package jahirfiquitiva.libs.iconshowcase.tasks

import android.content.Context
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.models.Icon
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import java.util.*
import kotlin.collections.ArrayList

class LoadIcons(context:Context?, listener:TaskListener<ArrayList<Icon>>? = null):
        BasicTaskLoader<ArrayList<Icon>>(context, listener) {

    override fun getTaskId():Int = 0

    override fun loadInBackground():ArrayList<Icon> {
        val icons:ArrayList<Icon> = ArrayList()
        ResourceUtils.getStringArray(context, R.array.icon_filters).forEach {
            var list:ArrayList<String> = ArrayList()
            list.plus(ResourceUtils.getStringArray(context,
                    context.resources.getIdentifier(it, "array", context.packageName)))
            list = sortIconsNames(list)
            list.forEach {
                icons.plus(Icon(it, IconUtils.getIconResourceWithName(context, it)))
            }
        }
        return icons
    }

    private fun sortIconsNames(icons:ArrayList<String>):ArrayList<String> {
        icons.sort()
        val noDuplicates:Set<String> = HashSet()
        noDuplicates.plus(icons)
        icons.clear()
        icons.plus(noDuplicates)
        icons.sort()
        return icons
    }

}