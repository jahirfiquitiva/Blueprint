package dev.jahir.blueprint.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.view.children
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.safeNotifySectionChanged
import dev.jahir.blueprint.ui.viewholders.IconsPreviewViewHolder
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.inflate
import dev.jahir.frames.ui.viewholders.SectionHeaderViewHolder

class HomeAdapter(wallpaper: Drawable? = null) :
    SectionedRecyclerViewAdapter<SectionedViewHolder>() {

    var wallpaper: Drawable? = wallpaper
        set(value) {
            field = value
            safeNotifySectionChanged(0)
        }

    init {
        shouldShowFooters(false)
        shouldShowHeadersForEmptySections(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionedViewHolder =
        when (viewType) {
            0 -> IconsPreviewViewHolder(parent.inflate(R.layout.item_home_icons_preview))
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
            (holder as? SectionHeaderViewHolder)?.bind("Algo", "")
        }
    }

    override fun onBindViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
    ) {
        (holder as? IconsPreviewViewHolder)?.bind(wallpaper)
    }

    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    override fun getItemCount(section: Int): Int = 1
    override fun getSectionCount(): Int = 1
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
        section
}