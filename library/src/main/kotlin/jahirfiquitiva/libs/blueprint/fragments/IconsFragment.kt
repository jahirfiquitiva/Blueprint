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
package jahirfiquitiva.libs.blueprint.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.fragments.dialogs.IconDialog
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.models.IconsCategory
import jahirfiquitiva.libs.blueprint.models.viewmodels.IconItemViewModel
import jahirfiquitiva.libs.frames.fragments.base.BaseViewModelFragment
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.printInfo
import jahirfiquitiva.libs.kauextensions.ui.views.EmptyViewRecyclerView

class IconsFragment:BaseViewModelFragment<Icon>() {
    
    private lateinit var model:IconItemViewModel
    private lateinit var rv:EmptyViewRecyclerView
    private lateinit var fastScroller:RecyclerFastScroller
    
    private var dialog:IconDialog? = null
    
    fun applyFilters(filters:ArrayList<String>) {
        model.items.value?.let {
            if (filters.isNotEmpty()) {
                setAdapterItems(ArrayList(it.filter { validFilter(it.title, filters) }))
            } else {
                setAdapterItems(it)
            }
        }
    }
    
    fun doSearch(search:String = "") {
        model.items.value?.let {
            setAdapterItems(it, search)
        }
    }
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(IconItemViewModel::class.java)
    }
    
    override fun registerObserver() {
        model.items.observe(this, Observer { data ->
            data?.let { setAdapterItems(it) }
        })
    }
    
    private fun validFilter(title:String, filters:ArrayList<String>):Boolean {
        filters.forEach { if (title.equals(it, true)) return true }
        return false
    }
    
    private fun setAdapterItems(categories:ArrayList<IconsCategory>, filteredBy:String = "") {
        val adapter = rv.adapter
        if (adapter is IconsAdapter) {
            val icons = ArrayList<Icon>()
            categories.forEach {
                if (filteredBy.hasContent())
                    icons.addAll(it.icons.filter { it.name.contains(filteredBy, true) })
                else icons.addAll(it.icons)
            }
            adapter.setItems(ArrayList(icons.distinct().sorted()))
            rv.state = EmptyViewRecyclerView.State.NORMAL
        }
    }
    
    override fun unregisterObserver() {
        model.items.removeObservers(this)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss(activity)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss(activity)
    }
    
    override fun loadDataFromViewModel() {
        model.loadData(activity)
    }
    
    override fun getContentLayout():Int = R.layout.section_layout
    
    override fun initUI(content:View) {
        rv = content.findViewById(R.id.section_rv)
        fastScroller = content.findViewById(R.id.fast_scroller)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        rv.adapter = IconsAdapter(false, { onItemClicked(it) })
        val columns = context.getInteger(R.integer.icons_columns)
        rv.layoutManager = GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false)
        rv.state = EmptyViewRecyclerView.State.LOADING
        fastScroller.attachRecyclerView(rv)
    }
    
    override fun onItemClicked(item:Icon) {
        dialog?.dismiss(activity)
        dialog = IconDialog()
        dialog?.show(activity, item.name, item.icon, true)
    }
}