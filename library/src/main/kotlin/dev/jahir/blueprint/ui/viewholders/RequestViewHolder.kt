package dev.jahir.blueprint.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.ui.widgets.RequestCardView
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cardView: RequestCardView? by itemView.findView(R.id.request_item_card)
    private val iconView: AppCompatImageView? by itemView.findView(R.id.icon)
    private val checkboxView: AppCompatCheckBox? by itemView.findView(R.id.checkbox)
    private val textView: TextView? by itemView.findView(R.id.name)

    @Suppress("DEPRECATION")
    fun bind(
        requestApp: RequestApp?,
        checked: Boolean,
        onCheckChange: ((requestApp: RequestApp, checked: Boolean) -> Unit)? = null
    ) {
        iconView?.setImageDrawable(requestApp?.icon)
        checkboxView?.setOnCheckedChangeListener(null)
        cardView?.setOnCheckedChangeListener(null)
        checkboxView?.isChecked = checked
        checkboxView?.isClickable = false
        val color =
            context.resolveColor(if (checked) R.attr.colorAccent else android.R.attr.textColorPrimary)
        textView?.text = requestApp?.name
        textView?.setTextColor(color)
        cardView?.isCheckable = true
        cardView?.isClickable = true
        cardView?.isEnabled = true
        cardView?.isChecked = checked
        requestApp?.let {
            cardView?.setOnCheckedChangeListener { _, isChecked ->
                onCheckChange?.invoke(requestApp, isChecked)
            }
        }
    }
}