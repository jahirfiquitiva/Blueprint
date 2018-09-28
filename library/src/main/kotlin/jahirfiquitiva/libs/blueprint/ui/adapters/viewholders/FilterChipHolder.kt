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
package jahirfiquitiva.libs.blueprint.ui.adapters.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Filter
import jahirfiquitiva.libs.blueprint.ui.widgets.SelectableChip
import jahirfiquitiva.libs.kext.extensions.bind

class FilterChipHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val chip: SelectableChip? by itemView.bind(R.id.filter_chip)
    
    fun bind(filter: Filter, select: Boolean, onSelectionChange: (Filter, Boolean) -> Unit) {
        chip?.text = filter.title
        chip?.selectedColor = filter.color
        chip?.setOnSelectedListener { onSelectionChange(filter, it) }
        chip?.initClickListener()
        chip?.chipSelected = filter.selected || select
    }
}