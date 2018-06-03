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
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.setPaddingBottom
import com.bumptech.glide.Glide
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.extensions.lazyViewModel
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.configs
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.models.IconsCategory
import jahirfiquitiva.libs.blueprint.providers.viewmodels.IconsViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.IconDialog
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_PICKER
import jahirfiquitiva.libs.frames.helpers.utils.IMAGE_PICKER
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kext.extensions.activity
import jahirfiquitiva.libs.kext.extensions.ctxt
import jahirfiquitiva.libs.kext.extensions.getUri
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.int

@Suppress("DEPRECATION")
class IconsFragment : ViewModelFragment<Icon>() {
    
    override fun autoStartLoad(): Boolean = true
    
    private var pickerKey = 0
    
    companion object {
        fun create(key: Int) = IconsFragment().apply { pickerKey = key }
    }
    
    private val model: IconsViewModel by lazyViewModel()
    private var recyclerView: EmptyViewRecyclerView? = null
    private var fastScroller: RecyclerFastScroller? = null
    
    private var dialog: IconDialog? = null
    
    private val adapter: IconsAdapter? by lazy {
        IconsAdapter(context?.let { Glide.with(it) }, false) { onItemClicked(it, false) }
    }
    
    fun applyFilters(filters: ArrayList<String>) {
        val list = ArrayList(model.getData().orEmpty())
        if (filters.isNotEmpty()) {
            setAdapterItems(list.jfilter { validFilter(it.title, filters) })
        } else {
            setAdapterItems(list)
        }
    }
    
    fun doSearch(search: String = "", closed: Boolean = false) {
        if (search.hasContent()) {
            recyclerView?.setEmptyImage(R.drawable.no_results)
            recyclerView?.setEmptyText(R.string.search_no_results)
        } else {
            recyclerView?.setEmptyImage(R.drawable.empty_section)
            recyclerView?.setEmptyText(R.string.empty_section)
        }
        setAdapterItems(ArrayList(model.getData().orEmpty()), search)
        if (!closed)
            scrollToTop()
    }
    
    fun scrollToTop() {
        recyclerView?.post { recyclerView?.scrollToPosition(0) }
    }
    
    override fun registerObservers() {
        model.observe(this) { categories ->
            setAdapterItems(ArrayList(categories))
            (activity as? BaseBlueprintActivity)?.let {
                val filters = ArrayList<String>()
                categories.forEach { filters += it.title }
                it.initFiltersDrawer(filters)
            }
        }
    }
    
    private fun setAdapterItems(categories: ArrayList<IconsCategory>, filteredBy: String = "") {
        val icons = ArrayList<Icon>()
        categories.forEach { category ->
            if (filteredBy.hasContent())
                icons.addAll(
                    category.getIcons().jfilter { validIconFilter(filteredBy, it, category) })
            else icons.addAll(category.getIcons())
        }
        adapter?.setItems(ArrayList(icons.distinctBy { it.name }.sortedBy { it.name }))
    }
    
    private fun validFilter(title: String, filters: ArrayList<String>): Boolean {
        filters.forEach { if (title.equals(it, true)) return true }
        return false
    }
    
    private fun validIconFilter(filter: String, icon: Icon, category: IconsCategory): Boolean {
        return if (filter.hasContent()) {
            if (configs.deepSearchEnabled) {
                icon.name.contains(filter, true) || category.title.contains(filter, true)
            } else {
                icon.name.contains(filter, true)
            }
        } else true
    }
    
    override fun unregisterObservers() {
        model.destroy(this)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        activity { dialog?.dismiss(it, IconDialog.TAG) }
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        activity { dialog?.dismiss(it, IconDialog.TAG) }
    }
    
    override fun loadDataFromViewModel() = activity { model.loadData(it) }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    
    override fun initUI(content: View) {
        recyclerView = content.findViewById(R.id.list_rv)
        fastScroller = content.findViewById(R.id.fast_scroller)
        
        val hasBottomNav = (activity as? BaseBlueprintActivity)?.hasBottomNavigation() ?: false
        if (hasBottomNav) {
            recyclerView?.setPaddingBottom(64.dpToPx)
            fastScroller?.setPaddingBottom(48.dpToPx)
        }
        
        /*
        fastScroller?.setOnHandleTouchListener { _, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    (activity as? BaseBlueprintActivity)?.lockFiltersDrawer(true)
                }
                MotionEvent.ACTION_UP -> {
                    (activity as? BaseBlueprintActivity)?.lockFiltersDrawer(false)
                }
            }
            true
        }
        */
        
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.setEmptyImage(R.drawable.empty_section)
        
        recyclerView?.textView = content.findViewById(R.id.empty_text)
        recyclerView?.setEmptyText(R.string.empty_section)
        
        recyclerView?.loadingView = content.findViewById(R.id.loading_view)
        recyclerView?.setLoadingText(R.string.loading_section)
        
        recyclerView?.adapter = adapter
        val columns = ctxt.int(R.integer.icons_columns)
        recyclerView?.layoutManager =
            GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false)
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
    }
    
    override fun onItemClicked(item: Icon, longClick: Boolean) {
        if (!longClick) {
            if (pickerKey != 0) {
                pickIcon(item)
            } else {
                activity {
                    dialog?.dismiss(it, IconDialog.TAG)
                    dialog = IconDialog()
                    dialog?.show(it, item.name, item.icon, configs.animationsEnabled)
                }
            }
        }
    }
    
    private fun pickIcon(item: Icon) {
        activity { activity ->
            val intent = Intent()
            val bitmap: Bitmap? = try {
                val drawable =
                    ResourcesCompat.getDrawable(resources, item.icon, null) as BitmapDrawable?
                drawable?.bitmap ?: BitmapFactory.decodeResource(resources, item.icon)
            } catch (e: Exception) {
                null
            }
            
            if (bitmap != null) {
                if (pickerKey == ICONS_PICKER) {
                    intent.putExtra("icon", bitmap)
                    val iconRes = Intent.ShortcutIconResource.fromContext(activity, item.icon)
                    intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
                } else if (pickerKey == IMAGE_PICKER) {
                    val uri: Uri? = bitmap.getUri(activity, item.name)
                    if (uri != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.data = uri
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    intent.putExtra("return-data", false)
                }
                activity.setResult(Activity.RESULT_OK, intent)
            } else {
                activity.setResult(Activity.RESULT_CANCELED, intent)
            }
            bitmap?.let {
                if (!it.isRecycled) it.recycle()
            }
            activity.finish()
        }
    }
    
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !allowReloadAfterVisibleToUser()) recyclerView?.updateEmptyState()
    }
}