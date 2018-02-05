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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.extensions.defaultLauncher
import jahirfiquitiva.libs.blueprint.helpers.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.AppLinkItemHolder
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.ApplyCardHolder
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.CounterItemHolder
import jahirfiquitiva.libs.frames.ui.adapters.viewholders.SectionedHeaderViewHolder
import jahirfiquitiva.libs.kauextensions.extensions.SimpleAnimationListener
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import java.lang.ref.WeakReference

class HomeAdapter(
        private val actv: WeakReference<Activity?>,
        private var iconsCount: Int = 0,
        private var wallsCount: Int = 0,
        private val listener: (HomeItem) -> Unit = {}
                 ) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {
    
    private val list: ArrayList<HomeItem> = ArrayList()
    
    private var shouldShowApplyCard: Boolean = false
        get() {
            val initCard = activity?.defaultLauncher?.isActuallySupported == true
            return if (initCard) activity?.bpKonfigs?.isApplyCardDismissed == false else false
        }
    
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
        notifySectionChanged(1)
    }
    
    fun updateWallsCount(newCount: Int) {
        wallsCount = newCount
        notifySectionChanged(1)
    }
    
    override fun getSectionCount(): Int = 4
    
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
            section
    
    override fun onBindHeaderViewHolder(
            holder: SectionedViewHolder?,
            section: Int,
            expanded: Boolean
                                       ) {
        (holder as? SectionedHeaderViewHolder)?.let {
            when (section) {
                1 -> it.setTitle(R.string.general_info)
                2 -> it.setTitle(R.string.more_apps)
                3 -> it.setTitle(R.string.useful_links)
                else -> it.setTitle("")
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SectionedViewHolder? =
            parent?.let {
                when (viewType) {
                    0 ->
                        ApplyCardHolder(it.inflate(R.layout.item_home_apply_card))
                    1 ->
                        CounterItemHolder(it.inflate(R.layout.item_home_counters))
                    2, 3 ->
                        AppLinkItemHolder(it.inflate(R.layout.item_home_app_link))
                    else -> SectionedHeaderViewHolder(it.inflate(R.layout.item_section_header))
                }
            }
    
    override fun getItemCount(section: Int): Int {
        return when (section) {
            0 -> if (shouldShowApplyCard) 1 else 0
            1 -> 1
            2 -> list.filter { it.isAnApp }.size
            3 -> list.filter { !it.isAnApp }.size
            else -> 0
        }
    }
    
    override fun onBindViewHolder(
            holder: SectionedViewHolder?,
            section: Int,
            relativePosition: Int,
            absolutePosition: Int
                                 ) {
        (holder as? ApplyCardHolder)?.let { bindApplyCard(it) }
        (holder as? CounterItemHolder)?.let { bindCounters(it) }
        (holder as? AppLinkItemHolder)?.let { bindAppsAndLinks(it, section, relativePosition) }
    }
    
    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    
    private fun bindApplyCard(holder: ApplyCardHolder) {
        activity?.accentColor?.let {
            val titleColor = activity?.getPrimaryTextColorFor(it)
            val contentColor = activity?.getSecondaryTextColorFor(it)
            titleColor?.let {
                holder.applyTitle?.setTextColor(it)
                holder.dismissButton?.setTextColor(it)
                holder.applyButton?.setTextColor(it)
            }
            if (contentColor != null)
                holder.applyContent?.setTextColor(contentColor)
        }
        
        holder.applyTitle?.text = activity?.getString(
                R.string.apply_title, activity?.getAppName())
        holder.applyContent?.text = activity?.getString(
                R.string.apply_content, activity?.defaultLauncher?.name)
        
        holder.dismissButton?.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(
                    activity, android.R.anim.slide_out_right)
            anim.setAnimationListener(
                    object : SimpleAnimationListener() {
                        override fun onAnimationEnd(animation: Animation?) {
                            super.onAnimationEnd(animation)
                            activity?.bpKonfigs?.isApplyCardDismissed = true
                            shouldShowApplyCard = false
                            notifyItemRemoved(0)
                            notifyDataSetChanged()
                        }
                    })
            holder.itemView.startAnimation(anim)
        }
        
        holder.applyButton?.setOnClickListener {
            activity?.executeLauncherIntent(activity?.defaultLauncher?.name ?: "")
        }
    }
    
    private fun bindCounters(holder: CounterItemHolder) {
        val labelColor = activity?.primaryTextColor ?: Color.parseColor("#de000000")
        val counterColor = activity?.secondaryTextColor ?: Color.parseColor("#8a000000")
        val iconColor = activity?.chipsIconsColor ?: Color.parseColor("#8a000000")
        val bgColor = activity?.chipsColor ?: Color.parseColor("#e0e0e0")
        
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
                list.filter { if (section == 2) it.isAnApp else !it.isAnApp }[position], listener)
    }
}