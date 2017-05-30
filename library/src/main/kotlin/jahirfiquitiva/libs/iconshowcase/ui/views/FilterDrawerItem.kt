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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.holders.FilterCheckBoxHolder
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils

class FilterDrawerItem:BaseDrawerItem<FilterDrawerItem, FilterDrawerItem.ViewHolder>() {

    var nameHolder:StringHolder? = null
    var listener:FilterCheckBoxHolder.StateChangeListener? = null
    var checkBoxHolder = FilterCheckBoxHolder()
    var showDivider = true
    var color = Color.parseColor("#b3e5fc")

    override fun withName(name:String?):FilterDrawerItem {
        this.nameHolder = StringHolder(name)
        return this
    }

    fun withListener(listener:FilterCheckBoxHolder.StateChangeListener):FilterDrawerItem {
        this.listener = listener
        return this
    }

    fun withDivider(show:Boolean):FilterDrawerItem {
        this.showDivider = show
        return this
    }

    fun withColor(@ColorInt color:Int):FilterDrawerItem {
        this.color = color
        return this
    }

    override fun getLayoutRes():Int = R.layout.item_drawer_filter

    override fun getViewHolder(v:View?):ViewHolder = ViewHolder(v)

    override fun getType():Int = R.id.filter

    override fun failedToRecycle(holder:ViewHolder?):Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindView(holder:ViewHolder?, payloads:MutableList<Any>?) {
        super.bindView(holder, payloads)
        nameHolder?.applyTo(holder?.title)
        holder?.title?.background = ColorDrawable(color)
        if (showDivider)
            holder?.divider?.background = ColorDrawable(
                    ColorUtils.getMaterialDividerColor(ThemeUtils.isDarkTheme()))
        else holder?.divider?.visibility = View.GONE
        checkBoxHolder = FilterCheckBoxHolder()
        checkBoxHolder.setup(holder?.checkBox!!, nameHolder?.text!!, listener)
        holder.itemView?.setOnClickListener {
            checkBoxHolder.apply(!checkBoxHolder.isChecked())
        }
        onPostBindView(this, holder.itemView)
    }

    class ViewHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val title = itemView?.findViewById(R.id.filter_name) as TextView
        val checkBox = itemView?.findViewById(R.id.filter_checkbox) as AppCompatCheckBox
        val divider = itemView?.findViewById(R.id.divider) as View
    }

}