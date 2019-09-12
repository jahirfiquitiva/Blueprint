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
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.sendEmail
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpItem
import jahirfiquitiva.libs.frames.helpers.extensions.jfilter
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.tint
import jahirfiquitiva.libs.kext.ui.activities.ThemedActivity
import jahirfiquitiva.libs.kext.ui.widgets.CustomSearchView

@SuppressLint("MissingSuperCall")
class HelpActivity : ThemedActivity<BPKonfigs>() {
    
    override val prefs: BPKonfigs by lazy { BPKonfigs(this) }
    override fun lightTheme(): Int = R.style.BlueprintLightTheme
    override fun darkTheme(): Int = R.style.BlueprintDarkTheme
    override fun amoledTheme(): Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme(): Int = R.style.BlueprintTransparentTheme
    override fun autoTintStatusBar() = true
    override fun autoTintNavigationBar() = true
    
    private val toolbar: Toolbar? by bind(R.id.toolbar)
    private val recyclerView: EmptyViewRecyclerView? by bind(R.id.list_rv)
    private val fastScroller: RecyclerFastScroller? by bind(R.id.fast_scroller)
    
    private var searchItem: MenuItem? = null
    private var searchView: CustomSearchView? = null
    
    private val faqs = ArrayList<HelpItem>()
    private val adapter = HelpAdapter()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        
        val questions = resources.getStringArray(R.array.questions)
        val answers = resources.getStringArray(R.array.answers)
        
        faqs.clear()
        questions.indices.mapTo(faqs) { HelpItem(questions[it], answers[it]) }
        
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.section_help)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        val refreshLayout: SwipeRefreshLayout? by bind(R.id.swipe_to_refresh)
        refreshLayout?.isEnabled = false
        
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.state = EmptyViewRecyclerView.State.LOADING
        
        adapter.setItems(faqs)
        recyclerView?.adapter = adapter
        
        recyclerView?.let { fastScroller?.attachRecyclerView(it) }
        
        recyclerView?.state = EmptyViewRecyclerView.State.NORMAL
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_help, menu)
        menu?.let {
            searchItem = it.findItem(R.id.search)
            searchView = searchItem?.actionView as? CustomSearchView
            searchView?.onCollapse = { doSearch() }
            searchView?.onQueryChanged = { doSearch(it) }
            searchView?.onQuerySubmit = { doSearch(it) }
            searchView?.bindToItem(searchItem)
            
            searchView?.queryHint = getString(R.string.search_x, "")
            
            searchView?.tint(getPrimaryTextColorFor(primaryColor, 0.6F))
            it.tint(getActiveIconsColorFor(primaryColor, 0.6F))
        }
        
        toolbar?.tint(
            getPrimaryTextColorFor(primaryColor, 0.6F),
            getSecondaryTextColorFor(primaryColor, 0.6F),
            getActiveIconsColorFor(primaryColor, 0.6F))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.contact -> {
                sendEmail(getString(R.string.email), "${getAppName()} Support")
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun doSearch(filter: String = "") {
        if (filter.hasContent()) {
            adapter.setItems(faqs.jfilter {
                (it.question.contains(filter, true) || it.answer.contains(filter, true))
            })
        } else {
            adapter.setItems(faqs)
        }
    }
}