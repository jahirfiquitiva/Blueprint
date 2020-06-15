package dev.jahir.blueprint.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.ui.viewholders.IconViewHolder
import dev.jahir.frames.extensions.views.inflate

class IconsAdapter(
    var animate: Boolean = true,
    var onClick: ((Icon, Drawable?) -> Unit)? = null
) : RecyclerView.Adapter<IconViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var icons: ArrayList<Icon> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder =
        IconViewHolder(parent.inflate(R.layout.item_icon))

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) =
        holder.bind(icons[position], animate, onClick)

    override fun getItemCount(): Int = icons.size

    override fun onViewRecycled(holder: IconViewHolder) {
        super.onViewRecycled(holder)
        if (!animate) holder.unbind()
    }

    override fun getItemId(position: Int): Long = icons[position].resId.hashCode().toLong()
}