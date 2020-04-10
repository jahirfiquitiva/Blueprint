package dev.jahir.blueprint.ui.viewholders

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import coil.api.load
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.preferences
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class IconViewHolder(itemView: View) : SectionedViewHolder(itemView) {

    private val iconView: AppCompatImageView? by itemView.findView(R.id.icon)

    fun bind(icon: Icon, animate: Boolean = true, onClick: ((Icon) -> Unit)? = null) {
        iconView?.load(icon.resId) {
            placeholder(context.drawable(context.string(R.string.icons_placeholder)))
            error(context.drawable(context.string(R.string.icons_placeholder)))
            crossfade(context.preferences.animationsEnabled)
            target { setIconDrawable(it, context.preferences.animationsEnabled && animate) }
        }
        onClick?.let {
            itemView.setOnClickListener { onClick.invoke(icon) }
        } ?: {
            iconView?.disableClick()
            itemView.disableClick()
        }()
    }

    private fun View.disableClick() {
        background = null
        isClickable = false
        isLongClickable = false
        isFocusable = false
        isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isContextClickable = false
        }
    }

    private fun setIconDrawable(drawable: Drawable?, animate: Boolean) {
        iconView?.apply {
            scaleX = 0F
            scaleY = 0F
            setImageDrawable(drawable)
            if (animate) {
                animate().scaleX(1F)
                    .scaleY(1F)
                    .setStartDelay(75)
                    .setDuration(200)
                    .start()
            } else {
                scaleX = 1F
                scaleY = 1F
            }
        }
    }

}