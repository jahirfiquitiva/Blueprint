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
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.ui.views

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.materialdrawer.model.BaseDrawerItem

class FilterTitleDrawerItem:BaseDrawerItem<FilterTitleDrawerItem, FilterTitleDrawerItem.ViewHolder>() {

    var listener:ButtonListener? = null

    fun withButtonListener(listener:ButtonListener):FilterTitleDrawerItem {
        this.listener = listener
        return this
    }

    override fun getLayoutRes():Int = R.layout.item_drawer_filter_title

    override fun getViewHolder(v:View?):ViewHolder = ViewHolder(v)

    override fun getType():Int = R.id.filters_title

    override fun failedToRecycle(holder:ViewHolder?):Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEnabled():Boolean = false

    override fun isSelected():Boolean = false

    override fun bindView(holder:ViewHolder?, payloads:MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder?.itemView?.isClickable = false
        holder?.itemView?.isEnabled = false
        // TODO: Show button only when filters list is not empty
        holder?.button?.setOnClickListener { listener?.onButtonPressed() }
        onPostBindView(this, holder?.itemView)
    }

    class ViewHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val button:AppCompatButton? = itemView?.findViewById(R.id.clear_filters)
    }

    interface ButtonListener {
        fun onButtonPressed()
    }

}