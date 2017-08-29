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
import android.widget.FrameLayout
import ca.allanwang.kau.utils.inflate
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.IconViewHolder
import jahirfiquitiva.libs.frames.ui.adapters.BaseListAdapter

class IconsAdapter(val animate:Boolean = false, val listener:(Icon) -> Unit = {}):
        BaseListAdapter<Icon, IconViewHolder>() {
    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):IconViewHolder =
            IconViewHolder(parent?.inflate(R.layout.item_icon) ?: FrameLayout(null))
    
    override fun doBind(holder:IconViewHolder, position:Int, shouldAnimate:Boolean) {
        if (animate) (holder as? IconViewHolder)?.bind(animate && shouldAnimate, list[position])
        else (holder as? IconViewHolder)?.bind(false, list[position], listener)
    }
}