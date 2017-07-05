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

package jahirfiquitiva.libs.blueprint.extensions

import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.activities.base.ThemedActivity
import jahirfiquitiva.libs.blueprint.utils.AMOLED
import jahirfiquitiva.libs.blueprint.utils.AUTO_AMOLED
import jahirfiquitiva.libs.blueprint.utils.AUTO_DARK
import jahirfiquitiva.libs.blueprint.utils.DARK
import jahirfiquitiva.libs.blueprint.utils.LIGHT
import java.util.*

fun ThemedActivity.setCustomTheme() {
    val enterAnimation = android.R.anim.fade_in
    val exitAnimation = android.R.anim.fade_out
    overridePendingTransition(enterAnimation, exitAnimation)
    setTheme(getCustomTheme())
    setNavbarColor(getNavbarColor())
}

fun ThemedActivity.isLightTheme():Boolean = !isDarkTheme()

fun ThemedActivity.isDarkTheme():Boolean {
    val c = Calendar.getInstance()
    val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
    when (konfigs.currentTheme) {
        LIGHT -> return false
        DARK, AMOLED -> return true
        AUTO_DARK, AUTO_AMOLED -> return hourOfDay !in 7..18
        else -> return false
    }
}

fun ThemedActivity.getCustomTheme():Int {
    val c = Calendar.getInstance()
    val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
    when (konfigs.currentTheme) {
        LIGHT -> return R.style.AppTheme
        DARK -> return R.style.AppThemeDark
        AMOLED -> return R.style.AppThemeAmoled
        AUTO_DARK -> return if (hourOfDay in 7..18) R.style.AppTheme else R.style.AppThemeDark
        AUTO_AMOLED -> return if (hourOfDay in 7..18) R.style.AppTheme else R.style.AppThemeAmoled
        else -> return R.style.AppTheme
    }
}

fun ThemedActivity.getNavbarColor():Int {
    if (konfigs.currentTheme == AMOLED)
        return getColorFromRes(android.R.color.black)
    else if (konfigs.hasColoredNavbar)
        if (konfigs.currentTheme == DARK)
            return getColorFromRes(R.color.dark_theme_navigation_bar)
        else
            return getColorFromRes(R.color.light_theme_navigation_bar)
    else
        return getColorFromRes(android.R.color.black)
}