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

import android.app.WallpaperManager
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.setPaddingBottom
import ca.allanwang.kau.utils.startLink
import com.bumptech.glide.Glide
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.blueprint.providers.viewmodels.IconsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.HomeAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.PreviewCardHolder
import jahirfiquitiva.libs.frames.providers.viewmodels.WallpapersViewModel
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class HomeFragment : ViewModelFragment<HomeItem>() {
    
    override fun autoStartLoad(): Boolean = true
    
    private var model: HomeItemViewModel? = null
    private var iconsModel: IconsViewModel? = null
    private var wallsModel: WallpapersViewModel? = null
    
    private var recyclerView: EmptyViewRecyclerView? = null
    private var nestedScroll: NestedScrollView? = null
    private var previewCardHolder: PreviewCardHolder? = null
    
    private val homeAdapter: HomeAdapter? by lazy {
        HomeAdapter(
                WeakReference(activity),
                iconsModel?.getData().orEmpty().size,
                wallsModel?.getData().orEmpty().size) {
            onItemClicked(it, false)
        }
    }
    
    private val defaultPicture: Drawable?
        get() {
            val picName = activity?.getString(R.string.icons_preview_picture)
            return if (picName.orEmpty().hasContent()) {
                activity?.let {
                    try {
                        picName?.let { s -> it.getDrawable(s) }
                    } catch (ignored: Exception) {
                        null
                    }
                }
            } else null
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
        iconsModel?.observe(this) { categories ->
            val allIcons = ArrayList<Icon>()
            val filters = ArrayList<String>()
            categories.forEach {
                allIcons.addAll(it.getIcons())
                filters += it.title
            }
            homeAdapter?.updateIconsCount(ArrayList(allIcons.distinctBy { it.name }).size)
            (activity as? BaseBlueprintActivity)?.initFiltersDrawer(filters)
        }
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
    
    override fun getContentLayout(): Int = R.layout.section_home
    
    override fun initUI(content: View) {
        previewCardHolder = PreviewCardHolder(
                IconsAdapter(context?.let { Glide.with(it) }, true),
                content.findViewById(R.id.icons_preview_card))
        
        nestedScroll = content.findViewById(R.id.nested_scroll)
        
        recyclerView = content.findViewById(R.id.list_rv)
        recyclerView?.isNestedScrollingEnabled = false
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        recyclerView?.setPaddingBottom(64.dpToPx * (if (hasBottomNav) 2 else 1))
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        recyclerView?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = homeAdapter
        
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        
        actv {
            (it as? BaseBlueprintActivity)?.let {
                it.requestWallpaperPermission(
                        it.getString(R.string.permission_request_wallpaper, it.getAppName())) {
                    bindPreviewCard()
                }
            }
        }
    }
    
    override fun onItemClicked(item: HomeItem, longClick: Boolean) {
        if (!longClick) {
            if (item.intent != null) context?.startActivity(item.intent)
            else context?.startLink(item.url)
        }
    }
    
    fun scrollToTop() {
        nestedScroll?.post { nestedScroll?.scrollTo(0, 0) }
    }
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !allowReloadAfterVisibleToUser()) recyclerView?.updateEmptyState()
        if (isVisibleToUser) scrollToTop()
    }
    
    private fun bindPreviewCard() {
        val wallManager: WallpaperManager? = WallpaperManager.getInstance(activity)
        val drawable: Drawable? =
                if (activity?.bpKonfigs?.wallpaperInIconsPreview == true) {
                    try {
                        wallManager?.fastDrawable
                    } catch (e: Exception) {
                        defaultPicture
                    }
                } else {
                    defaultPicture
                }
        previewCardHolder?.bind(drawable)
    }
}