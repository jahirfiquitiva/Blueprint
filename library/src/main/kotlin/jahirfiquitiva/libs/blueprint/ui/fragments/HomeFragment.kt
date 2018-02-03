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
import ca.allanwang.kau.utils.integer
import ca.allanwang.kau.utils.setPaddingBottom
import jahirfiquitiva.libs.archhelpers.ui.fragments.ViewModelFragment
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BottomNavigationBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.HomeAdapter
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class HomeFragment : ViewModelFragment<HomeItem>() {
    
    override fun autoStartLoad(): Boolean = true
    
    private var model: HomeItemViewModel? = null
    private var rv: EmptyViewRecyclerView? = null
    private val homeAdapter: HomeAdapter? by lazy {
        HomeAdapter(
                WeakReference(activity),
                activity?.integer(R.integer.icons_count) ?: 0,
                activity?.integer(R.integer.wallpapers_count) ?: 0
                   ) {
            onItemClicked(it, false)
        }
    }
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(HomeItemViewModel::class.java)
    }
    
    override fun registerObserver() {
        model?.observe(
                this, {
            homeAdapter?.updateItems(ArrayList(it))
            rv?.state = EmptyViewRecyclerView.State.NORMAL
        })
    }
    
    override fun unregisterObserver() {
        model?.destroy(this)
    }
    
    override fun loadDataFromViewModel() {
        actv { model?.loadData(it) }
    }
    
    override fun getContentLayout(): Int = R.layout.section_layout
    
    override fun initUI(content: View) {
        rv = content.findViewById(R.id.list_rv)
        rv?.let { (activity as? BaseBlueprintActivity)?.fabsMenu?.attachToRecyclerView(it) }
        
        val bottomNavigationHeight =
                (activity as? BottomNavigationBlueprintActivity)?.bottomBar?.height ?: 0
        rv?.setPaddingBottom(64F.dpToPx.toInt() + bottomNavigationHeight)
        
        rv?.emptyView = content.findViewById(R.id.empty_view)
        rv?.textView = content.findViewById(R.id.empty_text)
        rv?.state = EmptyViewRecyclerView.State.LOADING
        rv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv?.setHasFixedSize(true)
        rv?.adapter = homeAdapter
    }
    
    override fun onItemClicked(item: HomeItem, longClick: Boolean) {
        if (!longClick) {
            if (item.intent != null) context?.startActivity(item.intent)
            else context?.openLink(item.url)
        }
    }
}