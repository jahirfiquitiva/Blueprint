package dev.jahir.blueprint.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.view.children
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.Counter
import dev.jahir.blueprint.data.models.HomeItem
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCounter
import dev.jahir.blueprint.data.models.KustomCounter
import dev.jahir.blueprint.data.models.WallpapersCounter
import dev.jahir.blueprint.data.models.ZooperCounter
import dev.jahir.blueprint.ui.viewholders.AppLinkViewHolder
import dev.jahir.blueprint.ui.viewholders.CounterViewHolder
import dev.jahir.blueprint.ui.viewholders.HomeActionsViewHolder
import dev.jahir.blueprint.ui.viewholders.IconsPreviewViewHolder
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.inflate
import dev.jahir.frames.ui.viewholders.SectionHeaderViewHolder

@Suppress("MemberVisibilityCanBePrivate")
class HomeAdapter(
    actionsStyle: Int = 1,
    showOverview: Boolean = true,
    private val listener: HomeItemsListener? = null
) : SectionedRecyclerViewAdapter<SectionedViewHolder>() {

    var actionsStyle: Int = actionsStyle
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    private val showActions: Boolean
        get() = actionsStyle > 0

    var showDonateButton: Boolean = showOverview
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    var showOverview: Boolean = showOverview
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    var wallpaper: Drawable? = null
        set(value) {
            if (field != null) return
            field = value
            notifyDataSetChanged()
        }

    var iconsPreviewList: ArrayList<Icon> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    var homeItems: ArrayList<HomeItem> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    private val appItems: ArrayList<HomeItem>
        get() = ArrayList(homeItems.filter { it.isAnApp })

    private val linkItems: ArrayList<HomeItem>
        get() = ArrayList(homeItems.filter { !it.isAnApp })

    var iconsCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    var wallpapersCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    var kustomCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
        }

    var zooperCount: Int = 0
        set(value) {
            if (value == field) return
            field = value
            notifyDataSetChanged()
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
            ICONS_PREVIEW_SECTION -> IconsPreviewViewHolder(parent.inflate(R.layout.item_home_icons_preview))
            ACTIONS_SECTION ->
                HomeActionsViewHolder(
                    parent.inflate(
                        if (actionsStyle < 2) R.layout.item_home_actions
                        else R.layout.item_home_actions_big
                    )
                )
            OVERVIEW_SECTION -> CounterViewHolder(parent.inflate(R.layout.item_counter))
            MORE_APPS_SECTION, USEFUL_LINKS_SECTION ->
                AppLinkViewHolder(parent.inflate(R.layout.item_home_app_link))
            else -> SectionHeaderViewHolder(parent.inflate(R.layout.item_section_header))
        }

    override fun onBindHeaderViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        expanded: Boolean
    ) {
        if (section < OVERVIEW_SECTION) {
            (holder?.itemView as? ViewGroup)?.children?.forEach { it.gone() }
        } else {
            (holder as? SectionHeaderViewHolder)?.let {
                when (section) {
                    OVERVIEW_SECTION -> it.bind(R.string.overview, 0, false)
                    MORE_APPS_SECTION -> it.bind(R.string.more_apps, 0, counters.isNotEmpty())
                    USEFUL_LINKS_SECTION ->
                        it.bind(R.string.useful_links, 0, showOverview || appItems.isNotEmpty())
                    else -> it.bind(0, 0, false)
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
        (holder as? HomeActionsViewHolder)?.bind(showDonateButton, listener)
        (holder as? CounterViewHolder)?.bind(counters.getOrNull(relativePosition), listener)
        (holder as? AppLinkViewHolder)?.bind(
            if (section == MORE_APPS_SECTION) appItems.getOrNull(relativePosition)
            else linkItems.getOrNull(relativePosition), listener
        )
    }

    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    override fun getItemCount(section: Int): Int = when (section) {
        ICONS_PREVIEW_SECTION -> 1
        ACTIONS_SECTION -> if (showActions) 1 else 0
        OVERVIEW_SECTION -> if (showOverview) counters.size else 0
        MORE_APPS_SECTION -> appItems.size
        USEFUL_LINKS_SECTION -> linkItems.size
        else -> 0
    }

    override fun getSectionCount(): Int = SECTION_COUNT

    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
        section

    override fun getRowSpan(
        fullSpanSize: Int,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
    ): Int = if (section == OVERVIEW_SECTION && showOverview) 1 else 2

    companion object {
        private const val SECTION_COUNT = 5
        internal const val ICONS_PREVIEW_SECTION = 0
        private const val ACTIONS_SECTION = 1
        internal const val OVERVIEW_SECTION = 2
        private const val MORE_APPS_SECTION = 3
        private const val USEFUL_LINKS_SECTION = 4
    }
}