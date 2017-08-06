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
 */

package jahirfiquitiva.libs.blueprint.holders.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.holders.FilterCheckBoxHolder
import jahirfiquitiva.libs.kauextensions.extensions.dividerColor

class FilterDrawerItem:BaseDrawerItem<FilterDrawerItem, FilterDrawerItem.ViewHolder>() {
    private lateinit var nameHolder:StringHolder
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
    
    override fun getViewHolder(v:View):ViewHolder = ViewHolder(v)
    
    override fun getType():Int = R.id.filter
    
    override fun failedToRecycle(holder:ViewHolder?):Boolean = false
    
    override fun bindView(holder:ViewHolder?, payloads:MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder?.let {
            nameHolder.applyTo(it.title)
            it.title?.background = ColorDrawable(color)
            val context = it.itemView?.context
            if (showDivider) {
                it.divider?.visible()
                val dividerColor = context?.dividerColor
                dividerColor?.let { holder.divider?.background = ColorDrawable(it) }
            } else it.divider?.gone()
            checkBoxHolder = FilterCheckBoxHolder()
            it.checkBox?.let { checkBox ->
                checkBoxHolder.setup(checkBox, nameHolder.text.toString(), listener)
            }
            holder.itemView?.setOnClickListener {
                checkBoxHolder.apply(!checkBoxHolder.isChecked())
            }
            onPostBindView(this, holder.itemView)
        }
    }
    
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val title:TextView? = itemView.findViewById(R.id.filter_name)
        val checkBox:AppCompatCheckBox? = itemView.findViewById(R.id.filter_checkbox)
        val divider:View? = itemView.findViewById(R.id.divider)
    }
    
}