package dev.jahir.blueprint.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.Counter
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.resources.tint
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class CounterViewHolder(itemView: View) : SectionedViewHolder(itemView) {

    private val iconView: AppCompatImageView? by itemView.findView(R.id.stat_icon)
    private val titleView: TextView? by itemView.findView(R.id.stat_title)
    private val descriptionView: TextView? by itemView.findView(R.id.stat_description)

    fun bind(counter: Counter?, listener: HomeItemsListener? = null) {
        counter ?: return
        iconView?.setImageDrawable(
            context.drawable(counter.icon)
                ?.tint(
                    context.resolveColor(
                        R.attr.colorOnSurface, context.color(R.color.onSurface)
                    )
                )
        )
        titleView?.text = counter.count.toString()
        descriptionView?.text = context.string(counter.title).lower()
        itemView.setOnClickListener { listener?.onCounterClicked(counter) }
    }
}