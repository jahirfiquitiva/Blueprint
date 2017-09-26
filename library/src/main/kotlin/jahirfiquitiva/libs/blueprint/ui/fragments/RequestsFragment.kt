/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
import ca.allanwang.kau.utils.setPaddingBottom
import com.afollestad.materialdialogs.MaterialDialog
import com.andremion.counterfab.CounterFab
import com.pitchedapps.butler.iconrequest.App
import com.pitchedapps.butler.iconrequest.IconRequest
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.providers.viewmodels.RequestsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BottomNavigationBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.RequestsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.RequestLimitDialog
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.extensions.isLowRamDevice
import jahirfiquitiva.libs.frames.ui.fragments.base.BaseViewModelFragment
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.isInHorizontalMode
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration

@SuppressLint("MissingSuperCall")
class RequestsFragment:BaseViewModelFragment<App>() {
    
    lateinit var viewModel:RequestsViewModel
    
    lateinit var rv:EmptyViewRecyclerView
    lateinit var adapter:RequestsAdapter
    lateinit var fastScroll:RecyclerFastScroller
    
    private var spanCount = 0
    private var spacingDecoration:GridSpacingItemDecoration? = null
    private var dialog:RequestLimitDialog? = null
    private var otherDialog:MaterialDialog? = null
    
    override fun initUI(content:View) {
        rv = content.findViewById(R.id.list_rv)
        if (activity is BottomNavigationBlueprintActivity) {
            val bottomNavigationHeight = (activity as BottomNavigationBlueprintActivity).bottomBar.height
            rv.setPaddingBottom(64F.dpToPx.toInt() + bottomNavigationHeight)
        } else {
            rv.setPaddingBottom(64F.dpToPx.toInt())
        }
        rv.itemAnimator = if (context.isLowRamDevice) null else DefaultItemAnimator()
        rv.textView = content.findViewById(R.id.empty_text)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.setEmptyImage(R.drawable.empty_section)
        rv.setEmptyText(R.string.empty_section)
        rv.loadingView = content.findViewById(R.id.loading_view)
        rv.setLoadingText(R.string.loading_section)
        
        spanCount = if (context.isInHorizontalMode) 2 else 1
        rv.layoutManager = GridLayoutManager(context, spanCount)
        spacingDecoration = GridSpacingItemDecoration(spanCount,
                                                      context.dimenPixelSize(
                                                              R.dimen.cards_small_margin))
        rv.addItemDecoration(spacingDecoration)
        
        rv.addOnScrollListener(object:RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView:RecyclerView?, dx:Int, dy:Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) doToFab { it -> it.hide() }
                else doToFab { it -> it.show() }
            }
        })
        
        adapter = RequestsAdapter { updateFabCount() }
        rv.adapter = adapter
        fastScroll = content.findViewById(R.id.fast_scroller)
        fastScroll.attachRecyclerView(rv)
        rv.state = EmptyViewRecyclerView.State.LOADING
    }
    
    private fun updateFabCount() {
        val ir = IconRequest.get()
        ir?.let { doToFab { fab -> fab.count = it.selectedApps.size } }
    }
    
    private fun doToFab(what:(CounterFab) -> Unit) {
        if (activity is BaseBlueprintActivity) {
            what((activity as BaseBlueprintActivity).fab)
        }
    }
    
    fun scrollToTop() {
        rv.layoutManager.scrollToPosition(0)
    }
    
    private var hasSelectedAll = false
    fun toggleSelectAll() {
        val ir = IconRequest.get()
        ir?.let {
            if (hasSelectedAll) ir.unselectAllApps()
            else ir.selectAllApps()
            updateFabCount()
            adapter.notifyDataSetChanged()
            hasSelectedAll = !hasSelectedAll
        }
    }
    
    fun unselectAll() {
        val ir = IconRequest.get()
        ir?.let {
            ir.unselectAllApps()
            updateFabCount()
            adapter.notifyDataSetChanged()
            hasSelectedAll = false
        }
    }
    
    fun applyFilter(filter:String = "") {
        if (filter.hasContent()) {
            viewModel.getData()?.let {
                adapter.setItems(ArrayList(it.filter { it.name.contains(filter, true) }))
            }
        } else {
            viewModel.getData()?.let { adapter.setItems(ArrayList(it)) }
        }
    }
    
    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RequestsViewModel::class.java)
    }
    
    override fun registerObserver() {
        viewModel.observe(this, { adapter.setItems(ArrayList(it)) })
    }
    
    override fun loadDataFromViewModel() {
        viewModel.loadData(context, {
            otherDialog = activity.buildMaterialDialog {
                title(R.string.no_selected_apps_title)
                content(R.string.no_selected_apps_content)
                positiveText(android.R.string.ok)
            }
            otherDialog?.show()
        }, { reason, appsLeft, millis ->
                               try {
                                   dialog = RequestLimitDialog()
                                   if (reason == IconRequest.STATE_TIME_LIMITED && millis > 0) {
                                       dialog?.show(activity, millis)
                                   } else {
                                       dialog?.show(activity, appsLeft)
                                   }
                               } catch (e:Exception) {
                                   e.printStackTrace()
                               }
                           })
    }
    
    override fun unregisterObserver() {
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
        dialog?.dismiss(activity, RequestLimitDialog.TAG)
        dialog = null
    }
    
    override fun getContentLayout():Int = R.layout.section_layout
    override fun autoStartLoad():Boolean = true
}