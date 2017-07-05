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

package jahirfiquitiva.libs.blueprint.ui.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.activities.base.ThemedActivity
import jahirfiquitiva.libs.blueprint.extensions.getDividerColor
import jahirfiquitiva.libs.blueprint.extensions.makeGone
import jahirfiquitiva.libs.blueprint.extensions.makeVisible
import jahirfiquitiva.libs.blueprint.extensions.isDarkTheme
import jahirfiquitiva.libs.blueprint.holders.FilterCheckBoxHolder

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
        val context = holder?.itemView?.context
        if (showDivider && context is ThemedActivity) {
            holder.divider?.background = ColorDrawable(
                    context.getDividerColor(context.isDarkTheme()))
            holder.divider?.makeVisible()
        } else holder?.divider?.makeGone()
        checkBoxHolder = FilterCheckBoxHolder()
        checkBoxHolder.setup(holder?.checkBox!!, nameHolder?.text.toString(), listener)
        holder.itemView?.setOnClickListener {
            checkBoxHolder.apply(!checkBoxHolder.isChecked())
        }
        onPostBindView(this, holder.itemView)
    }

    class ViewHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val title:TextView? = itemView?.findViewById(R.id.filter_name)
        val checkBox:AppCompatCheckBox? = itemView?.findViewById(R.id.filter_checkbox)
        val divider:View? = itemView?.findViewById(R.id.divider)
    }

}