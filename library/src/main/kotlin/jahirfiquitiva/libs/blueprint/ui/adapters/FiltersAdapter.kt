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
import jahirfiquitiva.libs.archhelpers.ui.adapters.RecyclerViewListAdapter
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Filter
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.FilterChipHolder

class FiltersAdapter(private val onSelectionChange: (Filter, Boolean) -> Unit) :
    RecyclerViewListAdapter<Filter, FilterChipHolder>() {
    
    val selectedFilters = ArrayList<Filter>()
    
    fun updateSelectedFilters(filters: ArrayList<Filter>) {
        selectedFilters.clear()
        selectedFilters.addAll(filters)
        notifyDataSetChanged()
    }
    
    fun toggleFilter(filter: Filter, checked: Boolean) {
        if (checked) addToSelected(filter) else removeFromSelected(filter)
    }
    
    private fun addToSelected(filter: Filter) {
        if (selectedFilters.contains(filter)) return
        selectedFilters.add(filter)
        notifyDataSetChanged()
    }
    
    private fun removeFromSelected(filter: Filter) {
        if (!selectedFilters.contains(filter)) return
        selectedFilters.remove(filter)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterChipHolder =
        FilterChipHolder(parent.inflate(R.layout.item_filter_chip))
    
    override fun doBind(holder: FilterChipHolder, position: Int, shouldAnimate: Boolean) {
        val rightItem = list[position]
        holder.bind(
            rightItem, selectedFilters.any { it.title.equals(rightItem.title, true) },
            onSelectionChange)
    }
}