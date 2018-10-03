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

import android.os.Bundle
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kext.extensions.inactiveIconsColor

abstract class BottomNavigationBlueprintActivity : BaseBlueprintActivity() {
    
    private val bottomBar: AHBottomNavigation? by bind(R.id.bottom_navigation)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomBar()
    }
    
    private fun initBottomBar() {
        bottomBar?.let {
            it.accentColor = accentColor
            with(it) {
                defaultBackgroundColor = cardBackgroundColor
                inactiveColor = inactiveIconsColor
                // TODO: Enable this?
                isBehaviorTranslationEnabled = false
                isForceTint = true
                titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
                getNavigationItems().forEach {
                    addItem(AHBottomNavigationItem(getString(it.title), it.icon))
                }
                setOnTabSelectedListener { position, _ ->
                    return@setOnTabSelectedListener navigateToItem(
                        getNavigationItems()[position], true)
                }
                visible()
            }
        }
    }
    
    override fun navigateToItem(item: NavigationItem, fromClick: Boolean, force: Boolean): Boolean {
        if (!fromClick) bottomBar?.setCurrentItem(getNavigationItems().indexOf(item), false)
        if (!hasBottomNavigation()) {
            bottomBar?.hideBottomNavigation()
            bottomBar?.gone()
        }
        return super.navigateToItem(item, fromClick, force)
    }
    
    override fun hasBottomNavigation(): Boolean = !isIconsPicker
}