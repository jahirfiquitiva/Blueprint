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

package jahirfiquitiva.libs.iconshowcase.activities

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.activities.base.InternalBaseShowcaseActivity
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.Preferences
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.AttributeExtractor
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils

open class BottomBarShowcaseActivity:InternalBaseShowcaseActivity() {

    var bottomBar:AHBottomNavigation? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar(true)
        prefs = Preferences(this)
        setContentView(R.layout.activity_bottom_bar_showcase)
        initMainComponents()
        initBottomBar()
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

    private fun initBottomBar() {
        bottomBar = findViewById(R.id.bottom_navigation) as AHBottomNavigation
        bottomBar?.defaultBackgroundColor = AttributeExtractor.getCardBgColorFrom(this)
        bottomBar?.isBehaviorTranslationEnabled = false
        // if (fab != null)
        // bottomBar?.manageFloatingActionButtonBehavior(fab)
        bottomBar?.accentColor = AttributeExtractor.getAccentColorFrom(this)
        bottomBar?.inactiveColor = ColorUtils.getMaterialInactiveIconsColor(
                ThemeUtils.isDarkTheme())
        bottomBar?.isForceTint = true
        bottomBar?.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
        getNavigationItems().forEach {
            bottomBar?.addItem(
                    AHBottomNavigationItem(ResourceUtils.getString(this, it.title), it.icon))
        }
        bottomBar?.setOnTabSelectedListener { position, _ ->
            return@setOnTabSelectedListener navigateToItem(getNavigationItems()[position])
        }
        bottomBar?.setCurrentItem(0, true)
    }

    override fun getNavigationItems():Array<NavigationItem> {
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