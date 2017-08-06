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

package jahirfiquitiva.libs.blueprint.holders

import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visibleIf
import com.pchmn.materialchips.ChipView
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

class HomeItemsHolder {
    
    class ApplyCardHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val applyTitle:TextView? = itemView?.findViewById(R.id.apply_title)
        val applyContent:TextView? = itemView?.findViewById(R.id.apply_content)
        val dismissButton:AppCompatButton? = itemView?.findViewById(R.id.apply_dismiss)
        val applyButton:AppCompatButton? = itemView?.findViewById(R.id.apply_ok)
    }
    
    class ChipsItemHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val chipsTitle:TextView? = itemView?.findViewById(R.id.chips_title)
        val iconsChip:ChipView? = itemView?.findViewById(R.id.icons_chip)
        val wallsChip:ChipView? = itemView?.findViewById(R.id.wallpapers_chip)
        val widgetsChips:LinearLayout? = itemView?.findViewById(R.id.widgets_chips)
        val zooperChip:ChipView? = itemView?.findViewById(R.id.zooper_chip)
        val kustomChip:ChipView? = itemView?.findViewById(R.id.kustom_chip)
    }
    
    class AppLinkItemHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
        val root:LinearLayout? = itemView?.findViewById(R.id.home_app_link_sub_layout)
        val sectionTitle:TextView? = itemView?.findViewById(R.id.more_apps_section_title)
        val title:TextView? = itemView?.findViewById(R.id.home_app_link_title)
        val description:TextView? = itemView?.findViewById(R.id.home_app_link_description)
        val icon:ImageView? = itemView?.findViewById(R.id.home_app_link_image)
        val openIcon:ImageView? = itemView?.findViewById(R.id.home_app_link_open_icon)
        
        fun setItem(item:HomeItem, showSectionTitle:Boolean, listener:(HomeItem) -> Unit) =
                with(itemView) {
                    sectionTitle?.visibleIf(showSectionTitle)
                    if (showSectionTitle) {
                        if (item.isAnApp) sectionTitle?.text = itemView.context.getString(
                                R.string.more_apps)
                        else sectionTitle?.text = itemView.context.getString(R.string.useful_links)
                        sectionTitle?.setTextColor(
                                context.secondaryTextColor)
                    }
                    title?.text = item.title
                    description?.text = item.description
                    icon?.setImageDrawable(item.icon)
                    openIcon?.setImageDrawable(item.openIcon?.tint(context.activeIconsColor))
                    openIcon?.setOnClickListener { listener(item) }
                }
    }
    
}