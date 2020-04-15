package dev.jahir.blueprint.ui.viewholders

import android.graphics.ColorFilter
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Launcher
import dev.jahir.blueprint.extensions.bnwFilter
import dev.jahir.blueprint.extensions.withMaxAlpha
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.views.context
import dev.jahir.frames.extensions.views.findView

class LauncherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val iconView: AppCompatImageView? by itemView.findView(R.id.icon)
    private val nameView: TextView? by itemView.findView(R.id.name)

    fun bind(
        pair: Pair<Launcher, Boolean>, colorFilter: ColorFilter = bnwFilter,
        onClick: ((launcher: Launcher, installed: Boolean) -> Unit)? = null
    ) {
        val (launcher, isInstalled) = pair
        nameView?.text = launcher.cleanAppName
        nameView?.setTextColor(
            context.resolveColor(
                if (isInstalled) android.R.attr.textColorPrimary
                else android.R.attr.textColorTertiary
            ).withMaxAlpha(if (isInstalled) 1.0F else 0.85F)
        )
        iconView?.load(launcher.icon) {
            target {
                iconView?.setImageDrawable(it)
                iconView?.colorFilter = if (!isInstalled) colorFilter else null
                iconView?.alpha = if (isInstalled) 1.0F else 0.8F
            }
        }
        itemView.setOnClickListener { onClick?.invoke(launcher, isInstalled) }
    }
}