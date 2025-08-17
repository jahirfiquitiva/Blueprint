package dev.jahir.blueprint.ui.viewholders

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.HomeMenuItem
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class MenuItemViewHolder(itemView: View) : SectionedViewHolder(itemView) {

    private val menuItemButton: AppCompatButton? by itemView.findView(R.id.home_item_button)

    fun bind(menuItem: HomeMenuItem?, listener: HomeItemsListener? = null) {
        menuItem ?: return
        menuItemButton?.let {
            it.text = context.string(menuItem.title)
            it.setCompoundDrawablesWithIntrinsicBounds(
                context.drawable(menuItem.icon),
                null,
                null,
                null
            )
            it.setOnClickListener { listener?.onMenuItemClicked(menuItem.id) }
        }
    }
}
