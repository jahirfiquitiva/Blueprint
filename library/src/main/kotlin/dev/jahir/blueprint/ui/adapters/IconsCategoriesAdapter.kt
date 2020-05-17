package dev.jahir.blueprint.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.ui.viewholders.IconsCategoryPreviewViewHolder
import dev.jahir.frames.extensions.views.inflate

class IconsCategoriesAdapter(
    private val onOpenCategory: ((IconsCategory) -> Unit)? = null,
    private val onIconClick: ((Icon, Drawable?) -> Unit)? = null
) : RecyclerView.Adapter<IconsCategoryPreviewViewHolder>() {

    var categories: ArrayList<IconsCategory> = ArrayList()
        set(value) {
            field = ArrayList(value.filter { it.hasIcons() })
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IconsCategoryPreviewViewHolder =
        IconsCategoryPreviewViewHolder(parent.inflate(R.layout.item_category_preview))

    override fun onBindViewHolder(holder: IconsCategoryPreviewViewHolder, position: Int) =
        holder.bind(categories[position], position > 0, onOpenCategory, onIconClick)

    override fun getItemCount(): Int = categories.size
}