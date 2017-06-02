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
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.activities.base.InternalBaseShowcaseActivity
import jahirfiquitiva.libs.iconshowcase.models.NavigationItem
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.AttributeExtractor
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils

open class BottomBarShowcaseActivity:InternalBaseShowcaseActivity() {

    var bottomBar:AHBottomNavigation? = null

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar(true)
        setContentView(R.layout.activity_bottom_bar_showcase)
        initMainComponents(savedInstanceState)
        initBottomBar()
    }

    private fun initBottomBar() {
        bottomBar = findViewById(R.id.bottom_navigation) as AHBottomNavigation
        bottomBar?.defaultBackgroundColor = AttributeExtractor.getCardBgColorFrom(this)
        bottomBar?.isBehaviorTranslationEnabled = false
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

}