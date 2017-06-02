/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *
 * Special thanks to the project contributors and collaborators
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.adapters.IconsAdapter
import jahirfiquitiva.libs.iconshowcase.fragments.presenters.ItemsFragmentPresenter
import jahirfiquitiva.libs.iconshowcase.models.Icon
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.tasks.BasicTaskLoader
import jahirfiquitiva.libs.iconshowcase.tasks.LoadIcons
import jahirfiquitiva.libs.iconshowcase.ui.views.EmptyViewRecyclerView
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils

class IconsFragment:Fragment(), ItemsFragmentPresenter<ArrayList<Icon>> {

    var recyclerView:EmptyViewRecyclerView? = null
    var fastScroller:RecyclerFastScroller? = null

    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.extra_icons_grid, container, false) as View
        initUI(content)
        return content
    }

    override fun onViewCreated(view:View?, savedInstanceState:Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executeTask(context)
    }

    override fun initUI(content:View) {
        recyclerView = content.findViewById(R.id.icons_grid) as EmptyViewRecyclerView
        fastScroller = content.findViewById(R.id.fast_scroller) as RecyclerFastScroller
        recyclerView?.emptyView = content.findViewById(R.id.empty_view)
        recyclerView?.textView = content.findViewById(R.id.empty_text) as TextView?
        recyclerView?.adapter = IconsAdapter {
            onItemClicked(it)
        }
        val columns = ResourceUtils.getInteger(context, R.integer.icons_grid_width)
        recyclerView?.layoutManager = GridLayoutManager(context, columns,
                GridLayoutManager.VERTICAL, false)
        recyclerView?.updateState(EmptyViewRecyclerView.STATE_LOADING)
    }

    override fun onItemClicked(item:Any) {
        if (item is Icon) {
            TODO("Not implemented yet")
        }
    }

    override fun getLoaderId():Int = NavigationItem.DEFAULT_PREVIEWS_POSITION

    override fun buildLoader():Loader<ArrayList<Icon>> = LoadIcons(context,
            object:BasicTaskLoader.TaskListener {
                override fun onTaskStarted() {
                    recyclerView?.updateState(EmptyViewRecyclerView.STATE_LOADING)
                }
            })

    override fun onDataLoadFinished(data:ArrayList<Icon>) {
        val adapter = recyclerView?.adapter
        if (adapter is IconsAdapter) {
            adapter.clearAndAddAll(data)
            recyclerView?.updateState(EmptyViewRecyclerView.STATE_NORMAL)
        }
    }

}