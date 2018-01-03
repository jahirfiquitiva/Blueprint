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
package jahirfiquitiva.libs.blueprint.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.email.sendEmail
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpItem
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.ui.activities.ThemedActivity
import jahirfiquitiva.libs.kauextensions.ui.widgets.SearchView
import jahirfiquitiva.libs.kauextensions.ui.widgets.bindSearchView

@SuppressLint("MissingSuperCall")
class HelpActivity : ThemedActivity() {
    override fun lightTheme(): Int = R.style.BlueprintLightTheme
    override fun darkTheme(): Int = R.style.BlueprintDarkTheme
    override fun amoledTheme(): Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme(): Int = R.style.BlueprintTransparentTheme
    override fun autoTintStatusBar() = true
    override fun autoTintNavigationBar() = true
    
    private val toolbar: Toolbar by bind(R.id.toolbar)
    private val rv: EmptyViewRecyclerView by bind(R.id.list_rv)
    private val fastScroll: RecyclerFastScroller by bind(R.id.fast_scroller)
    
    var searchView: SearchView? = null
    val faqs = ArrayList<HelpItem>()
    val adapter = HelpAdapter()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        
        val questions = resources.getStringArray(R.array.questions)
        val answers = resources.getStringArray(R.array.answers)
        
        faqs.clear()
        questions.indices.mapTo(faqs) { HelpItem(questions[it], answers[it]) }
        
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.about)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
        refreshLayout.isEnabled = false
        
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.itemAnimator = DefaultItemAnimator()
        rv.state = EmptyViewRecyclerView.State.LOADING
        
        adapter.setItems(faqs)
        rv.adapter = adapter
        
        fastScroll.attachRecyclerView(rv)
        
        rv.state = EmptyViewRecyclerView.State.NORMAL
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater?.inflate(R.menu.menu_help, menu)
        menu?.let { searchView = bindSearchView(it, R.id.search) }
        searchView?.listener = object : SearchView.SearchListener {
            override fun onQueryChanged(query: String) {
                doSearch(query)
            }
            
            override fun onQuerySubmit(query: String) {
                doSearch(query)
            }
            
            override fun onSearchOpened(searchView: SearchView) {
                // Do nothing
            }
            
            override fun onSearchClosed(searchView: SearchView) {
                doSearch()
            }
        }
        searchView?.hintText = getString(R.string.search_x, "")
        toolbar.tint(
                getPrimaryTextColorFor(primaryColor, 0.6F),
                getSecondaryTextColorFor(primaryColor, 0.6F),
                getActiveIconsColorFor(primaryColor, 0.6F))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                android.R.id.home -> finish()
                R.id.contact -> {
                    sendEmail(getString(R.string.email), "${getAppName()} Support")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun doSearch(filter: String = "") {
        if (filter.hasContent()) {
            adapter.setItems(
                    ArrayList(
                            faqs.filter {
                                (it.question.contains(filter, true) || it.answer.contains(
                                        filter, true))
                            }))
        } else {
            adapter.setItems(faqs)
        }
    }
}