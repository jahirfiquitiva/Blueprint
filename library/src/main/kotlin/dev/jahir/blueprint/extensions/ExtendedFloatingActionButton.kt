package dev.jahir.blueprint.extensions

import androidx.annotation.DrawableRes
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.resources.tint

internal fun ExtendedFloatingActionButton.setup(
    text: String,
    @DrawableRes icon: Int,
    show: Boolean = true,
    shouldShrink: Boolean = false
) {
    this.text = text
    this.icon = context.drawable(icon)
        ?.tint(context.resolveColor(R.attr.colorOnSecondary, context.color(R.color.onAccent)))
    invalidate()
    if (show) show() else hide()
}