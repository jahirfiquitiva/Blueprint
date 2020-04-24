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
import dev.jahir.frames.ui.activities.base.BaseSystemUIVisibilityActivity
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment

class RequestFragment : BaseFramesFragment<RequestApp>() {

    private val requestAppsAdapter: RequestAppsAdapter by lazy { RequestAppsAdapter(::onCheckChange) }

    private val fabHeight: Int
        get() = (activity as? BlueprintActivity)?.fabBtn?.measuredHeight ?: 0

    private val extraHeight: Int
        get() = if (fabHeight > 0) 16.dpToPx else 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = requestAppsAdapter
        recyclerView?.setHasFixedSize(true)
        loadData()
    }

    override fun setupContentBottomOffset(view: View?) {
        (view ?: getView())?.let { v ->
            v.post {
                val bottomNavigationHeight =
                    (context as? BaseSystemUIVisibilityActivity<*>)?.bottomNavigation?.measuredHeight
                        ?: 0
                v.setPaddingBottom(bottomNavigationHeight)
                recyclerView?.setupBottomOffset(fabHeight + extraHeight)
            }
        }
    }

    override fun updateItemsInAdapter(items: ArrayList<RequestApp>) {
        requestAppsAdapter.appsToRequest = items
    }

    internal fun updateSelectedApps(selectedApps: ArrayList<RequestApp>?) {
        requestAppsAdapter.selectedApps = ArrayList(selectedApps.orEmpty())
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
        if ((activity as? BlueprintActivity)?.changeRequestAppState(app, checked) == true)
            requestAppsAdapter.changeAppState(app, checked)
        else requestAppsAdapter.untoggle(app)
    }

    companion object {
        const val TAG = "requests_fragment"
    }
}