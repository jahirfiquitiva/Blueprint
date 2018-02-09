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

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.AppLinkItemHolder
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.CounterItemHolder
import jahirfiquitiva.libs.frames.ui.adapters.viewholders.SectionedHeaderViewHolder
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kuper.helpers.extensions.tilesColor
import java.lang.ref.WeakReference

class HomeAdapter(
        private val actv: WeakReference<Activity?>,
        private var iconsCount: Int = 0,
        private var wallsCount: Int = 0,
        private val listener: (HomeItem) -> Unit = {}
                 ) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {
    
    private val list: ArrayList<HomeItem> = ArrayList()
    
    private val activity: Activity?
        get() = actv.get()
    
    init {
        shouldShowHeadersForEmptySections(false)
        shouldShowFooters(false)
    }
    
    companion object {
        private const val MINIMAL_AMOUNT = 0
    }
    
    fun updateItems(newItems: ArrayList<HomeItem>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }
    
    fun updateIconsCount(newCount: Int) {
        iconsCount = newCount
        notifySectionChanged(0)
    }
    
    fun updateWallsCount(newCount: Int) {
        wallsCount = newCount
        notifySectionChanged(0)
    }
    
    override fun getSectionCount(): Int = 3
    
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
            section
    
    override fun onBindHeaderViewHolder(
            holder: SectionedViewHolder?,
            section: Int,
            expanded: Boolean
                                       ) {
        (holder as? SectionedHeaderViewHolder)?.let {
            when (section) {
                0 -> it.setTitle(R.string.general_info)
                1 -> it.setTitle(R.string.more_apps)
                2 -> it.setTitle(R.string.useful_links)
                else -> it.setTitle("")
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SectionedViewHolder? =
            parent?.let {
                when (viewType) {
                    0 -> CounterItemHolder(it.inflate(R.layout.item_home_counters))
                    1, 2 ->
                        AppLinkItemHolder(it.inflate(R.layout.item_home_app_link))
                    else -> SectionedHeaderViewHolder(it.inflate(R.layout.item_section_header))
                }
            }
    
    override fun getItemCount(section: Int): Int {
        return when (section) {
            0 -> 1
            1 -> list.filter { it.isAnApp }.size
            2 -> list.filter { !it.isAnApp }.size
            else -> 0
        }
    }
    
    override fun onBindViewHolder(
            holder: SectionedViewHolder?,
            section: Int,
            relativePosition: Int,
            absolutePosition: Int
                                 ) {
        (holder as? CounterItemHolder)?.let { bindCounters(it) }
        (holder as? AppLinkItemHolder)?.let { bindAppsAndLinks(it, section, relativePosition) }
    }
    
    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    
    private fun bindCounters(holder: CounterItemHolder) {
        val bgColor = activity?.tilesColor ?: Color.parseColor("#e0e0e0")
        val labelColor = activity?.getPrimaryTextColorFor(bgColor) ?: Color.parseColor("#de000000")
        val counterColor =
                activity?.getSecondaryTextColorFor(bgColor) ?: Color.parseColor("#8a000000")
        val iconColor = activity?.getActiveIconsColorFor(bgColor) ?: Color.parseColor("#8a000000")
        
        if (iconsCount > MINIMAL_AMOUNT) {
            holder.iconsCounter?.setBackgroundColor(bgColor)
            holder.iconsCounterIcon?.setImageDrawable(
                    activity?.drawable(NavigationItem.ICONS.icon)?.tint(iconColor))
            holder.iconsCounterTitle?.setTextColor(labelColor)
            holder.iconsCounterCount?.setTextColor(counterColor)
            holder.iconsCounterCount?.text =
                    if (iconsCount > MINIMAL_AMOUNT) iconsCount.toString() else "…"
            holder.iconsCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.navigateToItem(NavigationItem.ICONS, false)
            }
            holder.iconsCounter?.visible()
        } else {
            holder.iconsCounter?.gone()
        }
        
        if (wallsCount > MINIMAL_AMOUNT) {
            holder.wallsCounter?.setBackgroundColor(bgColor)
            holder.wallsCounterIcon?.setImageDrawable(
                    activity?.drawable(NavigationItem.WALLPAPERS.icon)?.tint(iconColor))
            holder.wallsCounterTitle?.setTextColor(labelColor)
            holder.wallsCounterCount?.setTextColor(counterColor)
            holder.wallsCounterCount?.text =
                    if (wallsCount > MINIMAL_AMOUNT) wallsCount.toString() else "…"
            holder.wallsCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.navigateToItem(
                        NavigationItem.WALLPAPERS, false)
            }
            holder.wallsCounter?.visible()
        } else {
            holder.wallsCounter?.gone()
        }
        
        var kustomCount = activity?.resources?.assets?.list("komponents").orEmpty().size
        kustomCount += activity?.resources?.assets?.list("lockscreens").orEmpty().size
        kustomCount += activity?.resources?.assets?.list("wallpapers").orEmpty().size
        kustomCount += activity?.resources?.assets?.list("widgets").orEmpty().size
        
        if (kustomCount > MINIMAL_AMOUNT) {
            holder.kwgtCounter?.setBackgroundColor(bgColor)
            holder.kwgtCounterIcon?.setImageDrawable(
                    activity?.drawable(R.drawable.ic_kustom)?.tint(iconColor))
            holder.kwgtCounterTitle?.setTextColor(labelColor)
            holder.kwgtCounterCount?.setTextColor(counterColor)
            holder.kwgtCounterCount?.text = activity?.getString(
                    R.string.included_templates,
                    if (kustomCount > MINIMAL_AMOUNT) kustomCount.toString() else "…")
            holder.kwgtCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.launchKuperActivity()
            }
            holder.kwgtCounter?.visible()
        } else {
            holder.kwgtCounter?.gone()
        }
        
        val zooperCount = activity?.resources?.assets?.list("templates").orEmpty().size
        
        if (zooperCount > MINIMAL_AMOUNT) {
            holder.zooperCounter?.setBackgroundColor(bgColor)
            holder.zooperCounterIcon?.setImageDrawable(
                    activity?.drawable(R.drawable.ic_zooper)?.tint(iconColor))
            holder.zooperCounterTitle?.setTextColor(labelColor)
            holder.zooperCounterCount?.setTextColor(counterColor)
            holder.zooperCounterCount?.text = activity?.getString(
                    R.string.included_templates,
                    if (zooperCount > MINIMAL_AMOUNT) zooperCount.toString() else "…")
            holder.zooperCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.launchKuperActivity()
            }
            holder.zooperCounter?.visible()
        } else {
            holder.zooperCounter?.gone()
        }
    }
    
    private fun bindAppsAndLinks(holder: AppLinkItemHolder, section: Int, position: Int) {
        holder.setItem(
                list.filter { if (section == 1) it.isAnApp else !it.isAnApp }[position], listener)
    }
}