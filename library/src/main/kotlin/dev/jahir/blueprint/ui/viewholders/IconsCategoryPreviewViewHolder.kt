package dev.jahir.blueprint.ui.viewholders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.ui.widgets.IconsPreviewRecyclerView
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.tint
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView
import dev.jahir.frames.extensions.views.gone
import dev.jahir.frames.extensions.views.setPaddingLeft
import dev.jahir.frames.extensions.views.setPaddingRight
import dev.jahir.frames.extensions.views.visible
import dev.jahir.frames.extensions.views.visibleIf

class IconsCategoryPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dividerView: View? by itemView.findView(R.id.divider_layout)
    private val categoryTitleView: TextView? by itemView.findView(R.id.category_title)
    private val categoryCountView: TextView? by itemView.findView(R.id.category_count)
    private val categoryOpenBtnView: AppCompatImageView? by itemView.findView(R.id.category_open_btn)
    private val categoryIconsPreviewView: IconsPreviewRecyclerView? by itemView.findView(R.id.category_icons_preview)

    fun bind(
        category: IconsCategory,
        showDivider: Boolean = true,
        onOpenCategory: ((IconsCategory) -> Unit)? = null,
        onIconClick: ((Icon, Drawable?) -> Unit)? = null
    ) {
        dividerView?.visibleIf(showDivider)
        categoryTitleView?.text = category.title
        categoryCountView?.text =
            if (category.showCount) context.string(R.string.x_icons, category.count) else ""
        if (category.count > context.integer(R.integer.icons_columns_count)) {
            categoryCountView?.setPaddingLeft(0)
            categoryCountView?.setPaddingRight(0)
            categoryOpenBtnView?.setImageDrawable(
                context.drawable(R.drawable.ic_open_category)
                    ?.tint(
                        context.resolveColor(
                            R.attr.colorOnSurface,
                            context.color(R.color.onSurface)
                        )
                    )
            )
            categoryOpenBtnView?.setOnClickListener { onOpenCategory?.invoke(category) }
            categoryOpenBtnView?.visible()
        } else {
            categoryCountView?.setPaddingLeft(20.dpToPx)
            categoryCountView?.setPaddingRight(20.dpToPx)
            categoryOpenBtnView?.gone()
        }
        categoryIconsPreviewView?.animateIcons = false
        categoryIconsPreviewView?.setOnIconClickListener(onIconClick)
        categoryIconsPreviewView?.setIcons(category.getIconsForPreview(context))
    }
}