package dev.jahir.blueprint.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.ui.viewholders.HelpViewHolder
import dev.jahir.frames.extensions.views.inflate

class HelpAdapter : RecyclerView.Adapter<HelpViewHolder>() {

    var items: ArrayList<Pair<String, String>> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder =
        HelpViewHolder(parent.inflate(R.layout.item_help))

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}