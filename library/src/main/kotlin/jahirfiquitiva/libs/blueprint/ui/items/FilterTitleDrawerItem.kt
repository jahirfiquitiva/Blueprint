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
package jahirfiquitiva.libs.blueprint.ui.items

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.materialdrawer.model.BaseDrawerItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.kauextensions.extensions.bind

class FilterTitleDrawerItem(
        private var listener: ButtonListener? = null
                           ) :
        BaseDrawerItem<FilterTitleDrawerItem, FilterTitleDrawerItem.ViewHolder>() {
    
    fun withButtonListener(listener: ButtonListener): FilterTitleDrawerItem {
        this.listener = listener
        return this
    }
    
    override fun getLayoutRes(): Int = R.layout.item_drawer_filter_title
    
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
    
    override fun getType(): Int = R.id.filters_title
    
    override fun failedToRecycle(holder: ViewHolder?): Boolean = false
    
    override fun isEnabled(): Boolean = false
    
    override fun isSelected(): Boolean = false
    
    override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder?.itemView?.isClickable = false
        holder?.itemView?.isEnabled = false
        holder?.button?.setOnClickListener { listener?.onButtonPressed() }
        onPostBindView(this, holder?.itemView)
    }
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: AppCompatButton? by bind(R.id.clear_filters)
    }
    
    interface ButtonListener {
        fun onButtonPressed()
    }
}