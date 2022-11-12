package dev.jahir.blueprint.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.jahir.blueprint.R
import dev.jahir.blueprint.ui.viewholders.HelpViewHolder
import dev.jahir.frames.extensions.views.inflate

class HelpAdapter : ListAdapter<Pair<String, String>, HelpViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder =
        HelpViewHolder(parent.inflate(R.layout.item_help))

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) =
        holder.bind(getItem(position))

    private object DiffCallback : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean = oldItem.first == newItem.first && oldItem.second == newItem.second

        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean = oldItem.first == newItem.first && oldItem.second == newItem.second
    }
}