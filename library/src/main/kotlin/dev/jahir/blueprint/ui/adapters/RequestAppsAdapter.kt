package dev.jahir.blueprint.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.ui.viewholders.RequestViewHolder
import dev.jahir.frames.extensions.views.inflate

class RequestAppsAdapter(private val onCheckChange: ((requestApp: RequestApp, checked: Boolean) -> Unit)? = null) :
    RecyclerView.Adapter<RequestViewHolder>() {

    var appsToRequest: ArrayList<RequestApp> = ArrayList()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    var selectedApps: ArrayList<RequestApp> = ArrayList()
        set(value) {
            // if (value.isEmpty() && field.isEmpty()) return
            // if (value.isNotEmpty() && value.size < appsToRequest.size) return
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    internal fun untoggle(app: RequestApp) {
        val index = try {
            appsToRequest.indexOf(app)
        } catch (e: Exception) {
            -1
        }
        if (index < 0) return
        notifyItemChanged(index)
    }

    internal fun changeAppState(app: RequestApp, selected: Boolean) {
        val index = try {
            appsToRequest.indexOf(app)
        } catch (e: Exception) {
            -1
        }
        if (index < 0) return
        if (selected && selectedApps.size >= appsToRequest.size) return
        if (selected) selectedApps.add(app) else selectedApps.remove(app)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder =
        RequestViewHolder(parent.inflate(R.layout.item_request))

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val app = appsToRequest[position]
        holder.bind(app, selectedApps.any { it == app }, onCheckChange)
    }

    override fun onViewRecycled(holder: RequestViewHolder) {
        super.onViewRecycled(holder)
        holder.bind(null, false)
    }

    override fun getItemCount(): Int = appsToRequest.size
}