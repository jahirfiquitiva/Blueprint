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
package jahirfiquitiva.libs.blueprint.ui.activities

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpAdapter
import jahirfiquitiva.libs.blueprint.ui.adapters.HelpItem
import jahirfiquitiva.libs.frames.ui.widgets.EmptyViewRecyclerView
import jahirfiquitiva.libs.kauextensions.activities.ThemedActivity
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.tint

class HelpActivity:ThemedActivity() {
    override fun lightTheme():Int = R.style.BlueprintLightTheme
    override fun darkTheme():Int = R.style.BlueprintDarkTheme
    override fun amoledTheme():Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme():Int = R.style.BlueprintTransparentTheme
    override fun autoStatusBarTint():Boolean = true
    
    private lateinit var toolbar:Toolbar
    private lateinit var rv:EmptyViewRecyclerView
    private lateinit var fastScroll:RecyclerFastScroller
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        
        val questions = resources.getStringArray(R.array.questions)
        val answers = resources.getStringArray(R.array.answers)
        
        val faqs = ArrayList<HelpItem>()
        questions.indices.mapTo(faqs) { HelpItem(questions[it], answers[it]) }
        
        toolbar = findViewById(R.id.toolbar)
        
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.about)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh)
        refreshLayout.isEnabled = false
        
        rv = findViewById(R.id.list_rv)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.itemAnimator = DefaultItemAnimator()
        rv.state = EmptyViewRecyclerView.State.LOADING
        
        val adapter = HelpAdapter()
        adapter.setItems(faqs)
        rv.adapter = adapter
        
        fastScroll = findViewById(R.id.fast_scroller)
        fastScroll.attachRecyclerView(rv)
        
        rv.state = EmptyViewRecyclerView.State.NORMAL
    }
    
    override fun onCreateOptionsMenu(menu:Menu?):Boolean {
        toolbar.tint(getPrimaryTextColorFor(primaryColor, 0.6F),
                     getSecondaryTextColorFor(primaryColor, 0.6F),
                     getActiveIconsColorFor(primaryColor, 0.6F))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item:MenuItem?):Boolean {
        item?.let {
            if (it.itemId == android.R.id.home) finish()
        }
        return super.onOptionsItemSelected(item)
    }
}