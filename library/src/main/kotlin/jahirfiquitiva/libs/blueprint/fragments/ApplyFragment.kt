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

import android.support.v7.widget.GridLayoutManager
import android.view.View
import ca.allanwang.kau.utils.isAppInstalled
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.adapters.LaunchersAdapter
import jahirfiquitiva.libs.blueprint.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.extensions.showLauncherNotInstalledDialog
import jahirfiquitiva.libs.blueprint.extensions.supportedLaunchers
import jahirfiquitiva.libs.blueprint.fragments.base.BaseSectionFragment
import jahirfiquitiva.libs.blueprint.fragments.presenters.BasicFragmentPresenter
import jahirfiquitiva.libs.blueprint.models.Launcher
import jahirfiquitiva.libs.kauextensions.extensions.getDimensionPixelSize
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.printInfo
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kauextensions.ui.views.EmptyViewRecyclerView

class ApplyFragment:BaseSectionFragment<Launcher>(), BasicFragmentPresenter {
    override fun getContentLayout():Int = R.layout.section_layout
    
    override fun initUI(content:View) {
        val rv:EmptyViewRecyclerView = content.findViewById(R.id.section_rv)
        val fastScroller:RecyclerFastScroller = content.findViewById(R.id.fast_scroller)
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text)
        val adapter = LaunchersAdapter { onItemClicked(it) }
        rv.adapter = adapter
        val columns = context.getInteger(R.integer.icons_columns) - 1
        rv.layoutManager = GridLayoutManager(context, columns, GridLayoutManager.VERTICAL, false)
        rv.addItemDecoration(GridSpacingItemDecoration(columns, context.getDimensionPixelSize(
                R.dimen.cards_margin)))
        rv.state = EmptyViewRecyclerView.State.LOADING
        fastScroller.attachRecyclerView(rv)
        val list = ArrayList<Launcher>()
        context.supportedLaunchers.forEach {
            list.add(it)
        }
        adapter.setItems(
                ArrayList(list.distinct().sortedBy { isLauncherInstalled(it.packageNames) }))
        rv.state = EmptyViewRecyclerView.State.NORMAL
    }
    
    private fun isLauncherInstalled(packages:Array<String>):Boolean {
        packages.forEach {
            if (context.isAppInstalled(it)) return true
        }
        return false
    }
    
    override fun onItemClicked(item:Launcher) {
        if (isLauncherInstalled(item.packageNames) || item.name.contains("lineage", true))
            context.executeLauncherIntent(item.name)
        else context.showLauncherNotInstalledDialog(item)
    }
}