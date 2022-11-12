package dev.jahir.blueprint.ui.decorations

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.withAlpha

class GridDividerItemDecoration(context: Context?, orientation: Int) :
    DividerItemDecoration(context, orientation) {
    init {
        val dividerColor = (context?.color(R.color.dividers) ?: 0).withAlpha(.08F)
        try {
            setDrawable(
                GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(dividerColor, dividerColor)
                ).apply { setSize(1.dpToPx, 1.dpToPx) })
        } catch (e: Exception) {
        }
    }
}