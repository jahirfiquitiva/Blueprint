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
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setPaddingBottom
import com.afollestad.materialdialogs.MaterialDialog
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.extensions.lazyViewModel
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.blueprint.providers.viewmodels.RequestsViewModel
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.quest.events.RequestsCallback
import jahirfiquitiva.libs.blueprint.ui.activities.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.RequestsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.RequestLimitDialog
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.helpers.extensions.mdDialog
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

@Suppress("DEPRECATION")
@SuppressLint("MissingSuperCall")
class RequestsFragment : ViewModelFragment<App>(), RequestsCallback {
    
    companion object {
        fun create(debug: Boolean): RequestsFragment =
            RequestsFragment().apply { this.debug = debug }
    }
    
    private var debug = false
    
    private val viewModel: RequestsViewModel by lazyViewModel()
    
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
            content(R.string.loading_apps_to_request)
            progress(false, 100, true)
            positiveText(android.R.string.ok)
            onPositive { _, _ -> canShowProgress = false }
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
                    if (dy > 0) doToFab { it -> it.hide() }
                    else doToFab { it -> it.show() }
                }
            })
        
        recyclerView?.adapter = adapter
        fastScroller?.attachSwipeRefreshLayout(swipeToRefresh)
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
    }
    
    private fun updateFabCount() {
        postDelayed(10) {
            IconRequest.get()?.let { doToFab { fab -> fab.count = it.selectedApps.size } }
        }
    }
    
    private fun doToFab(what: (CounterFab) -> Unit) {
        (activity as? BaseBlueprintActivity)?.postToFab(what)
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    fun refresh() {
        val isRefreshing = swipeToRefresh?.isRefreshing ?: false
        if (isRefreshing) swipeToRefresh?.isRefreshing = false
        unselectAll()
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
            if (hasSelectedAll) ir.unselectAllApps()
            else ir.selectAllApps()
            updateFabCount()
            adapter?.notifyDataSetChanged()
            hasSelectedAll = !hasSelectedAll
        }
    }
    
    fun unselectAll() {
        val ir = IconRequest.get()
        ir?.let {
            ir.unselectAllApps()
            updateFabCount()
            adapter?.notifyDataSetChanged()
            hasSelectedAll = false
        }
    }
    
    fun applyFilter(filter: String = "", closed: Boolean = false) {
        if (filter.hasContent()) {
            recyclerView?.setEmptyImage(R.drawable.no_results)
            recyclerView?.setEmptyText(R.string.search_no_results)
            adapter?.setItems(
                ArrayList(viewModel.getData().orEmpty()).jfilter {
                    it.name.contains(filter, true)
                })
        } else {
            recyclerView?.setEmptyImage(R.drawable.empty_section)
            recyclerView?.setEmptyText(R.string.empty_section)
            viewModel.getData()?.let { adapter?.setItems(ArrayList(it)) }
        }
        if (!closed)
            scrollToTop()
    }
    
    override fun registerObservers() {
        viewModel.callback = this
        viewModel.observe(this) {
            swipeToRefresh?.isRefreshing = false
            adapter?.setItems(ArrayList(it))
            if (it.isEmpty()) {
                unselectAll()
                doToFab { it.hide() }
            } else {
                doToFab { it.show() }
            }
            progressDialog?.dismiss()
            updateFabCount()
            normalState()
        }
    }
    
    override fun loadDataFromViewModel() {
        doToFab { it.hide() }
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        internalLoadData(false)
    }
    
    private fun internalLoadData(force: Boolean) {
        activity {
            viewModel.loadData(
                it, debug, context?.getString(R.string.arctic_backend_host),
                context?.getString(R.string.arctic_backend_api_key), force)
        }
    }
    
    override fun onAppsLoaded(apps: ArrayList<App>) {
        super.onAppsLoaded(apps)
        viewModel.postResult(apps)
        normalState()
    }
    
    private fun normalState() {
        try {
            postDelayed(10) { recyclerView?.state = EmptyViewRecyclerView.State.NORMAL }
        } catch (e: Exception) {
        }
    }
    
    override fun onRequestLimited(context: Context, reason: Int, requestsLeft: Int, millis: Long) {
        super.onRequestLimited(context, reason, requestsLeft, millis)
        activity {
            try {
                dialog = RequestLimitDialog()
                if (reason == IconRequest.STATE_TIME_LIMITED && millis > 0) {
                    dialog?.show(it, millis)
                } else {
                    dialog?.show(it, requestsLeft)
                }
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
                    progressDialog?.setProgress(progress)
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
                content(R.string.no_selected_apps_content)
                positiveText(android.R.string.ok)
            }
            otherDialog?.show()
        }
    }
    
    override fun unregisterObservers() {
        viewModel.destroy(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        destroyDialog()
        doToFab { it -> it.count = 0 }
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
    override fun allowReloadAfterVisibleToUser(): Boolean = viewModel.getData().orEmpty().isEmpty()
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        canShowProgress = isVisibleToUser
        super.setUserVisibleHint(isVisibleToUser)
    }
}