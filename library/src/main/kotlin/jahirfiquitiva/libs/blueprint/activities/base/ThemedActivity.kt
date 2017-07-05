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

package jahirfiquitiva.libs.blueprint.activities.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AutoSwitchMode
import jahirfiquitiva.libs.blueprint.extensions.*

open class ThemedActivity:AppCompatActivity() {
    var lastTheme = 0
    var coloredNavbar = false

    override fun onCreate(savedInstanceState:Bundle?) {
        Aesthetic.attach(this)
        super.onCreate(savedInstanceState)
        if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .colorPrimary(getPrimaryColor(usesDarkTheme))
                    .colorPrimaryDark(getPrimaryDarkColor(usesDarkTheme))
                    .colorAccent(getAccentColor(usesDarkTheme))
                    .colorStatusBar(getAccentColor(usesDarkTheme))
                    .lightStatusBarMode(
                            if (getPrimaryDarkColor(usesDarkTheme).isColorLight())
                                AutoSwitchMode.ON else AutoSwitchMode.OFF)
                    .colorNavigationBar(navbarColor)
                    .textColorPrimary(getPrimaryTextColor(usesDarkTheme))
                    .textColorPrimaryInverse(getPrimaryTextColorInverse(usesLightTheme))
                    .textColorSecondary(getSecondaryTextColor(usesDarkTheme))
                    .textColorSecondaryInverse(getSecondaryTextColorInverse(usesLightTheme))
                    .colorIconTitleActive(getActiveIconsColor(usesDarkTheme))
                    .colorIconTitleInactive(getInactiveIconsColor(usesDarkTheme))
                    .apply()
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        Aesthetic.resume(this)
        if (lastTheme != konfigs.currentTheme || coloredNavbar != konfigs.hasColoredNavbar)
            restart()
    }

    override fun onPause() {
        Aesthetic.pause(this)
        super.onPause()
    }

    override fun onPostCreate(savedInstanceState:Bundle?) {
        super.onPostCreate(savedInstanceState)
        lastTheme = konfigs.currentTheme
        coloredNavbar = konfigs.hasColoredNavbar
    }
}