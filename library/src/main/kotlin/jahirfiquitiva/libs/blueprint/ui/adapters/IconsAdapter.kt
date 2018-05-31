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
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.IconViewHolder

class IconsAdapter(
    private val manager: RequestManager?,
    private val fromPreviews: Boolean = false,
    private val listener: (Icon) -> Unit = {}
                  ) :
    RecyclerViewListAdapter<Icon, IconViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder =
        IconViewHolder(parent.inflate(R.layout.item_icon))
    
    override fun doBind(holder: IconViewHolder, position: Int, shouldAnimate: Boolean) {
        if (fromPreviews) {
            (holder as? IconViewHolder)?.bind(manager, true, list[position], false)
        } else {
            (holder as? IconViewHolder)?.bind(manager, false, list[position], true, listener)
        }
    }
    
    override fun onViewRecycled(holder: IconViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }
}