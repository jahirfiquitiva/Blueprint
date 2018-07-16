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
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import com.bumptech.glide.RequestManager
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.activities.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.AppLinkItemHolder
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.CounterItemHolder
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.PreviewCardHolder
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.helpers.extensions.tilesColor
import jahirfiquitiva.libs.frames.ui.adapters.viewholders.SectionedHeaderViewHolder
import jahirfiquitiva.libs.kext.extensions.boolean
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.getSecondaryTextColorFor
import java.lang.ref.WeakReference

class HomeAdapter(
    private val actv: WeakReference<Activity?>,
    private val manager: RequestManager? = null,
    private var iconsCount: Int = 0,
    private var wallsCount: Int = 0,
    private val listener: (HomeItem) -> Unit = {}
                 ) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {
    
    private val list: ArrayList<HomeItem> = ArrayList()
    
    private val activity: Activity?
        get() = actv.get()
    
    private val showInfo: Boolean = activity?.boolean(R.bool.show_info) ?: true
    
    private val pool: RecyclerView.RecycledViewPool by lazy {
        RecyclerView.RecycledViewPool()
    }
    
    private var wallpaper: Drawable? = null
    private var onlyPicture: Boolean = false
    
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
        val rightSection = if (showInfo) 2 else 1
        try {
            notifySectionChanged(rightSection)
        } catch (e: Exception) {
        }
        try {
            notifySectionChanged(rightSection + 1)
        } catch (e: Exception) {
        }
    }
    
    fun updateWallpaper(wallpaper: Drawable?, onlyPicture: Boolean) {
        this.wallpaper = wallpaper
        this.onlyPicture = onlyPicture
        try {
            notifySectionChanged(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun updateIconsCount(newCount: Int) {
        if (showInfo) {
            iconsCount = newCount
            try {
                notifySectionChanged(1)
            } catch (e: Exception) {
            }
        }
    }
    
    fun updateWallsCount(newCount: Int) {
        if (showInfo) {
            wallsCount = newCount
            try {
                notifySectionChanged(1)
            } catch (e: Exception) {
            }
        }
    }
    
    override fun getSectionCount(): Int = if (showInfo) 4 else 3
    
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
        section
    
    override fun onBindHeaderViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        expanded: Boolean
                                       ) {
        (holder as? SectionedHeaderViewHolder)?.let {
            when (section) {
                1 -> {
                    if (showInfo) it.setTitle(R.string.general_info)
                    else it.setTitle(R.string.more_apps)
                }
                2 -> {
                    if (showInfo) it.setTitle(R.string.more_apps)
                    else it.setTitle(R.string.useful_links)
                }
                3 -> it.setTitle(R.string.useful_links)
                else -> it.setTitle("")
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionedViewHolder =
        when (viewType) {
            0 -> {
                val holder = PreviewCardHolder(parent.inflate(R.layout.item_home_icons_preview))
                holder.setPool(pool)
                holder
            }
            1 -> {
                if (showInfo) CounterItemHolder(parent.inflate(R.layout.item_home_counters))
                else AppLinkItemHolder(parent.inflate(R.layout.item_home_app_link))
            }
            2, 3 ->
                AppLinkItemHolder(parent.inflate(R.layout.item_home_app_link))
            else -> SectionedHeaderViewHolder(parent.inflate(R.layout.item_section_header))
        }
    
    override fun getItemCount(section: Int): Int {
        return when (section) {
            0 -> 1
            1 -> if (showInfo) 1 else list.jfilter { it.isAnApp }.size
            2 -> list.jfilter { if (showInfo) it.isAnApp else !it.isAnApp }.size
            3 -> list.jfilter { !it.isAnApp }.size
            else -> 0
        }
    }
    
    override fun onBindViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
                                 ) {
        (holder as? PreviewCardHolder)?.let { bindPreviewCard(it) }
        (holder as? CounterItemHolder)?.let { bindCounters(it) }
        (holder as? AppLinkItemHolder)?.let { bindAppsAndLinks(it, section, relativePosition) }
    }
    
    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    
    private fun bindPreviewCard(holder: PreviewCardHolder) {
        holder.bind(wallpaper, onlyPicture, manager)
    }
    
    private fun bindCounters(holder: CounterItemHolder) {
        val bgColor = activity?.tilesColor ?: Color.parseColor("#e0e0e0")
        val labelColor =
            activity?.getPrimaryTextColorFor(bgColor, 0.6F) ?: Color.parseColor("#de000000")
        val counterColor =
            activity?.getSecondaryTextColorFor(bgColor, 0.6F) ?: Color.parseColor("#8a000000")
        val iconColor =
            activity?.getActiveIconsColorFor(bgColor, 0.6F) ?: Color.parseColor("#8a000000")
        
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
        val correctSection = if (showInfo) 1 else 0
        holder.setItem(
            list.jfilter { if (section == correctSection) it.isAnApp else !it.isAnApp }[position],
            listener)
    }
}