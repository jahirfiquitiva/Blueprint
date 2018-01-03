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
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.data.models.IconsCategory
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.providers.viewmodels.IconItemViewModel
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.IconDialog
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.ctxt
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.hasContent

class IconsFragment : ViewModelFragment<Icon>() {
    
    override fun autoStartLoad(): Boolean = true
    
    private lateinit var model: IconItemViewModel
    private lateinit var rv: EmptyViewRecyclerView
    private lateinit var fastScroller: RecyclerFastScroller
    
    private var dialog: IconDialog? = null
    
    fun applyFilters(filters: ArrayList<String>) {
        model.getData()?.let {
            if (filters.isNotEmpty()) {
                setAdapterItems(ArrayList(it.filter { validFilter(it.title, filters) }))
            } else {
                setAdapterItems(ArrayList(it))
            }
        }
    }
    
    fun doSearch(search: String = "") {
        model.getData()?.let {
            setAdapterItems(ArrayList(it), search)
        }
    }
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(IconItemViewModel::class.java)
    }
    
    override fun registerObserver() {
        model.observe(
                this, {
            setAdapterItems(ArrayList(it))
        })
    }
    
    private fun validFilter(title: String, filters: ArrayList<String>): Boolean {
        filters.forEach { if (title.equals(it, true)) return true }
        return false
    }
    
    private fun setAdapterItems(categories: ArrayList<IconsCategory>, filteredBy: String = "") {
        val adapter = rv.adapter
        if (adapter is IconsAdapter) {
            val icons = ArrayList<Icon>()
            categories.forEach {
                val category = it
                if (filteredBy.hasContent())
                    icons.addAll(
                            it.icons.filter {
                                if (ctxt.bpKonfigs.deepSearchEnabled) {
                                    it.name.contains(filteredBy, true) ||
                                            category.title.contains(filteredBy, true)
                                } else {
                                    it.name.contains(filteredBy, true)
                                }
                            })
                else icons.addAll(it.icons)
            }
            adapter.setItems(ArrayList(icons.distinct().sorted()))
            rv.state = EmptyViewRecyclerView.State.NORMAL
        }
    }
    
    override fun unregisterObserver() = model.destroy(this)
    
    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.dismiss(actv, IconDialog.TAG)
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss(actv, IconDialog.TAG)
    }
    
    override fun loadDataFromViewModel() = model.loadData(actv)
    
    override fun getContentLayout(): Int = R.layout.section_layout
    
    override fun initUI(content: View) {
        rv = content.findViewById(R.id.list_rv)
        fastScroller = content.findViewById(R.id.fast_scroller)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        rv.adapter = IconsAdapter(false, { onItemClicked(it, false) })
        val columns = ctxt.getInteger(R.integer.icons_columns)
        rv.layoutManager = GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false)
        rv.state = EmptyViewRecyclerView.State.LOADING
        fastScroller.attachRecyclerView(rv)
    }
    
    override fun onItemClicked(item: Icon, longClick: Boolean) {
        if (!longClick) {
            dialog?.dismiss(actv, IconDialog.TAG)
            dialog = IconDialog()
            dialog?.show(actv, item.name, item.icon, ctxt.bpKonfigs.animationsEnabled)
        }
    }
}