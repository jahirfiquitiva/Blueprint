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

package jahirfiquitiva.libs.blueprint.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.makeVisibleIf
import jahirfiquitiva.libs.blueprint.models.HomeCard

class HomeCardsHolder {

    class WelcomeCardHolder(itemView:View?, val icons:Int = 0, val wallpapers:Int = 0,
                            val widgets:Int = 0):RecyclerView.ViewHolder(itemView)

    class ExtraCardHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val root:LinearLayout? = itemView?.findViewById(R.id.home_extra_card_sub_layout)
        val sectionTitle:TextView? = itemView?.findViewById(R.id.more_apps_section_title)
        val title:TextView? = itemView?.findViewById(R.id.home_extra_card_title)
        val description:TextView? = itemView?.findViewById(R.id.home_extra_card_description)
        val icon:ImageView? = itemView?.findViewById(R.id.home_extra_card_image)

        fun setItem(item:HomeCard, showSectionTitle:Boolean, listener:(HomeCard) -> Unit) =
                with(itemView) {
                    sectionTitle?.makeVisibleIf(showSectionTitle)
                    title?.text = item.title
                    description?.text = item.description
                    icon?.setImageDrawable(item.icon)
                    setOnClickListener { listener(item) }
                }
    }

}