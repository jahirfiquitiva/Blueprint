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

package jahirfiquitiva.libs.blueprint.ui.adapters.viewholders

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visibleIf
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

class HomeItemsViewHolders {
    
    class ApplyCardHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val applyTitle:TextView = itemView.findViewById(R.id.apply_title)
        val applyContent:TextView = itemView.findViewById(R.id.apply_content)
        val dismissButton:AppCompatButton = itemView.findViewById(R.id.apply_dismiss)
        val applyButton:AppCompatButton = itemView.findViewById(R.id.apply_ok)
    }
    
    class CounterItemHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val iconsCounter:LinearLayout = itemView.findViewById(R.id.icons_counter)
        val iconsCounterTitle:TextView = itemView.findViewById(R.id.icons_counter_title)
        val iconsCounterCount:TextView = itemView.findViewById(R.id.icons_counter_count)
        val iconsCounterIcon:ImageView = itemView.findViewById(R.id.icons_counter_icon)
        
        val wallsCounter:LinearLayout = itemView.findViewById(R.id.walls_counter)
        val wallsCounterTitle:TextView = itemView.findViewById(R.id.walls_counter_title)
        val wallsCounterCount:TextView = itemView.findViewById(R.id.walls_counter_count)
        val wallsCounterIcon:ImageView = itemView.findViewById(R.id.walls_counter_icon)
        
        val kwgtCounter:LinearLayout = itemView.findViewById(R.id.kwgt_counter)
        val kwgtCounterTitle:TextView = itemView.findViewById(R.id.kwgt_counter_title)
        val kwgtCounterCount:TextView = itemView.findViewById(R.id.kwgt_counter_count)
        val kwgtCounterIcon:ImageView = itemView.findViewById(R.id.kwgt_counter_icon)
        
        val zooperCounter:LinearLayout = itemView.findViewById(R.id.zooper_counter)
        val zooperCounterTitle:TextView = itemView.findViewById(R.id.zooper_counter_title)
        val zooperCounterCount:TextView = itemView.findViewById(R.id.zooper_counter_count)
        val zooperCounterIcon:ImageView = itemView.findViewById(R.id.zooper_counter_icon)
    }
    
    class AppLinkItemHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val root:LinearLayout = itemView.findViewById(R.id.home_app_link_sub_layout)
        val sectionTitle:TextView = itemView.findViewById(R.id.more_apps_section_title)
        val title:TextView = itemView.findViewById(R.id.home_app_link_title)
        val description:TextView = itemView.findViewById(R.id.home_app_link_description)
        val icon:ImageView = itemView.findViewById(R.id.home_app_link_image)
        val openIcon:ImageView = itemView.findViewById(R.id.home_app_link_open_icon)
        
        fun setItem(item:HomeItem, showSectionTitle:Boolean, listener:(HomeItem) -> Unit) =
                with(itemView) {
                    sectionTitle.visibleIf(showSectionTitle)
                    if (showSectionTitle) {
                        if (item.isAnApp) sectionTitle.text = itemView.context.getString(
                                R.string.more_apps)
                        else sectionTitle.text = itemView.context.getString(R.string.useful_links)
                        sectionTitle.setTextColor(
                                context.secondaryTextColor)
                    }
                    title.text = item.title
                    description.text = item.description
                    icon.setImageDrawable(item.icon)
                    openIcon.setImageDrawable(item.openIcon?.tint(context.activeIconsColor))
                    openIcon.setOnClickListener { listener(item) }
                }
    }
    
}