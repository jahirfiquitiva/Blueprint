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
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.holders.IconHolder
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.utils.inflate

class IconsAdapter(val listener:(Icon) -> Unit):BaseListAdapter<Icon>() {
    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):IconHolder {
        return IconHolder(parent?.inflate(R.layout.item_icon))
    }

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder?, position:Int) {
        if (position < 0) return
        (holder as? IconHolder)?.bind(list[position], listener)
    }
}