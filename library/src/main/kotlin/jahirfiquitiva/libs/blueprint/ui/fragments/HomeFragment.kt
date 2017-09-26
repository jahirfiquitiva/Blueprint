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

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.setPaddingBottom
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeApplyCardViewModel
import jahirfiquitiva.libs.blueprint.providers.viewmodels.HomeItemViewModel
import jahirfiquitiva.libs.blueprint.ui.activities.BottomNavigationBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.activities.base.BaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.HomeItemsAdapter
import jahirfiquitiva.libs.frames.ui.fragments.base.BaseViewModelFragment
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.openLink

class HomeFragment:BaseViewModelFragment<HomeItem>() {
    
    override fun autoStartLoad():Boolean = true
    
    private lateinit var model:HomeItemViewModel
    private lateinit var applyCardModel:HomeApplyCardViewModel
    private lateinit var rv:EmptyViewRecyclerView
    private lateinit var homeAdapter:HomeItemsAdapter
    
    override fun initViewModel() {
        model = ViewModelProviders.of(this).get(HomeItemViewModel::class.java)
        applyCardModel = ViewModelProviders.of(this).get(HomeApplyCardViewModel::class.java)
    }
    
    override fun registerObserver() {
        model.observe(this, {
            homeAdapter.setItems(ArrayList(it))
            rv.state = EmptyViewRecyclerView.State.NORMAL
        })
        applyCardModel.observe(this, {
            homeAdapter.shouldShowApplyCard = it
            homeAdapter.notifyDataSetChanged()
        })
    }
    
    override fun unregisterObserver() {
        model.destroy(this)
        applyCardModel.destroy(this)
    }
    
    override fun loadDataFromViewModel() {
        model.loadData(activity)
        applyCardModel.loadData(activity)
    }
    
    override fun getContentLayout():Int = R.layout.section_layout
    
    override fun initUI(content:View) {
        rv = content.findViewById(R.id.list_rv)
        if (activity is BaseBlueprintActivity) {
            (activity as BaseBlueprintActivity).fabsMenu.attachToRecyclerView(rv)
        }
        if (activity is BottomNavigationBlueprintActivity) {
            val bottomNavigationHeight = (activity as BottomNavigationBlueprintActivity).bottomBar.height
            rv.setPaddingBottom(64F.dpToPx.toInt() + bottomNavigationHeight)
        } else {
            rv.setPaddingBottom(64F.dpToPx.toInt())
        }
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        rv.state = EmptyViewRecyclerView.State.LOADING
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        homeAdapter = HomeItemsAdapter(activity, { onItemClicked(it) },
                                       context.getInteger(R.integer.icons_count),
                                       context.getInteger(R.integer.wallpapers_count),
                                       context.getInteger(R.integer.kwgt_count),
                                       context.getInteger(R.integer.zooper_count))
        rv.setHasFixedSize(true)
        rv.adapter = homeAdapter
    }
    
    override fun onItemClicked(item:HomeItem) =
            if (item.intent != null) context.startActivity(item.intent)
            else context.openLink(item.url)
}