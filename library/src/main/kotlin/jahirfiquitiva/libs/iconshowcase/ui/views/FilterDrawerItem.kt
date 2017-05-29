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

package jahirfiquitiva.libs.iconshowcase.ui.views

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.holders.FilterCheckBoxHolder

class FilterDrawerItem:BaseDrawerItem<FilterDrawerItem, FilterDrawerItem.ViewHolder>() {

    var nameHolder:StringHolder? = null
    val checkBoxHolder = FilterCheckBoxHolder()

    override fun getViewHolder(v:View?):ViewHolder {
        return ViewHolder(v)
    }

    override fun getLayoutRes():Int {
        return R.layout.item_drawer_filter
    }

    override fun getType():Int {
        return R.id.filter
    }

    override fun failedToRecycle(holder:ViewHolder?):Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindView(holder:ViewHolder?, payloads:MutableList<Any>?) {
        super.bindView(holder, payloads)
        nameHolder?.applyTo(holder?.title)
        holder?.itemView?.setOnClickListener {
            checkBoxHolder.setCheckedAndApplyTo(!checkBoxHolder.checked, holder.checkBox)
        }
        onPostBindView(this, holder?.itemView)
    }

    override fun withName(name:String?):FilterDrawerItem {
        this.nameHolder = StringHolder(name)
        return this
    }

    class ViewHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val title = itemView?.findViewById(R.id.filter_name) as TextView
        val checkBox = itemView?.findViewById(R.id.filter_checkbox) as AppCompatCheckBox
    }
}