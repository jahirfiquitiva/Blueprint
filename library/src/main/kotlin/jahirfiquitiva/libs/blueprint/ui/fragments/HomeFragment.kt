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
import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.openLink
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setPaddingBottom
import com.bumptech.glide.Glide
import jahirfiquitiva.libs.archhelpers.extensions.lazyViewModel
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.configs
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.blueprint.providers.viewmodels.IconsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.HomeAdapter
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.frames.viewmodels.WallpapersViewModel
import jahirfiquitiva.libs.kext.extensions.activity
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.hasContent
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class HomeFragment : ViewModelFragment<HomeItem>() {
    
    companion object {
        private const val PREVIEW_DELAY = 25L
    }
    
    private val model: HomeItemViewModel by lazyViewModel()
    private val iconsModel: IconsViewModel by lazyViewModel()
    private val wallsModel: WallpapersViewModel by lazyViewModel()
    
    private var recyclerView: EmptyViewRecyclerView? = null
    private var modelsLoaded = false
    
    private val homeAdapter: HomeAdapter? by lazy {
        HomeAdapter(
            WeakReference(activity),
            context?.let { Glide.with(it) },
            iconsModel.getData().orEmpty().size,
            wallsModel.getData().orEmpty().size) {
            onItemClicked(it, false)
        }
    }
    
    private val defaultPicture: Drawable? by lazy {
        val picName = activity?.getString(R.string.icons_preview_picture).orEmpty()
        if (picName.hasContent()) {
            try {
                activity?.drawable(picName)
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    override fun registerObservers() {
        model.observe(this) {
            bindPreviewCard()
            postDelayed(PREVIEW_DELAY) {
                homeAdapter?.updateItems(ArrayList(it))
                normalState()
            }
        }
        iconsModel.observe(this) { categories ->
            bindPreviewCard()
            postDelayed(PREVIEW_DELAY) {
                val allIcons = ArrayList<Icon>()
                val filters = ArrayList<String>()
                categories.forEach {
                    allIcons.addAll(it.getIcons())
                    filters += it.title
                }
                homeAdapter?.updateIconsCount(ArrayList(allIcons.distinctBy { it.name }).size)
                (activity as? BaseBlueprintActivity)?.initFiltersFromCategories(filters)
                normalState()
            }
        }
        wallsModel.observe(this) {
            bindPreviewCard()
            postDelayed(PREVIEW_DELAY) {
                homeAdapter?.updateWallsCount(it.size)
                normalState()
            }
        }
    }
    
    override fun unregisterObservers() {
        model.destroy(this)
        iconsModel.destroy(this)
        wallsModel.destroy(this)
    }
    
    override fun loadDataFromViewModel() {
        activity {
            model.loadData(it)
            iconsModel.loadData(it)
            wallsModel.loadData(it)
        }
    }
    
    private fun normalState() {
        postDelayed(10) {
            try {
                recyclerView?.state = EmptyViewRecyclerView.State.NORMAL
            } catch (e: Exception) {
            }
        }
    }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        recyclerView?.setPaddingBottom(64.dpToPx * (if (hasBottomNav) 2 else 1))
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = homeAdapter
        
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        
        requestWallPermission(true)
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
        if (isVisibleToUser) scrollToTop()
    }
    
    override fun onResume() {
        super.onResume()
        if (configs.shouldResetWallpaper) {
            configs.shouldResetWallpaper = false
            requestWallPermission(true)
        }
    }
    
    private fun requestWallPermission(onlyPicture: Boolean = false) {
        (activity as? BaseBlueprintActivity)?.let {
            it.requestWallpaperPermission(
                it.getString(R.string.permission_request_wallpaper, it.getAppName())) {
                bindPreviewCard(onlyPicture)
            }
        } ?: bindPreviewCard(onlyPicture)
    }
    
    @SuppressLint("MissingPermission")
    private fun bindPreviewCard(onlyPicture: Boolean = false) {
        if (!modelsLoaded || onlyPicture) {
            modelsLoaded = true
            val drawable: Drawable? = if (configs.wallpaperInIconsPreview) {
                try {
                    val wallManager: WallpaperManager? = WallpaperManager.getInstance(activity)
                    wallManager?.fastDrawable
                } catch (e: Exception) {
                    defaultPicture
                }
            } else {
                defaultPicture
            }
            homeAdapter?.updateWallpaper(drawable, onlyPicture)
            postDelayed(PREVIEW_DELAY) { loadDataFromViewModel() }
        }
    }
    
    override fun autoStartLoad(): Boolean = false
    override fun allowReloadAfterVisibleToUser(): Boolean = false
}