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

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.adapters.HomeItemsAdapter
import jahirfiquitiva.libs.blueprint.fragments.base.BaseViewModelFragment
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.blueprint.models.viewmodels.HomeApplyCardViewModel
import jahirfiquitiva.libs.blueprint.models.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import jahirfiquitiva.libs.kauextensions.ui.views.EmptyViewRecyclerView

class HomeFragment:BaseViewModelFragment() {
    
    private lateinit var model:HomeItemViewModel
    private lateinit var applyCardModel:HomeApplyCardViewModel
    private lateinit var rv:EmptyViewRecyclerView
    private lateinit var homeAdapter:HomeItemsAdapter
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(HomeItemViewModel::class.java)
        applyCardModel = ViewModelProviders.of(this).get(HomeApplyCardViewModel::class.java)
    }
    
    override fun registerObserver() {
        model.items.observe(this, Observer<ArrayList<HomeItem>> { data ->
            data?.let {
                homeAdapter.setItems(it)
                rv.state = EmptyViewRecyclerView.State.NORMAL
            }
        })
        applyCardModel.items.observe(this, Observer<Boolean> { data ->
            try {
                homeAdapter.shouldShowApplyCard = data ?: false
                homeAdapter.notifyDataSetChanged()
            } catch (ignored:Exception) {
            }
        })
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun unregisterObserver() {
        model.items.removeObservers(this)
        applyCardModel.items.removeObservers(this)
    }
    
    override fun loadDataFromViewModel() {
        model.loadData(activity)
        applyCardModel.loadData(activity)
    }
    
    override fun getContentLayout():Int = R.layout.section_home
    
    override fun initUI(content:View) {
        rv = content.findViewById(R.id.home_rv)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        rv.state = EmptyViewRecyclerView.State.LOADING
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        homeAdapter = HomeItemsAdapter(context, { onItemClicked(it) })
        rv.setHasFixedSize(true)
        rv.adapter = homeAdapter
    }
    
    override fun onItemClicked(item:Any) {
        if (item is HomeItem) {
            if (item.intent != null) context.startActivity(item.intent)
            else context.openLink(item.url)
        }
    }
}