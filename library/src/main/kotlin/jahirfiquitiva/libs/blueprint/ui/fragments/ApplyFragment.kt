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

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.isAppInstalled
import ca.allanwang.kau.utils.setPaddingBottom
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Launcher
import jahirfiquitiva.libs.blueprint.helpers.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.helpers.extensions.showLauncherNotInstalledDialog
import jahirfiquitiva.libs.blueprint.providers.viewmodels.LaunchersViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.LaunchersAdapter
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.ctxt
import jahirfiquitiva.libs.kauextensions.extensions.dimenPixelSize
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration

@Suppress("DEPRECATION")
class ApplyFragment : ViewModelFragment<Launcher>() {
    
    private var recyclerView: EmptyViewRecyclerView? = null
    private var adapter: LaunchersAdapter? = null
    private var launchersViewModel: LaunchersViewModel? = null
    
    override fun initViewModel() {
        launchersViewModel = ViewModelProviders.of(this).get(LaunchersViewModel::class.java)
    }
    
    override fun registerObserver() {
        launchersViewModel?.observe(this) { setAdapterItems(it) }
    }
    
    override fun loadDataFromViewModel() {
        ctxt { launchersViewModel?.loadData(it) }
    }
    
    override fun unregisterObserver() {
        launchersViewModel?.destroy(this)
    }
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        val fastScroller: RecyclerFastScroller? by content.bind(R.id.fast_scroller)
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        if (hasBottomNav) recyclerView?.setPaddingBottom(64.dpToPx)
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        ctxt {
            adapter = LaunchersAdapter(Glide.with(it)) { onItemClicked(it, false) }
        }
        
        recyclerView?.adapter = adapter
        val columns = ctxt.getInteger(R.integer.icons_columns) - 1
        recyclerView?.layoutManager =
                GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false)
        recyclerView?.addItemDecoration(
                GridSpacingItemDecoration(columns, dimenPixelSize(R.dimen.cards_margin)))
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
    }
    
    fun applyFilter(filter: String = "") {
        if (filter.hasContent()) {
            recyclerView?.setEmptyImage(R.drawable.no_results)
            recyclerView?.setEmptyText(R.string.search_no_results)
            setAdapterItems(
                    ArrayList(launchersViewModel?.getData().orEmpty().filter {
                        it.name.contains(filter, true)
                    }))
        } else {
            recyclerView?.setEmptyImage(R.drawable.empty_section)
            recyclerView?.setEmptyText(R.string.empty_section)
            setAdapterItems(ArrayList(launchersViewModel?.getData().orEmpty()))
        }
        scrollToTop()
    }
    
    private fun setAdapterItems(items: ArrayList<Launcher>) {
        adapter?.setItems(
                ArrayList(items.distinct().sortedBy { !isLauncherInstalled(it.packageNames) }))
    }
    
    private fun isLauncherInstalled(packages: Array<String>): Boolean {
        packages.forEach {
            if (context?.isAppInstalled(it) == true) return true
        }
        return false
    }
    
    override fun onItemClicked(item: Launcher, longClick: Boolean) {
        if (!longClick) {
            if (isLauncherInstalled(item.packageNames) || item.name.contains("lineage", true))
                context?.executeLauncherIntent(item.name)
            else context?.showLauncherNotInstalledDialog(item)
        }
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    override fun autoStartLoad(): Boolean = true
}