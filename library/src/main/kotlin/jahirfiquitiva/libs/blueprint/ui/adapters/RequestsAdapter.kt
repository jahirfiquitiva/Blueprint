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
package jahirfiquitiva.libs.blueprint.ui.adapters

import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import com.bumptech.glide.RequestManager
import jahirfiquitiva.libs.archhelpers.ui.adapters.RecyclerViewListAdapter
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.RequestViewHolder

class RequestsAdapter(
    private val manager: RequestManager?,
    private val onItemsChanged: () -> Unit
                     ) :
    RecyclerViewListAdapter<App, RequestViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder =
        RequestViewHolder(parent.inflate(R.layout.item_app_to_request))
    
    override fun doBind(holder: RequestViewHolder, position: Int, shouldAnimate: Boolean) {
        holder.setItem(manager, list[position]) { checkbox, item ->
            IconRequest.get()?.let {
                if (it.toggleAppSelected(item)) {
                    checkbox.isChecked = !checkbox.isChecked
                    onItemsChanged()
                }
            }
        }
    }
    
    override fun onViewRecycled(holder: RequestViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }
}