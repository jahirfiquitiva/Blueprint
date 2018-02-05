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
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.setPaddingBottom
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.blueprint.providers.viewmodels.IconsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BottomNavigationBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.HomeAdapter
import jahirfiquitiva.libs.frames.providers.viewmodels.WallpapersViewModel
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class HomeFragment : ViewModelFragment<HomeItem>() {
    
    override fun autoStartLoad(): Boolean = true
    
    private var model: HomeItemViewModel? = null
    private var iconsModel: IconsViewModel? = null
    private var wallsModel: WallpapersViewModel? = null
    
    private var recyclerView: EmptyViewRecyclerView? = null
    private val homeAdapter: HomeAdapter? by lazy {
        HomeAdapter(
                WeakReference(activity),
                iconsModel?.getData().orEmpty().size,
                wallsModel?.getData().orEmpty().size) {
            onItemClicked(it, false)
        }
    }
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(HomeItemViewModel::class.java)
        iconsModel = ViewModelProviders.of(this).get(IconsViewModel::class.java)
        wallsModel = ViewModelProviders.of(this).get(WallpapersViewModel::class.java)
    }
    
    override fun registerObserver() {
        model?.observe(this) {
            homeAdapter?.updateItems(ArrayList(it))
            recyclerView?.state = EmptyViewRecyclerView.State.NORMAL
        }
        iconsModel?.observe(this) { homeAdapter?.updateIconsCount(it.size) }
        wallsModel?.observe(this) { homeAdapter?.updateWallsCount(it.size) }
    }
    
    override fun unregisterObserver() {
        model?.destroy(this)
        iconsModel?.destroy(this)
        wallsModel?.destroy(this)
    }
    
    override fun loadDataFromViewModel() {
        actv {
            model?.loadData(it)
            iconsModel?.loadData(it)
            wallsModel?.loadData(it)
        }
    }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        recyclerView?.let {
            (activity as? BaseBlueprintActivity)?.fabsMenu?.attachToRecyclerView(
                    it)
        }
        
        val bottomNavigationHeight =
                (activity as? BottomNavigationBlueprintActivity)?.bottomBar?.height ?: 0
        recyclerView?.setPaddingBottom(64F.dpToPx.toInt() + bottomNavigationHeight)
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        recyclerView?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = homeAdapter
        
        val fastScroller: RecyclerFastScroller? by content.bind(R.id.fast_scroller)
        fastScroller?.attachRecyclerView(recyclerView)
    }
    
    override fun onItemClicked(item: HomeItem, longClick: Boolean) {
        if (!longClick) {
            if (item.intent != null) context?.startActivity(item.intent)
            else context?.openLink(item.url)
        }
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !allowReloadAfterVisibleToUser()) recyclerView?.updateEmptyState()
    }
}