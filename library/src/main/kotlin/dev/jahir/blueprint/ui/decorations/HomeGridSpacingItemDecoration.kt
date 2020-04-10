package dev.jahir.blueprint.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.ui.adapters.HomeAdapter
import dev.jahir.frames.ui.decorations.GridSpacingItemDecoration

open class HomeGridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : GridSpacingItemDecoration(spanCount, spacing, true) {

    override fun internalOffsetsSetup(outRect: Rect, view: View, parent: RecyclerView) {
        val absolutePosition = parent.getChildAdapterPosition(view)
        val actualPosition =
            (parent.adapter as? HomeAdapter)?.getRelativePosition(absolutePosition)
        val relativePosition = actualPosition?.relativePos() ?: -1
        val section = actualPosition?.section() ?: 0
        if (section == 1 && relativePosition >= 0) {
            val column = relativePosition % spanCount
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (relativePosition < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        }
    }
}