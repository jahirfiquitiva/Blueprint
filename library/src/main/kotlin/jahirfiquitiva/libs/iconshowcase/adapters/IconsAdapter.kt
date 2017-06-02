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

package jahirfiquitiva.libs.iconshowcase.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.holders.IconHolder
import jahirfiquitiva.libs.iconshowcase.models.Icon
import jahirfiquitiva.libs.iconshowcase.utils.inflate

class IconsAdapter(val listener:(Icon) -> Unit):RecyclerView.Adapter<IconHolder>() {

    val icons:ArrayList<Icon> = ArrayList()

    fun setItems(nIcons:ArrayList<Icon>) {
        icons.clear()
        icons.plus(nIcons)
        notifyDataSetChanged()
    }

    override fun getItemCount():Int = icons.size

    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):IconHolder {
        return IconHolder(parent?.inflate(R.layout.item_icon))
    }

    override fun onBindViewHolder(holder:IconHolder?, position:Int) {
        if (position < 0) return
        holder?.bind(icons[position], listener)
    }
}