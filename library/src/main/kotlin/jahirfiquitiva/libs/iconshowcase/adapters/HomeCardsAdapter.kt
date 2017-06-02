/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.models.holders.HomeCardsHolder
import jahirfiquitiva.libs.iconshowcase.models.HomeCard
import jahirfiquitiva.libs.iconshowcase.utils.inflate

class HomeCardsAdapter(val listener:(HomeCard) -> Unit):BaseListAdapter<HomeCard>() {

    override fun onBindViewHolder(holder:RecyclerView.ViewHolder?, position:Int) {
        if (position < 0) return
        if (holder is HomeCardsHolder.ExtraCardHolder) {
            holder.setItem(list[position - 1], position == 1, listener)
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):RecyclerView.ViewHolder {
        when (viewType) {
            0 -> return HomeCardsHolder.WelcomeCardHolder(
                    parent?.inflate(R.layout.item_welcome_card))
            else -> return HomeCardsHolder.ExtraCardHolder(
                    parent?.inflate(R.layout.item_home_extra_card))
        }
    }

    override fun getItemCount():Int = list.size + 1

    override fun getItemViewType(position:Int):Int = position
}