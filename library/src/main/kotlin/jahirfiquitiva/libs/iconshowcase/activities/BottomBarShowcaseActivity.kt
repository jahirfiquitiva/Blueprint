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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.Toolbar
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.activities.base.InternalBaseShowcaseActivity
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.ui.views.CounterFab
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences
import jahirfiquitiva.libs.iconshowcase.utils.themes.AttributeExtractor

open class BottomBarShowcaseActivity:InternalBaseShowcaseActivity() {

    var bottomBar:BottomNavigationView? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar(true)
        prefs = Preferences(this)
        setContentView(R.layout.activity_bottom_bar_showcase)
        initToolbar()
        initCollapsingToolbar()
        initBottomBar()
        initFAB()
    }

    override fun initToolbar() {
        super.initToolbar()
        toolbar?.setOnMenuItemClickListener(
                Toolbar.OnMenuItemClickListener { item ->
                    val i = item.itemId
                    if (i == R.id.switch_theme) {
                        // switchTheme()
                        return@OnMenuItemClickListener true
                    }
                    return@OnMenuItemClickListener false
                })
    }

    fun initFAB() {
        fab = findViewById(R.id.fab) as CounterFab
    }

    private fun initBottomBar() {
        bottomBar = findViewById(R.id.bottom_navigation) as BottomNavigationView
        bottomBar?.background = ColorDrawable(AttributeExtractor.getCardBgColorFrom(this))
        bottomBar?.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener {
                    val id = 10 //getItemId(item.getItemId(), true)
                    if (id >= 0) {
                        return@OnNavigationItemSelectedListener navigateToItem(
                                getNavigationItems()?.get(id) as NavigationItem)
                    }
                    return@OnNavigationItemSelectedListener false
                })
    }

    override fun getNavigationItems():Array<NavigationItem>? {
        return arrayOf(
                NavigationItem("Home", NavigationItem.DEFAULT_HOME_POSITION, R.string.section_home,
                        R.drawable.ic_home),
                NavigationItem("Previews", NavigationItem.DEFAULT_PREVIEWS_POSITION,
                        R.string.section_icons, R.drawable.ic_previews),
                NavigationItem("Wallpapers", NavigationItem.DEFAULT_WALLPAPERS_POSITION,
                        R.string.section_wallpapers, R.drawable.ic_wallpapers),
                NavigationItem("Apply", NavigationItem.DEFAULT_APPLY_POSITION,
                        R.string.section_apply, R.drawable.ic_apply),
                NavigationItem("Requests", NavigationItem.DEFAULT_REQUEST_POSITION,
                        R.string.section_icon_request, R.drawable.ic_request)
                      )
    }

}