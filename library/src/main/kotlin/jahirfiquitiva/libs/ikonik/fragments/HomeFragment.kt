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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.Loader
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jahirfiquitiva.libs.ikonik.R
import jahirfiquitiva.libs.ikonik.adapters.HomeCardsAdapter
import jahirfiquitiva.libs.ikonik.fragments.presenters.ItemsFragmentPresenter
import jahirfiquitiva.libs.ikonik.models.HomeCard
import jahirfiquitiva.libs.ikonik.models.NavigationItem
import jahirfiquitiva.libs.ikonik.tasks.BasicTaskLoader
import jahirfiquitiva.libs.ikonik.tasks.HomeCardsLoader
import jahirfiquitiva.libs.ikonik.ui.views.EmptyViewRecyclerView
import jahirfiquitiva.libs.ikonik.utils.ColorUtils
import jahirfiquitiva.libs.ikonik.utils.NetworkUtils
import jahirfiquitiva.libs.ikonik.utils.themes.ThemeUtils

class HomeFragment:Fragment(), ItemsFragmentPresenter<ArrayList<HomeCard>> {

    var rv:EmptyViewRecyclerView? = null

    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.section_home, container, false) as View
        initUI(content)
        return content
    }

    override fun onViewCreated(view:View?, savedInstanceState:Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executeTask(context)
    }

    override fun initUI(content:View) {
        rv = content.findViewById(R.id.home_rv)
        rv?.emptyView = content.findViewById(R.id.empty_view)
        rv?.textView = content.findViewById(R.id.empty_text)
        rv?.updateState(EmptyViewRecyclerView.STATE_LOADING)
        rv?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val deco = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        deco.setDrawable(
                ColorDrawable(ColorUtils.getMaterialDividerColor(ThemeUtils.isDarkTheme())))
        rv?.addItemDecoration(deco)
        rv?.adapter = HomeCardsAdapter { onItemClicked(it) }
    }

    override fun onItemClicked(item:Any) {
        if (item is HomeCard) {
            if (item.intent != null) context.startActivity(item.intent)
            else NetworkUtils.openLink(context, item.url)
        }
    }

    override fun getLoaderId():Int = NavigationItem.DEFAULT_HOME_POSITION

    override fun buildLoader():Loader<ArrayList<HomeCard>> =
		HomeCardsLoader(context, object:BasicTaskLoader.TaskListener {
									override fun onTaskStarted() {
										rv?.updateState(
												EmptyViewRecyclerView.STATE_LOADING)
									}
								})

    override fun onDataLoadFinished(data:ArrayList<HomeCard>) {
        val adapter = rv?.adapter
        if (adapter is HomeCardsAdapter) {
            adapter.clearAndAddAll(data)
            rv?.updateState(EmptyViewRecyclerView.STATE_NORMAL)
        }
    }
}