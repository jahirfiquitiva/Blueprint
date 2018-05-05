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
package jahirfiquitiva.libs.blueprint.ui.fragments

import android.content.ComponentName
import android.content.pm.PackageManager
import android.preference.Preference
import android.preference.SwitchPreference
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.configs
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.kauextensions.extensions.actv
import jahirfiquitiva.libs.kauextensions.extensions.ctxt
import jahirfiquitiva.libs.kuper.ui.fragments.SettingsFragment

@Suppress("DEPRECATION")
class SettingsFragment : SettingsFragment() {
    override fun initPreferences() {
        super.initPreferences()
        
        val toolbarHeaderPref = findPreference("wallpaper_in_icons_preview") as SwitchPreference
        toolbarHeaderPref.setOnPreferenceChangeListener { _, any ->
            val enable = any.toString().equals("true", true)
            if (enable != configs.wallpaperInIconsPreview)
                configs.wallpaperInIconsPreview = enable
            true
        }
        
        var componentName =
                (context?.packageName ?: "") + "." + getString(R.string.main_activity_name)
        val className: Class<*>? = try {
            Class.forName(componentName)
        } catch (e: Exception) {
            componentName = getString(R.string.main_activity_fullname)
            try {
                Class.forName(componentName)
            } catch (ignored: Exception) {
                null
            }
        }
        
        val hideIcon = findPreference("launcher_icon") as SwitchPreference
        
        if (className != null) {
            hideIcon.isChecked = !configs.launcherIconShown
            
            hideIcon.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val component = ComponentName(ctxt.packageName, componentName)
                        if (newValue.toString().equals("true", true)) {
                            clearDialog()
                            dialog = actv.buildMaterialDialog {
                                title(R.string.hideicon_dialog_title)
                                content(R.string.hideicon_dialog_content)
                                positiveText(android.R.string.yes)
                                negativeText(android.R.string.no)
                                onPositive { _, _ ->
                                    if (configs.launcherIconShown) {
                                        configs.launcherIconShown = false
                                        ctxt.packageManager.setComponentEnabledSetting(
                                                component,
                                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                PackageManager.DONT_KILL_APP)
                                        hideIcon.isChecked = true
                                    }
                                }
                                onNegative { _, _ ->
                                    hideIcon.isChecked = false
                                }
                                dismissListener {
                                    hideIcon.isChecked = false
                                }
                            }
                            dialog?.show()
                        } else {
                            if (!configs.launcherIconShown) {
                                configs.launcherIconShown = true
                                ctxt.packageManager.setComponentEnabledSetting(
                                        component,
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP)
                            }
                        }
                        true
                    }
        } else {
            hideIcon.isEnabled = false
        }
    }
}