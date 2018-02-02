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
import jahirfiquitiva.libs.archhelpers.ui.adapters.ListAdapter
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Launcher
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.LauncherViewHolder

class LaunchersAdapter(val listener: (Launcher) -> Unit = {}) :
        ListAdapter<Launcher, LauncherViewHolder>() {
    override fun doCreateVH(parent: ViewGroup, viewType: Int): LauncherViewHolder =
            LauncherViewHolder(parent.inflate(R.layout.item_launcher))
    
    override fun doBind(holder: LauncherViewHolder, position: Int, shouldAnimate: Boolean) =
            holder.bind(list[position], listener)
    
    override fun onViewRecycled(holder: LauncherViewHolder?) {
        super.onViewRecycled(holder)
        holder?.unbind()
    }
}