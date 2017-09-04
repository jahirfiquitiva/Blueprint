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
package jahirfiquitiva.libs.blueprint.ui.adapters

import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import com.pitchedapps.butler.iconrequest.App
import com.pitchedapps.butler.iconrequest.IconRequest
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.RequestViewHolder
import jahirfiquitiva.libs.frames.ui.adapters.BaseListAdapter

class RequestsAdapter(private val onItemsChanged:() -> Unit):
        BaseListAdapter<App, RequestViewHolder>() {
    
    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):RequestViewHolder? =
            parent?.inflate(R.layout.item_app_to_request)?.let { RequestViewHolder(it) }
    
    override fun doBind(holder:RequestViewHolder, position:Int, shouldAnimate:Boolean) {
        holder.setItem(list[position], { _, item ->
            val ir = IconRequest.get()
            if (ir != null && ir.apps != null) {
                ir.toggleAppSelected(item)
                notifyItemChanged(position)
                onItemsChanged()
            }
        })
    }
}