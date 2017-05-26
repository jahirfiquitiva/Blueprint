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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.holders.HomeCardHolder
import jahirfiquitiva.libs.iconshowcase.models.HomeCard
import java.util.*

class HomeCardsAdapter(val cards:ArrayList<HomeCard>):
        RecyclerView.Adapter<HomeCardHolder.ExtraCardHolder>() {

    override fun onBindViewHolder(holder:HomeCardHolder.ExtraCardHolder?, position:Int) {
        holder?.setItem(cards[position])
    }

    override fun onCreateViewHolder(parent:ViewGroup?,
                                    viewType:Int):HomeCardHolder.ExtraCardHolder =
            HomeCardHolder.ExtraCardHolder(
                    LayoutInflater.from(parent?.context)
                            .inflate(R.layout.item_home_extra_card, parent, false))

    override fun getItemCount():Int = cards.size
}