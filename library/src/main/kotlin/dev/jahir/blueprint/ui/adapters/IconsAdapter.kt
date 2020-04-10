package dev.jahir.blueprint.ui.adapters

import android.view.ViewGroup
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.ui.viewholders.IconViewHolder
import dev.jahir.frames.extensions.views.inflate

class IconsAdapter(private val onClick: (Icon) -> Unit) :
    SectionedRecyclerViewAdapter<SectionedViewHolder>() {

    var icons: ArrayList<Icon> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    init {
        shouldShowFooters(false)
        shouldShowHeadersForEmptySections(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder =
        IconViewHolder(parent.inflate(R.layout.item_icon))

    override fun onBindHeaderViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        expanded: Boolean
    ) {
        //
    }

    override fun onBindViewHolder(
        holder: SectionedViewHolder?,
        section: Int,
        relativePosition: Int,
        absolutePosition: Int
    ) {
        (holder as? IconViewHolder)?.bind(icons[relativePosition], false, onClick)
    }

    override fun onBindFooterViewHolder(holder: SectionedViewHolder?, section: Int) {}
    override fun getItemCount(section: Int): Int = icons.size
    override fun getSectionCount(): Int = 1
    override fun getItemViewType(section: Int, relativePosition: Int, absolutePosition: Int): Int =
        section
}