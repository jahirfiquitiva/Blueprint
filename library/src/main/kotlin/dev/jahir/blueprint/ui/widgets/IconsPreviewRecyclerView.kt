package dev.jahir.blueprint.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.blueprint.ui.adapters.IconsPreviewAdapter
import dev.jahir.frames.extensions.context.dimenPixelSize
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.views.visible
import dev.jahir.frames.ui.decorations.GridSpacingItemDecoration

@SuppressLint("ClickableViewAccessibility")
class IconsPreviewRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    private val decoration: GridSpacingItemDecoration by lazy {
        GridSpacingItemDecoration(
            context.integer(R.integer.icons_columns_count),
            context.dimenPixelSize(R.dimen.grids_spacing)
        )
    }

    private val iconsAdapter: IconsPreviewAdapter by lazy { IconsPreviewAdapter() }

    private var currentIcons: ArrayList<Icon> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            removeItemDecoration(decoration)
            iconsAdapter.icons = field
            addItemDecoration(decoration)
            if (field.isNotEmpty()) visible()
        }

    init {
        isSaveEnabled = true
        isNestedScrollingEnabled = false
        layoutManager = LayoutManager(context, context.integer(R.integer.icons_columns_count))
        adapter = iconsAdapter
    }

    fun resetIcons(force: Boolean = false) {
        if (currentIcons.isNullOrEmpty() || force)
            currentIcons = buildIconsList()
    }

    private fun buildIconsList(): ArrayList<Icon> {
        val nextIcons = ArrayList<Icon>()
        context.stringArray(R.array.icons_preview).filter { it.hasContent() }.forEach {
            nextIcons.add(Icon(it, context.drawableRes(it)))
        }
        if (nextIcons.isNotEmpty()) {
            val expectedIcons = context.integer(R.integer.icons_columns_count)
            val shuffledIcons = nextIcons.distinctBy { it.name }.shuffled()
            val maxSize =
                if (shuffledIcons.size <= expectedIcons) shuffledIcons.size
                else expectedIcons
            nextIcons.clear()
            nextIcons.addAll(shuffledIcons.subList(0, maxSize))
        }
        return nextIcons
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.icons = currentIcons
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as? SavedState
        super.onRestoreInstanceState(savedState?.superState)
        this.currentIcons = ArrayList(savedState?.icons.orEmpty())
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean = false
    override fun performClick(): Boolean = false

    private class LayoutManager(context: Context, span: Int) :
        GridLayoutManager(context, span, RecyclerView.VERTICAL, false) {
        override fun canScrollHorizontally(): Boolean = false
        override fun canScrollVertically(): Boolean = false

        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean
        ): Boolean = false

        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean,
            focusedChildVisible: Boolean
        ): Boolean = false
    }

    @Suppress("unused")
    private class SavedState : BaseSavedState {
        var icons: ArrayList<Icon> = ArrayList()

        internal constructor(superState: Parcelable?) : super(superState)
        private constructor(parcel: Parcel?) : super(parcel) {
            icons = ArrayList(parcel?.createTypedArrayList(Icon.CREATOR).orEmpty())
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeTypedList(icons)
        }

        companion object {
            private val CREATOR = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(parcel: Parcel?): SavedState? = SavedState(parcel)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}