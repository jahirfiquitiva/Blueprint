package dev.jahir.blueprint.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.jahir.blueprint.BuildConfig
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.RequestApp
import dev.jahir.blueprint.data.viewmodels.RequestsViewModel
import dev.jahir.blueprint.extensions.animateVisibility
import dev.jahir.blueprint.ui.activities.BlueprintActivity
import dev.jahir.blueprint.ui.adapters.RequestAppsAdapter
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.context.toast
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lighten
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.resources.tint
import dev.jahir.frames.extensions.utils.lazyViewModel
import dev.jahir.frames.extensions.utils.postDelayed
import dev.jahir.frames.extensions.views.attachSwipeRefreshLayout
import dev.jahir.frames.extensions.views.setPaddingBottom
import dev.jahir.frames.ui.widgets.StatefulRecyclerView

class RequestFragment : Fragment(R.layout.fragment_request),
    StatefulRecyclerView.StateDrawableModifier {

    private val originalItems: ArrayList<RequestApp> = ArrayList()
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: StatefulRecyclerView? = null
    private var sendRequestBtn: AppCompatButton? = null

    private var requestBtnShown: Boolean = false

    private val requestAppsAdapter: RequestAppsAdapter by lazy { RequestAppsAdapter(::onCheckChange) }
    private val requestsViewModel: RequestsViewModel by lazyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestsViewModel.observeAppsToRequest(this) { updateItems(it) }
        requestsViewModel.observeSelectedApps(this) { updateSelectedApps(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestsViewModel.destroy(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(dev.jahir.frames.R.id.recycler_view)
        recyclerView?.stateDrawableModifier = this

        recyclerView?.emptyText = R.string.nothing_found
        recyclerView?.emptyDrawable = R.drawable.ic_empty_section

        recyclerView?.noSearchResultsText = R.string.no_results_found
        recyclerView?.noSearchResultsDrawable = R.drawable.ic_empty_results

        recyclerView?.loadingText = R.string.loading

        recyclerView?.itemAnimator = DefaultItemAnimator()
        swipeRefreshLayout = view.findViewById(dev.jahir.frames.R.id.swipe_to_refresh)
        swipeRefreshLayout?.setOnRefreshListener { startRefreshing() }
        swipeRefreshLayout?.setColorSchemeColors(
            context?.resolveColor(dev.jahir.frames.R.attr.colorSecondary, 0) ?: 0
        )
        swipeRefreshLayout?.setProgressBackgroundColorSchemeColor(
            (context?.resolveColor(dev.jahir.frames.R.attr.colorSurface, 0) ?: 0).lighten(.1F)
        )
        recyclerView?.attachSwipeRefreshLayout(swipeRefreshLayout)

        sendRequestBtn = view.findViewById(R.id.send_request_btn)
        sendRequestBtn?.setOnClickListener {
            // TODO: Send request!
            context?.toast("Send request!")
        }

        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = requestAppsAdapter

        (activity as? BlueprintActivity)?.bottomNavigation?.let {
            it.post {
                view.setPaddingBottom(it.measuredHeight)
                recyclerView?.setPaddingBottom(
                    it.measuredHeight
                            + (if (requestBtnShown) sendRequestBtn?.measuredHeight ?: 0 else 0)
                            + 4.dpToPx
                )
            }
        }
        loadData()
    }

    internal fun setRefreshEnabled(enabled: Boolean) {
        swipeRefreshLayout?.isEnabled = enabled
    }

    internal fun applyFilter(filter: String, closed: Boolean) {
        recyclerView?.searching = filter.hasContent() && !closed
        requestAppsAdapter.appsToRequest =
            if (filter.hasContent() && !closed)
                getFilteredItems(ArrayList(originalItems), filter)
            else originalItems
        if (!closed) scrollToTop()
    }

    private fun startRefreshing() {
        swipeRefreshLayout?.isRefreshing = true
        recyclerView?.loading = true
        try {
            loadData()
            postDelayed(500) { stopRefreshing() }
        } catch (e: Exception) {
            stopRefreshing()
        }
    }

    private fun stopRefreshing() {
        Handler().post {
            swipeRefreshLayout?.isRefreshing = false
            recyclerView?.loading = false
        }
    }

    private fun scrollToTop() {
        recyclerView?.post { recyclerView?.smoothScrollToPosition(0) }
    }

    private fun updateItems(newItems: ArrayList<RequestApp>, stillLoading: Boolean = false) {
        this.originalItems.clear()
        this.originalItems.addAll(newItems)
        requestAppsAdapter.appsToRequest = newItems
        if (!stillLoading) stopRefreshing()
    }

    private fun updateSelectedApps(selectedApps: ArrayList<RequestApp>) {
        if (selectedApps.isNotEmpty())
            sendRequestBtn?.text = context?.string(R.string.send_request_x, selectedApps.size)
        sendRequestBtn?.animateVisibility(selectedApps.isNotEmpty())
        if (selectedApps.size >= requestsViewModel.appsToRequest.size || selectedApps.isEmpty())
            requestAppsAdapter.selectedApps = selectedApps
    }

    override fun modifyDrawable(drawable: Drawable?): Drawable? =
        try {
            drawable?.tint(context?.resolveColor(dev.jahir.frames.R.attr.colorOnSurface, 0) ?: 0)
        } catch (e: Exception) {
            drawable
        }


    private fun loadData() {
        requestsViewModel.loadApps(context, BuildConfig.DEBUG)
    }

    private fun getFilteredItems(
        originalItems: ArrayList<RequestApp>,
        filter: String
    ): ArrayList<RequestApp> =
        ArrayList(originalItems.filter { it.name.lower().contains(filter.lower()) })

    private fun onCheckChange(app: RequestApp, checked: Boolean) {
        if (checked) requestsViewModel.selectApp(app)
        else requestsViewModel.deselectApp(app)
        requestAppsAdapter.changeAppState(app, checked)
    }

    internal fun toggleSelectAll() {
        requestsViewModel.toggleSelectAll()
    }

    companion object {
        internal const val TAG = "requests_fragment"
    }
}