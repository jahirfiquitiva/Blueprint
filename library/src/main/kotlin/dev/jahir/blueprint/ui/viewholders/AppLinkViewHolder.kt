package dev.jahir.blueprint.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.HomeItem
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.resources.tint
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView
import dev.jahir.frames.extensions.views.gone

class AppLinkViewHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val title: TextView? by itemView.findView(R.id.home_app_link_title)
    private val description: TextView? by itemView.findView(R.id.home_app_link_description)
    private val icon: AppCompatImageView? by itemView.findView(R.id.home_app_link_image)
    private val openIcon: AppCompatImageView? by itemView.findView(R.id.home_app_link_open_icon)

    fun bind(item: HomeItem?, listener: HomeItemsListener? = null) {
        item ?: return
        title?.text = item.title
        description?.text = item.description
        item.icon?.let {
            icon?.setImageDrawable(it)
        } ?: icon?.gone()
        item.openIcon?.let {
            openIcon?.setImageDrawable(
                context.drawable(it)?.tint(
                    context.resolveColor(R.attr.colorOnSurface, context.color(R.color.onSurface))
                )
            )
        } ?: openIcon?.gone()
        itemView.setOnClickListener {
            listener?.onAppLinkClicked(item.url, item.intent)
        }
    }
}