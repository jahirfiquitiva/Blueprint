package dev.jahir.blueprint.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.createParcel
import dev.jahir.blueprint.ui.adapters.IconsAdapter
import dev.jahir.frames.extensions.context.dimenPixelSize
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.views.visible
import dev.jahir.frames.ui.decorations.GridSpacingItemDecoration

@Suppress("unused")
@SuppressLint("ClickableViewAccessibility")
class IconsPreviewRecyclerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attributeSet, defStyleAttr) {

    private val iconsAdapter: IconsAdapter by lazy { IconsAdapter() }
    private val icons: ArrayList<Icon> = ArrayList()

    internal var animateIcons: Boolean = true
        set(value) {
            field = value
            iconsAdapter.animate = value
            iconsAdapter.notifyDataSetChanged()
        }

    init {
        isSaveEnabled = true
        isNestedScrollingEnabled = false
        setHasFixedSize(true)
        itemAnimator = DefaultItemAnimator()
        layoutManager = LayoutManager(context, context.integer(R.integer.icons_columns_count))
        addItemDecoration(
            GridSpacingItemDecoration(
                context.integer(R.integer.icons_columns_count),
                context.dimenPixelSize(R.dimen.grids_spacing), false
            )
        )
        adapter = iconsAdapter
    }

    internal fun setOnIconClickListener(onClick: ((Icon, Drawable?) -> Unit)? = null) {
        iconsAdapter.onClick = onClick
        iconsAdapter.notifyDataSetChanged()
    }

    internal fun setIcons(newIcons: List<Icon>) {
        if (newIcons.isEmpty()) return
        val expectedIcons = context.integer(R.integer.icons_columns_count)
        val maxSize = if (newIcons.size <= expectedIcons) newIcons.size else expectedIcons
        icons.clear()
        icons.addAll(newIcons.subList(0, maxSize))
        iconsAdapter.icons = icons
        if (icons.isNotEmpty()) visible()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.icons = icons
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as? SavedState
        super.onRestoreInstanceState(savedState?.superState)
        setIcons(ArrayList(savedState?.icons.orEmpty()))
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
            @JvmField
            val CREATOR = createParcel { SavedState(it) }
        }
    }
}