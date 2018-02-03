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
import android.app.WallpaperManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.tint
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
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.PreviewCardHolder
import jahirfiquitiva.libs.frames.ui.adapters.viewholders.SectionedHeaderViewHolder
import jahirfiquitiva.libs.kauextensions.extensions.SimpleAnimationListener
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import java.lang.ref.WeakReference

class HomeAdapter(
        private val actv: WeakReference<Activity?>,
        private val iconsCount: Int = 0,
        private val wallsCount: Int = 0,
        private val listener: (HomeItem) -> Unit = {}
                 ) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {
    
    private val list: ArrayList<HomeItem> = ArrayList()
    
    private var firstLinkPosition: Int = -1
    
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
        private const val MINIMAL_AMOUNT = -1
    }
    
    fun updateItems(newItems: ArrayList<HomeItem>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }
    
    override fun getSectionCount(): Int = 5
    
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
            section
    
    override fun onBindHeaderViewHolder(
            holder: SectionedViewHolder?,
            section: Int,
            expanded: Boolean
                                       ) {
        (holder as? SectionedHeaderViewHolder)?.let {
            when (section) {
                2 -> it.setTitle(R.string.general_info)
                3 -> it.setTitle(R.string.more_apps)
                4 -> it.setTitle(R.string.useful_links)
                else -> it.setTitle("")
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SectionedViewHolder? =
            parent?.let {
                when (viewType) {
                    0 ->
                        PreviewCardHolder(
                                it.inflate(R.layout.item_home_icons_preview))
                    1 ->
                        ApplyCardHolder(it.inflate(R.layout.item_home_apply_card))
                    2 ->
                        CounterItemHolder(it.inflate(R.layout.item_home_counters))
                    3, 4 ->
                        AppLinkItemHolder(it.inflate(R.layout.item_home_app_link))
                    else -> SectionedHeaderViewHolder(it.inflate(R.layout.item_section_header))
                }
            }
    
    override fun getItemCount(section: Int): Int {
        return when (section) {
            0 -> 1
            1 -> if (shouldShowApplyCard) 1 else 0
            2 -> 1
            3 -> list.filter { it.isAnApp }.size
            4 -> list.filter { !it.isAnApp }.size
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
        (holder as? ApplyCardHolder)?.let { bindApplyCard(it) }
        (holder as? CounterItemHolder)?.let { bindCounters(it) }
        (holder as? AppLinkItemHolder)?.let { bindAppsAndLinks(it, section, relativePosition) }
    }
    
    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    
    private fun bindPreviewCard(holder: PreviewCardHolder) {
        val wallManager: WallpaperManager? = WallpaperManager.getInstance(activity)
        val drawable: Drawable? =
                if (activity?.bpKonfigs?.wallpaperInIconsPreview == true) {
                    try {
                        wallManager?.fastDrawable
                    } catch (e: Exception) {
                        defaultPicture
                    }
                } else {
                    defaultPicture
                }
        holder.bind(drawable)
    }
    
    private val defaultPicture: Drawable?
        get() {
            val picName = activity?.getString(R.string.icons_preview_picture)
            return if (picName.orEmpty().hasContent()) {
                activity?.let {
                    try {
                        picName?.getDrawable(it)
                    } catch (ignored: Exception) {
                        null
                    }
                }
            } else null
        }
    
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
        
        // holder.sectionTitle?.setTextColor(counterColor)
        
        if (iconsCount > MINIMAL_AMOUNT) {
            holder.iconsCounter?.setBackgroundColor(bgColor)
            holder.iconsCounterIcon?.setImageDrawable(
                    activity?.drawable(NavigationItem.ICONS.icon)?.tint(iconColor))
            holder.iconsCounterTitle?.setTextColor(labelColor)
            holder.iconsCounterCount?.setTextColor(counterColor)
            holder.iconsCounterCount?.text = iconsCount.toString()
            holder.iconsCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.navigateToItem(NavigationItem.ICONS)
            }
        } else {
            holder.iconsCounter?.gone()
        }
        
        if (wallsCount > MINIMAL_AMOUNT) {
            holder.wallsCounter?.setBackgroundColor(bgColor)
            holder.wallsCounterIcon?.setImageDrawable(
                    activity?.drawable(NavigationItem.WALLPAPERS.icon)?.tint(iconColor))
            holder.wallsCounterTitle?.setTextColor(labelColor)
            holder.wallsCounterCount?.setTextColor(counterColor)
            holder.wallsCounterCount?.text = wallsCount.toString()
            holder.wallsCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.navigateToItem(NavigationItem.WALLPAPERS)
            }
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
                    R.string.included_templates, kustomCount.toString())
            holder.kwgtCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.launchKuperActivity()
            }
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
                    R.string.included_templates, zooperCount.toString())
            holder.zooperCounter?.setOnClickListener {
                (activity as? BaseBlueprintActivity)?.launchKuperActivity()
            }
        } else {
            holder.zooperCounter?.gone()
        }
    }
    
    private fun bindAppsAndLinks(holder: AppLinkItemHolder, section: Int, position: Int) {
        holder.setItem(
                list.filter { if (section == 3) it.isAnApp else !it.isAnApp }[position], listener)
    }
}