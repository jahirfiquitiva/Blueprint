package dev.jahir.blueprint.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.ui.viewholders.IconViewHolder
import dev.jahir.frames.extensions.views.inflate

class IconsAdapter(
    var animate: Boolean = true,
    var onClick: ((Icon) -> Unit)? = null
) : ListAdapter<Icon, IconViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder =
        IconViewHolder(parent.inflate(R.layout.item_icon))

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) =
        holder.bind(getItem(position), animate, onClick)

    private object DiffCallback : DiffUtil.ItemCallback<Icon>() {
        override fun areItemsTheSame(oldItem: Icon, newItem: Icon): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Icon, newItem: Icon): Boolean =
            oldItem.resId == newItem.resId
    }
}