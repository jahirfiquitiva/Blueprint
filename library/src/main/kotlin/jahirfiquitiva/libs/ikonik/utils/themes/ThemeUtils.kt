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
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.utils.themes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import jahirfiquitiva.libs.ikonik.R
import jahirfiquitiva.libs.ikonik.utils.ColorUtils
import jahirfiquitiva.libs.ikonik.utils.Preferences
import jahirfiquitiva.libs.ikonik.utils.ResourceUtils
import java.util.Calendar

object ThemeUtils {
    val LIGHT = 0
    val DARK = 1
    val AMOLED = 2
    val AUTO_DARK = 3
    val AUTO_AMOLED = 4

    private var currentTheme = 0
    private var coloredNavbar:Boolean = false

    @ColorRes
    fun darkOrLight(@ColorRes dark:Int,
                    @ColorRes light:Int):Int = if (isDarkTheme()) dark else light

    @ColorInt
    fun darkOrLight(context:Context, @ColorRes dark:Int, @ColorRes light:Int):Int =
            ContextCompat.getColor(context, darkOrLight(dark, light))

    fun getCurrentTheme():Int = currentTheme

    fun isDarkTheme():Boolean = currentTheme == DARK || currentTheme == AMOLED

    fun hasColoredNavbar():Boolean = coloredNavbar

    fun setThemeTo(activity:AppCompatActivity) {
        val enterAnimation = android.R.anim.fade_in
        val exitAnimation = android.R.anim.fade_out
        activity.overridePendingTransition(enterAnimation, exitAnimation)
        val prefs = Preferences(activity)
        currentTheme = prefs.getTheme()
        activity.setTheme(getTheme(currentTheme))
        setNavbarColorTo(activity, prefs.hasColoredNavbar())
    }

    @StyleRes
    fun getTheme(prefTheme:Int):Int {
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        when (prefTheme) {
            LIGHT -> return R.style.AppTheme
            DARK -> return R.style.AppThemeDark
            AMOLED -> return R.style.AppThemeAmoled
            AUTO_DARK -> return if (hourOfDay in 7..18) R.style.AppTheme else R.style.AppThemeDark
            AUTO_AMOLED -> return if (hourOfDay in 7..18) R.style.AppTheme else R.style.AppThemeAmoled
            else -> return R.style.AppTheme
        }
    }

    private fun setNavbarColorTo(activity:AppCompatActivity,
                                 colorEnabled:Boolean) {
        coloredNavbar = colorEnabled
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        activity.window.navigationBarColor = if (getCurrentTheme() == AMOLED)
            ResourceUtils.getColor(activity, android.R.color.black)
        else if (colorEnabled)
            if (getCurrentTheme() == DARK)
                ResourceUtils.getColor(activity, R.color.dark_theme_navigation_bar)
            else
                ResourceUtils.getColor(activity, R.color.light_theme_navigation_bar)
        else
            ResourceUtils.getColor(activity, android.R.color.black)
    }

    fun setStatusBarModeTo(activity:AppCompatActivity) {
        val lightStatusBar = ColorUtils.isLightColor(
                AttributeExtractor.getPrimaryDarkColorFrom(activity))
        setStatusBarModeTo(activity, lightStatusBar)
    }

    fun setStatusBarModeTo(activity:AppCompatActivity, lightMode:Boolean) {
        val view = activity.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            if (lightMode) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            view.systemUiVisibility = flags
        }
    }

    fun restartActivity(activity:Activity) {
        val intent = activity.intent
        intent.removeCategory(Intent.CATEGORY_LAUNCHER)
        activity.startActivity(intent)
        activity.finish()
    }
}