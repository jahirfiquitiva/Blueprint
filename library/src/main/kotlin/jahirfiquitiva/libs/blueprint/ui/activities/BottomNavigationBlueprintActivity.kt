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
import ca.allanwang.kau.utils.visible
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.ui.activities.base.InternalBaseBlueprintActivity
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kauextensions.extensions.inactiveIconsColor

abstract class BottomNavigationBlueprintActivity:InternalBaseBlueprintActivity() {
    
    internal lateinit var bottomBar:AHBottomNavigation
    
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomBar()
    }
    
    private fun initBottomBar() {
        bottomBar = findViewById(R.id.bottom_navigation)
        bottomBar.accentColor = accentColor
        with(bottomBar) {
            defaultBackgroundColor = cardBackgroundColor
            inactiveColor = inactiveIconsColor
            // TODO: Enable this?
            // isBehaviorTranslationEnabled = false
            isForceTint = true
            titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
            getNavigationItems().forEach {
                addItem(AHBottomNavigationItem(getString(it.title), it.icon))
            }
            setOnTabSelectedListener { position, _ ->
                return@setOnTabSelectedListener navigateToItem(getNavigationItems()[position])
            }
            setCurrentItem(0, true)
            visible()
        }
    }
    
    override fun internalNavigateToItem(item:NavigationItem):Boolean {
        bottomBar.setCurrentItem(item.id, false)
        return super.internalNavigateToItem(item)
    }
    
    override fun hasBottomNavigation():Boolean = true
}