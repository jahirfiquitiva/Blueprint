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
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.utils.dimenPixelSize
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setPaddingBottom
import com.afollestad.materialdialogs.MaterialDialog
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.providers.viewmodels.RequestsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.RequestsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.RequestLimitDialog
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.ctxt
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.isInHorizontalMode
import jahirfiquitiva.libs.kauextensions.extensions.isLowRamDevice
import jahirfiquitiva.libs.kauextensions.extensions.runOnUiThread
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.quest.App
import jahirfiquitiva.libs.quest.IconRequest

@Suppress("DEPRECATION")
@SuppressLint("MissingSuperCall")
class RequestsFragment : ViewModelFragment<App>() {
    
    private var viewModel: RequestsViewModel? = null
    
    private var recyclerView: EmptyViewRecyclerView? = null
    private var fastScroller: RecyclerFastScroller? = null
    private val adapter: RequestsAdapter? by lazy {
        RequestsAdapter(Glide.with(this)) { updateFabCount() }
    }
    
    private var spanCount = 0
    private var spacingDecoration: GridSpacingItemDecoration? = null
    private var dialog: RequestLimitDialog? = null
    private var otherDialog: MaterialDialog? = null
    
    private var actuallyVisible = false
    
    private val progressDialog: MaterialDialog? by lazy {
        activity?.buildMaterialDialog {
            content(R.string.loading_apps_to_request)
            progress(false, 100, true)
            positiveText(android.R.string.ok)
            onPositive { _, _ -> canShowProgress = false }
        }
    }
    
    private var canShowProgress = true
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        recyclerView?.setPaddingBottom(64.dpToPx * (if (hasBottomNav) 2 else 1))
        
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
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy > 0) doToFab { it -> it.hide() }
                        else doToFab { it -> it.show() }
                    }
                })
        
        recyclerView?.adapter = adapter
        fastScroller = content.findViewById(R.id.fast_scroller)
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
        updateFabCount()
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        postDelayed(10) { refresh() }
    }
    
    override fun onResume() {
        super.onResume()
        postDelayed(50) { loadDataFromViewModel() }
    }
    
    private fun updateFabCount() {
        IconRequest.get()?.let { doToFab { fab -> fab.count = it.selectedApps.size } }
    }
    
    private fun doToFab(what: (CounterFab) -> Unit) {
        (activity as? BaseBlueprintActivity)?.postToFab(what)
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    fun refresh() {
        unselectAll()
        doToFab { it.hide() }
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        canShowProgress = true
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
    
    fun applyFilter(filter: String = "") {
        if (filter.hasContent()) {
            recyclerView?.setEmptyImage(R.drawable.no_results)
            recyclerView?.setEmptyText(R.string.search_no_results)
            viewModel?.getData()?.let {
                adapter?.setItems(ArrayList(it.filter { it.name.contains(filter, true) }))
            }
        } else {
            recyclerView?.setEmptyImage(R.drawable.empty_section)
            recyclerView?.setEmptyText(R.string.empty_section)
            viewModel?.getData()?.let { adapter?.setItems(ArrayList(it)) }
        }
        scrollToTop()
    }
    
    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RequestsViewModel::class.java)
    }
    
    override fun registerObserver() {
        viewModel?.observe(this) {
            adapter?.setItems(ArrayList(it))
            if (actuallyVisible) {
                if (it.isEmpty()) {
                    unselectAll()
                    doToFab { it.hide() }
                } else {
                    doToFab { it.show() }
                }
                progressDialog?.dismiss()
            }
        }
    }
    
    override fun loadDataFromViewModel() {
        if (actuallyVisible) doToFab { it.hide() }
        canShowProgress = true
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        internalLoadData(false)
    }
    
    private fun internalLoadData(force: Boolean) {
        actv { actv ->
            viewModel?.loadData(
                    actv, {
                otherDialog = actv.buildMaterialDialog {
                    title(R.string.no_selected_apps_title)
                    content(R.string.no_selected_apps_content)
                    positiveText(android.R.string.ok)
                }
                if (actuallyVisible) otherDialog?.show()
            }, { reason, appsLeft, millis ->
                        try {
                            dialog = RequestLimitDialog()
                            if (reason == IconRequest.STATE_TIME_LIMITED && millis > 0) {
                                if (actuallyVisible) dialog?.show(actv, millis)
                            } else {
                                if (actuallyVisible) dialog?.show(actv, appsLeft)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, context?.getString(R.string.arctic_backend_host),
                    context?.getString(R.string.arctic_backend_api_key),
                    force) { progress ->
                if (canShowProgress) {
                    actv {
                        runOnUiThread {
                            progressDialog?.setProgress(progress)
                            progressDialog?.setOnDismissListener { canShowProgress = false }
                            if (progress >= 100) {
                                progressDialog?.dismiss()
                            } else {
                                if (actuallyVisible) progressDialog?.show()
                            }
                        }
                    }
                }
            }
        }
    }
    
    override fun unregisterObserver() {
        viewModel?.destroy(this)
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
        actv { dialog?.dismiss(it, RequestLimitDialog.TAG) }
        dialog = null
    }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    override fun autoStartLoad(): Boolean = true
    override fun allowReloadAfterVisibleToUser(): Boolean = true
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) canShowProgress = true
        super.setUserVisibleHint(isVisibleToUser)
        actuallyVisible = isVisibleToUser
        if (isVisibleToUser) {
            updateFabCount()
        } else {
            doToFab { it.hide() }
        }
    }
}