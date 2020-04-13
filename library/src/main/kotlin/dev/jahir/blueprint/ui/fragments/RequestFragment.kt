package dev.jahir.blueprint.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.ui.activities.BlueprintActivity
import dev.jahir.blueprint.ui.adapters.RequestAppsAdapter
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.views.setPaddingBottom
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment

class RequestFragment : BaseFramesFragment<RequestApp>() {

    private val requestAppsAdapter: RequestAppsAdapter by lazy { RequestAppsAdapter(::onCheckChange) }

    private val fabHeight: Int
        get() = (activity as? BlueprintActivity)?.fabBtn?.measuredHeight ?: 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = requestAppsAdapter
        recyclerView?.setHasFixedSize(true)
        // TODO: Setup margin properly
        (activity as? BlueprintActivity)?.bottomNavigation?.let {
            it.post { recyclerView?.setPaddingBottom(it.measuredHeight + fabHeight + 16.dpToPx) }
        }
        loadData()
    }

    override fun updateItemsInAdapter(items: ArrayList<RequestApp>) {
        requestAppsAdapter.appsToRequest = items
    }

    internal fun updateSelectedApps(selectedApps: ArrayList<RequestApp>) {
        requestAppsAdapter.selectedApps = selectedApps
    }

    override fun loadData() {
        (activity as? BlueprintActivity)?.loadAppsToRequest()
    }

    override fun getFilteredItems(
        originalItems: ArrayList<RequestApp>,
        filter: String
    ): ArrayList<RequestApp> =
        ArrayList(originalItems.filter { it.name.lower().contains(filter.lower()) })

    private fun onCheckChange(app: RequestApp, checked: Boolean) {
        (activity as? BlueprintActivity)?.changeRequestAppState(app, checked)
        requestAppsAdapter.changeAppState(app, checked)
    }

    companion object {
        internal const val TAG = "requests_fragment"
    }
}