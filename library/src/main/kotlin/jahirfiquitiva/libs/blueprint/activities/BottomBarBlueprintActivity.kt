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
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.activities

import android.os.Bundle
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.activities.base.InternalBaseBlueprintActivity
import jahirfiquitiva.libs.blueprint.extensions.getAccentColor
import jahirfiquitiva.libs.blueprint.extensions.getCardBackgroundColor
import jahirfiquitiva.libs.blueprint.extensions.getInactiveIconsColor
import jahirfiquitiva.libs.blueprint.extensions.makeVisible

abstract class BottomBarBlueprintActivity:InternalBaseBlueprintActivity() {

    private lateinit var bottomBar:AHBottomNavigation

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomBar()
    }

    private fun initBottomBar() {
        bottomBar = findViewById(R.id.bottom_navigation)
        bottomBar.accentColor = getAccentColor()
        with(bottomBar) {
            defaultBackgroundColor = getCardBackgroundColor()
            isBehaviorTranslationEnabled = false
            inactiveColor = getInactiveIconsColor()
            isForceTint = true
            titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
            getNavigationItems().forEach {
                addItem(
                        AHBottomNavigationItem(getString(it.title), it.icon))
            }
            setOnTabSelectedListener { position, _ ->
                return@setOnTabSelectedListener navigateToItem(getNavigationItems()[position])
            }
            setCurrentItem(0, true)
            makeVisible()
        }
    }

    override fun onSaveInstanceState(outState:Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState:Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomBar.setCurrentItem(savedInstanceState?.getInt("currentItemId", 0) ?: 0, true)
    }

    override fun hasBottomBar():Boolean = true

}