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
        val adapter = (parent.adapter as? HomeAdapter)
        val showOverview = adapter?.showOverview == true
        val actualPosition = adapter?.getRelativePosition(absolutePosition)
        val relativePosition = actualPosition?.relativePos() ?: -1
        val section = actualPosition?.section() ?: 0

        if (section == HomeAdapter.OVERVIEW_SECTION && relativePosition >= 0 && showOverview) {
            val column = relativePosition % spanCount
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            val isInFirstRow = relativePosition < spanCount
            val rowCount = (adapter?.getItemCount(HomeAdapter.OVERVIEW_SECTION) ?: 0) / spanCount
            val currentRow = ((relativePosition + 1) / spanCount) - column
            val isInLastRow = currentRow >= (rowCount - 1)

            if (relativePosition < spanCount && !isInFirstRow) outRect.top = spacing
            if (!isInLastRow) outRect.bottom = spacing
        }
    }
}