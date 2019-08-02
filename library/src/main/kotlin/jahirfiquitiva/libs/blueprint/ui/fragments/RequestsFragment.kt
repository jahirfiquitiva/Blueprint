/*
 * Copyright (c) 2018. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.blueprint.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setPaddingBottom
import com.afollestad.materialdialogs.MaterialDialog
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.extensions.getViewModel
import jahirfiquitiva.libs.archhelpers.extensions.mdDialog
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.showIf
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.providers.viewmodels.RequestsViewModel
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.ui.activities.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.RequestsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.RequestLimitDialog
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.activity
import jahirfiquitiva.libs.kext.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kext.extensions.ctxt
import jahirfiquitiva.libs.kext.extensions.dimenPixelSize
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.isInHorizontalMode
import jahirfiquitiva.libs.kext.extensions.isLowRamDevice
import jahirfiquitiva.libs.kext.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kext.ui.fragments.ViewModelFragment
import kotlin.math.max

@Suppress("DEPRECATION")
@SuppressLint("MissingSuperCall")
class RequestsFragment : ViewModelFragment<App>(), RequestsCallback {
    
    companion object {
        private const val FAB_DELAY = 200L
        fun create(debug: Boolean): RequestsFragment =
            RequestsFragment().apply { this.debug = debug }
    }
    
    private var debug = false
    
    private var viewModel: RequestsViewModel? = null
    private var recyclerView: EmptyViewRecyclerView? = null
    private var fastScroller: RecyclerFastScroller? = null
    private var swipeToRefresh: SwipeRefreshLayout? = null
    
    private val adapter: RequestsAdapter? by lazy {
        RequestsAdapter(context?.let { Glide.with(it) }) { updateFabCount() }
    }
    
    private var spanCount = 0
    private var spacingDecoration: GridSpacingItemDecoration =
        GridSpacingItemDecoration(spanCount, 0)
    private var dialog: RequestLimitDialog? = null
    private var otherDialog: MaterialDialog? = null
    private var canShowProgress = true
    
    private val progressDialog: MaterialDialog? by lazy {
        activity?.mdDialog {
            message(R.string.loading_apps_to_request)
            positiveButton(android.R.string.ok) { canShowProgress = false }
        }
    }
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        fastScroller = content.findViewById(R.id.fast_scroller)
        swipeToRefresh = content.findViewById(R.id.swipe_to_refresh)
        
        swipeToRefresh?.let {
            it.setProgressBackgroundColorSchemeColor(it.context.cardBackgroundColor)
            it.setColorSchemeColors(it.context.accentColor)
            it.setOnRefreshListener { refresh() }
        }
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        recyclerView?.setPaddingBottom(64.dpToPx * (if (hasBottomNav) 2 else 1))
        if (hasBottomNav) fastScroller?.setPaddingBottom(48.dpToPx)
        
        recyclerView?.itemAnimator =
            if (context?.isLowRamDevice == true) null else DefaultItemAnimator()
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        spanCount = if (context?.isInHorizontalMode == true) 2 else 1
        recyclerView?.layoutManager = GridLayoutManager(context, spanCount)
        spacingDecoration = GridSpacingItemDecoration(
            spanCount, ctxt.dimenPixelSize(R.dimen.cards_small_margin))
        recyclerView?.addItemDecoration(spacingDecoration)
        
        recyclerView?.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) doToFab { it.hide() }
                    else doToFab { it.show() }
                }
            })
        
        recyclerView?.adapter = adapter
        swipeToRefresh?.let { fastScroller?.attachSwipeRefreshLayout(it) }
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
        
        postDelayed(FAB_DELAY) { doToFab { it.showIf(viewModel?.getData()?.size ?: 0 > 0) } }
    }
    
    override fun onResume() {
        super.onResume()
        postDelayed(FAB_DELAY) { doToFab { it.showIf(viewModel?.getData()?.size ?: 0 > 0) } }
    }
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        canShowProgress = isVisibleToUser
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            postDelayed(FAB_DELAY) { doToFab { it.showIf(viewModel?.getData()?.size ?: 0 > 0) } }
        }
    }
    
    private fun updateFabCount() {
        postDelayed(10) {
            IconRequest.get()?.let { doToFab { fab -> fab.count = it.selectedApps.size } }
        }
    }
    
    internal fun getDataCount(): Int = max(viewModel?.getData()?.size ?: 0, adapter?.itemCount ?: 0)
    
    private fun doToFab(what: (CounterFab) -> Unit) {
        (activity as? BaseBlueprintActivity)?.postToFab(what)
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    fun refresh() {
        val isRefreshing = swipeToRefresh?.isRefreshing ?: false
        if (isRefreshing) swipeToRefresh?.isRefreshing = false
        deselectAll()
        doToFab { it.hide() }
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        canShowProgress = true
        swipeToRefresh?.isRefreshing = true
        internalLoadData(true)
    }
    
    private var hasSelectedAll = false
    fun toggleSelectAll() {
        val ir = IconRequest.get()
        ir?.let {
            val listModified = if (hasSelectedAll) ir.deselectAllApps() else ir.selectAllApps()
            if (listModified) {
                updateFabCount()
                adapter?.notifyDataSetChanged()
                hasSelectedAll = !hasSelectedAll
            }
        }
    }
    
    fun deselectAll() {
        val ir = IconRequest.get()
        ir?.let {
            if (ir.deselectAllApps()) {
                updateFabCount()
                adapter?.notifyDataSetChanged()
                hasSelectedAll = false
            }
        }
    }
    
    fun applyFilter(filter: String = "", closed: Boolean = false) {
        if (filter.hasContent()) {
            recyclerView?.setEmptyImage(R.drawable.no_results)
            recyclerView?.setEmptyText(R.string.search_no_results)
            adapter?.setItems(
                ArrayList(viewModel?.getData().orEmpty()).jfilter {
                    it.name.contains(filter, true)
                })
        } else {
            recyclerView?.setEmptyImage(R.drawable.empty_section)
            recyclerView?.setEmptyText(R.string.empty_section)
            viewModel?.getData()?.let { adapter?.setItems(ArrayList(it)) }
        }
        doToFab { it.showIf(viewModel?.getData()?.size ?: 0 > 0) }
        if (!closed) scrollToTop()
    }
    
    override fun initViewModels() {
        viewModel = getViewModel()
    }
    
    override fun registerObservers() {
        viewModel?.callback = this
        viewModel?.observe(this) { items ->
            swipeToRefresh?.isRefreshing = false
            adapter?.setItems(ArrayList(items))
            progressDialog?.dismiss()
            updateFabCount()
            if (items.isEmpty()) {
                deselectAll()
                doToFab { it.hide() }
                emptyState()
            } else {
                doToFab { it.show() }
                normalState()
            }
        }
    }
    
    override fun loadDataFromViewModel() {
        doToFab { it.hide() }
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        internalLoadData(false)
    }
    
    private fun internalLoadData(force: Boolean) {
        activity {
            viewModel?.loadData(
                it, debug, context?.getString(R.string.arctic_backend_host),
                context?.getString(R.string.arctic_backend_api_key), force)
        }
    }
    
    override fun onAppsLoaded(apps: ArrayList<App>) {
        super.onAppsLoaded(apps)
        viewModel?.postResult(apps)
    }
    
    private fun emptyState() {
        try {
            postDelayed(10) { recyclerView?.state = EmptyViewRecyclerView.State.EMPTY }
        } catch (e: Exception) {
        }
    }
    
    private fun normalState() {
        try {
            postDelayed(10) { recyclerView?.state = EmptyViewRecyclerView.State.NORMAL }
        } catch (e: Exception) {
        }
    }
    
    override fun onRequestLimited(
        context: Context,
        reason: Int,
        requestsLeft: Int,
        timeLeft: Long,
        toSend: Boolean
                                 ) {
        super.onRequestLimited(context, reason, requestsLeft, timeLeft, toSend)
        activity {
            try {
                destroyDialog()
                dialog = if (reason == IconRequest.STATE_TIME_LIMITED && timeLeft > 0) {
                    RequestLimitDialog.invoke(true, timeLeft, 0)
                } else if (reason == IconRequest.STATE_COUNT_LIMITED) {
                    RequestLimitDialog.invoke(false, 0, requestsLeft)
                } else null
                dialog?.show(it)
            } catch (e: Exception) {
                BL.e("Error", e)
            }
        }
    }
    
    override fun onRequestProgress(progress: Int) {
        super.onRequestProgress(progress)
        if ((isVisible || userVisibleHint) && canShowProgress) {
            activity {
                it.runOnUiThread {
                    progressDialog?.setOnDismissListener { canShowProgress = false }
                    if (progress >= 100) {
                        progressDialog?.dismiss()
                        swipeToRefresh?.isRefreshing = false
                    } else {
                        progressDialog?.show()
                    }
                }
            }
        }
    }
    
    override fun onRequestEmpty(context: Context) {
        super.onRequestEmpty(context)
        activity {
            otherDialog = it.mdDialog {
                title(R.string.no_selected_apps_title)
                message(R.string.no_selected_apps_content)
                positiveButton(android.R.string.ok)
            }
            otherDialog?.show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        destroyDialog()
        doToFab { it.count = 0 }
    }
    
    private fun destroyDialog() {
        otherDialog?.dismiss()
        otherDialog = null
        progressDialog?.dismiss()
        activity { dialog?.dismiss(it, RequestLimitDialog.TAG) }
        dialog = null
    }
    
    override fun getContentLayout(): Int = R.layout.section_with_swipe_refresh
    override fun autoStartLoad(): Boolean = true
    override fun allowReloadAfterVisibleToUser(): Boolean = false
}
