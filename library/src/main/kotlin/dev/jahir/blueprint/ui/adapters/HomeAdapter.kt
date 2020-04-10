package dev.jahir.blueprint.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.view.children
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.Counter
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCounter
import dev.jahir.blueprint.data.models.KustomCounter
import dev.jahir.blueprint.data.models.WallpapersCounter
import dev.jahir.blueprint.data.models.ZooperCounter
import dev.jahir.blueprint.extensions.safeNotifySectionChanged
import dev.jahir.blueprint.ui.viewholders.CounterViewHolder
import dev.jahir.blueprint.ui.viewholders.IconsPreviewViewHolder
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.inflate
import dev.jahir.frames.ui.viewholders.SectionHeaderViewHolder

@Suppress("MemberVisibilityCanBePrivate")
class HomeAdapter(
    private var showOverview: Boolean = true,
    private var listener: HomeItemsListener? = null
) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {

    var wallpaper: Drawable? = null
        set(value) {
            field = value
            safeNotifySectionChanged(0)
        }

    var iconsPreviewList: ArrayList<Icon> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            safeNotifySectionChanged(0)
        }

    var iconsCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            safeNotifySectionChanged(1)
        }

    var wallpapersCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            safeNotifySectionChanged(1)
        }

    var kustomCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            safeNotifySectionChanged(1)
        }

    var zooperCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            safeNotifySectionChanged(1)
        }

    private val counters: List<Counter>
        get() = if (showOverview) {
            arrayOf(
                IconsCounter(iconsCount),
                WallpapersCounter(wallpapersCount),
                KustomCounter(kustomCount),
                ZooperCounter(zooperCount)
            ).filter { it.count > 0 }
        } else listOf()

    init {
        shouldShowFooters(false)
        shouldShowHeadersForEmptySections(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionedViewHolder =
        when (viewType) {
            0 -> IconsPreviewViewHolder(parent.inflate(R.layout.item_home_icons_preview))
            1 -> CounterViewHolder(parent.inflate(R.layout.item_stats))
            else -> SectionHeaderViewHolder(parent.inflate(R.layout.item_section_header))
        }

    override fun onBindHeaderViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        expanded: Boolean
    ) {
        if (section <= 0) {
            (holder?.itemView as? ViewGroup)?.children?.forEach { it.gone() }
        } else {
            when (section) {
                1 -> (holder as? SectionHeaderViewHolder)?.bind(R.string.overview, 0, false)
                else -> {
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
    ) {
        (holder as? IconsPreviewViewHolder)?.bind(iconsPreviewList, wallpaper, listener)
        (holder as? CounterViewHolder)?.bind(counters[relativePosition], listener)
    }

    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    override fun getItemCount(section: Int): Int = when (section) {
        0 -> 1
        1 -> counters.size
        else -> 0
    }

    override fun getSectionCount(): Int = if (showOverview) 2 else 1
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
        section

    override fun getRowSpan(
        fullSpanSize: Int,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
    ): Int = if (section == 1) 1 else 2
}