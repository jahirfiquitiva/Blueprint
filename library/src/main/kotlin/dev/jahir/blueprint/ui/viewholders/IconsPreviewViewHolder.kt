package dev.jahir.blueprint.ui.viewholders

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.ui.widgets.IconsPreviewRecyclerView
import dev.jahir.frames.extensions.views.findView

class IconsPreviewViewHolder(itemView: View) : SectionedViewHolder(itemView) {

    private val wallpaperView: AppCompatImageView? by itemView.findView(R.id.wallpaper)
    private val iconsGrid: IconsPreviewRecyclerView? by itemView.findView(R.id.icons_preview_grid)

    fun bind(wallpaper: Drawable? = null) {
        wallpaperView?.setImageDrawable(wallpaper)
        iconsGrid?.resetIcons()
        itemView.setOnClickListener { iconsGrid?.resetIcons(true) }
    }
}