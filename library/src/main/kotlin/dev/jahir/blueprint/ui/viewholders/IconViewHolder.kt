package dev.jahir.blueprint.ui.viewholders

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.setPadding
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.asAdaptive
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.preferences
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class IconViewHolder(itemView: View) : SectionedViewHolder(itemView) {

    private val iconView: AppCompatImageView? by itemView.findView(R.id.icon)

    fun bind(icon: Icon, animate: Boolean = true, onClick: ((Icon, Drawable?) -> Unit)? = null) {
        setIconDrawable(icon, context.preferences.animationsEnabled && animate, onClick)
    }

    private fun View.disableClick() {
        background = null
        isClickable = false
        isLongClickable = false
        isFocusable = false
        isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) isContextClickable = false
    }

    fun unbind() {
        iconView?.setImageDrawable(null)
    }

    private fun setIconDrawable(
        icon: Icon,
        animate: Boolean,
        onClick: ((Icon, Drawable?) -> Unit)? = null
    ) {
        val (iconDrawable, isAdaptive) =
            context.drawable(icon.resId)?.asAdaptive(context) ?: Pair(null, false)
        iconView?.apply {
            if (animate) {
                scaleX = 0F
                scaleY = 0F
                alpha = 0F
            }
            setImageDrawable(iconDrawable)
            if (animate) {
                animate().scaleX(1F)
                    .scaleY(1F)
                    .alpha(1F)
                    .setStartDelay(ICON_ANIMATION_DELAY)
                    .setDuration(ICON_ANIMATION_DURATION)
                    .start()
            }
        }
        iconView?.setPadding(if (isAdaptive) 10.dpToPx else 6.dpToPx)
        if (onClick == null) {
            iconView?.disableClick()
            itemView.disableClick()
        } else itemView.setOnClickListener { onClick.invoke(icon, iconDrawable) }
    }

    companion object {
        internal const val ICON_ANIMATION_DELAY: Long = 50L
        internal const val ICON_ANIMATION_DURATION: Long = 250L
    }
}