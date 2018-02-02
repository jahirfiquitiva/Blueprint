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
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

class HomeItemsViewHolders {
    
    class ApplyCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val applyTitle: TextView?by itemView.bind(R.id.apply_title)
        val applyContent: TextView?by itemView.bind(R.id.apply_content)
        val dismissButton: AppCompatButton?by itemView.bind(R.id.apply_dismiss)
        val applyButton: AppCompatButton?by itemView.bind(R.id.apply_ok)
    }
    
    class CounterItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sectionTitle: TextView? by itemView.bind(R.id.general_info_section_title)
        
        val iconsCounter: LinearLayout? by itemView.bind(R.id.icons_counter)
        val iconsCounterTitle: TextView? by itemView.bind(R.id.icons_counter_title)
        val iconsCounterCount: TextView? by itemView.bind(R.id.icons_counter_count)
        val iconsCounterIcon: ImageView? by itemView.bind(R.id.icons_counter_icon)
        
        val wallsCounter: LinearLayout? by itemView.bind(R.id.walls_counter)
        val wallsCounterTitle: TextView? by itemView.bind(R.id.walls_counter_title)
        val wallsCounterCount: TextView? by itemView.bind(R.id.walls_counter_count)
        val wallsCounterIcon: ImageView? by itemView.bind(R.id.walls_counter_icon)
        
        val kwgtCounter: LinearLayout? by itemView.bind(R.id.kwgt_counter)
        val kwgtCounterTitle: TextView? by itemView.bind(R.id.kwgt_counter_title)
        val kwgtCounterCount: TextView? by itemView.bind(R.id.kwgt_counter_count)
        val kwgtCounterIcon: ImageView? by itemView.bind(R.id.kwgt_counter_icon)
        
        val zooperCounter: LinearLayout? by itemView.bind(R.id.zooper_counter)
        val zooperCounterTitle: TextView? by itemView.bind(R.id.zooper_counter_title)
        val zooperCounterCount: TextView? by itemView.bind(R.id.zooper_counter_count)
        val zooperCounterIcon: ImageView? by itemView.bind(R.id.zooper_counter_icon)
    }
    
    class AppLinkItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: LinearLayout? by itemView.bind(R.id.home_app_link_sub_layout)
        val sectionTitle: TextView? by itemView.bind(R.id.more_apps_section_title)
        val title: TextView? by itemView.bind(R.id.home_app_link_title)
        val description: TextView? by itemView.bind(R.id.home_app_link_description)
        val icon: ImageView? by itemView.bind(R.id.home_app_link_image)
        val openIcon: ImageView? by itemView.bind(R.id.home_app_link_open_icon)
        
        fun setItem(item: HomeItem, showSectionTitle: Boolean, listener: (HomeItem) -> Unit) =
                with(itemView) {
                    sectionTitle?.visibleIf(showSectionTitle)
                    if (showSectionTitle) {
                        if (item.isAnApp) sectionTitle?.text = itemView.context.getString(
                                R.string.more_apps)
                        else sectionTitle?.text = itemView.context.getString(R.string.useful_links)
                        sectionTitle?.setTextColor(context.secondaryTextColor)
                    }
                    title?.text = item.title
                    description?.text = item.description
                    icon?.setImageDrawable(item.icon)
                    openIcon?.setImageDrawable(item.openIcon?.tint(context.activeIconsColor))
                    openIcon?.setOnClickListener { listener(item) }
                }
    }
    
}