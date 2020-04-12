package dev.jahir.blueprint.ui.adapters

import android.content.Context
import android.graphics.ColorFilter
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Launcher
import dev.jahir.blueprint.extensions.bnwFilter
import dev.jahir.blueprint.ui.viewholders.LauncherViewHolder
import dev.jahir.frames.extensions.views.inflate

class LaunchersAdapter(
    context: Context? = null,
    private val onClick: ((launcher: Launcher, installed: Boolean) -> Unit)? = null
) : RecyclerView.Adapter<LauncherViewHolder>() {

    private val colorFilter: ColorFilter = bnwFilter

    var launchers: ArrayList<Pair<Launcher, Boolean>> = Launcher.getSupportedLaunchers(context)
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = launchers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LauncherViewHolder =
        LauncherViewHolder(parent.inflate(R.layout.item_launcher))

    override fun onBindViewHolder(holder: LauncherViewHolder, position: Int) {
        holder.bind(launchers[position], colorFilter, onClick)
    }
}