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

package jahirfiquitiva.libs.blueprint.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import jahirfiquitiva.libs.blueprint.R

class Preferences(val context:Context) {
    private val PREFERENCES_NAME = "dashboard_preferences"
    private val THEME = "theme"
    private val COLORED_NAVBAR = "colored_navbar"
    private val WORKING_DASHBOARD = "working_dashboard"
    private val ASKED_PERMISSIONS = "asked_permissions"
    private val VERSION_CODE = "version_code"
    private val LAUNCHER_ICON = "launcher_icon_shown"
    private val WALLS_DOWNLOAD_FOLDER = "walls_download_folder"
    private val APPS_TO_REQUEST_LOADED = "apps_to_request_loaded"
    private val WALLS_LIST_LOADED = "walls_list_loaded"
    private val SETTINGS_MODIFIED = "settings_modified"
    private val ANIMATIONS_ENABLED = "animations_enabled"
    private val WALLPAPER_AS_TOOLBAR_HEADER = "wallpaper_as_toolbar_header"
    private val APPLY_DIALOG_DISMISSED = "apply_dialog_dismissed"
    private val WALLS_DIALOG_DISMISSED = "walls_dialog_dismissed"
    private val WALLS_COLUMNS_NUMBER = "walls_columns_number"
    private val MUZEI_REFRESH_INTERVAL = "muzei_refresh_interval"
    private val MUZEI_REFRESH_ON_WIFI_ONLY = "muzei_refresh_on_wifi_only"


    fun getPrefs():SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME,
                                                                    Context.MODE_PRIVATE)

    fun isDashboardWorking():Boolean = getPrefs().getBoolean(WORKING_DASHBOARD, false)

    fun setDashboardWorking(enable:Boolean) = getPrefs().edit().putBoolean(WORKING_DASHBOARD,
                                                                           enable).apply()

    fun hasAskedPermissions():Boolean = getPrefs().getBoolean(ASKED_PERMISSIONS, false)

    fun setHasAskedPermissions(asked:Boolean) = getPrefs().edit().putBoolean(ASKED_PERMISSIONS,
                                                                             asked).apply()

    fun setIconShown(show:Boolean) = getPrefs().edit().putBoolean(LAUNCHER_ICON, show).apply()

    fun getLauncherIconShown():Boolean = getPrefs().getBoolean(LAUNCHER_ICON, true)

    fun getDownloadsFolder():String = getPrefs().getString(WALLS_DOWNLOAD_FOLDER,
                                                           ResourceUtils.getString(context,
                                                                                   R.string.walls_save_location,
                                                                                   Environment.getExternalStorageDirectory().absolutePath))

    fun setDownloadsFolder(folder:String) = getPrefs().edit().putString(WALLS_DOWNLOAD_FOLDER,
                                                                        folder).apply()

    fun setIfAppsToRequestLoaded(loaded:Boolean) = getPrefs().edit().putBoolean(
            APPS_TO_REQUEST_LOADED, loaded).apply()

    fun didAppsToRequestLoad():Boolean = getPrefs().getBoolean(APPS_TO_REQUEST_LOADED, false)

    fun getWallsListLoaded():Boolean = getPrefs().getBoolean(WALLS_LIST_LOADED, false)

    fun setWallsListLoaded(loaded:Boolean) = getPrefs().edit().putBoolean(WALLS_LIST_LOADED,
                                                                          loaded).apply()

    fun getSettingsModified():Boolean = getPrefs().getBoolean(SETTINGS_MODIFIED, false)

    fun setSettingsModified(loaded:Boolean) = getPrefs().edit().putBoolean(SETTINGS_MODIFIED,
                                                                           loaded).apply()

    fun getAnimationsEnabled():Boolean = getPrefs().getBoolean(ANIMATIONS_ENABLED, true)

    fun setAnimationsEnabled(animationsEnabled:Boolean) = getPrefs().edit().putBoolean(
            ANIMATIONS_ENABLED, animationsEnabled).apply()

    fun getWallpaperAsToolbarHeaderEnabled():Boolean {
        val value = ResourceUtils.getBoolean(context,
                                             R.bool.show_user_wallpaper_in_toolbar_by_default)
        return getPrefs().getBoolean(WALLPAPER_AS_TOOLBAR_HEADER, value)
    }

    fun setWallpaperAsToolbarHeaderEnabled(
            wallpaperAsToolbarHeader:Boolean) = getPrefs().edit().putBoolean(
            WALLPAPER_AS_TOOLBAR_HEADER, wallpaperAsToolbarHeader).apply()

    fun getApplyDialogDismissed():Boolean = getPrefs().getBoolean(APPLY_DIALOG_DISMISSED, false)

    fun setApplyDialogDismissed(applyDialogDismissed:Boolean) = getPrefs().edit().putBoolean(
            APPLY_DIALOG_DISMISSED, applyDialogDismissed).apply()

    fun getWallsDialogDismissed():Boolean = getPrefs().getBoolean(WALLS_DIALOG_DISMISSED, false)

    fun setWallsDialogDismissed(wallsDialogDismissed:Boolean) = getPrefs().edit().putBoolean(
            WALLS_DIALOG_DISMISSED, wallsDialogDismissed).apply()

    fun getWallsColumnsNumber():Int = getPrefs().getInt(WALLS_COLUMNS_NUMBER,
                                                        context.resources.getInteger(
                                                                R.integer.wallpapers_grid_width))

    fun setWallsColumnsNumber(columnsNumber:Int) = getPrefs().edit().putInt(WALLS_COLUMNS_NUMBER,
                                                                            columnsNumber).apply()

    fun getVersionCode():Int = getPrefs().getInt(VERSION_CODE, 0)

    fun setVersionCode(versionCode:Int) = getPrefs().edit().putInt(VERSION_CODE,
                                                                   versionCode).apply()

    fun getMuzeiRefreshInterval():Int = getPrefs().getInt(MUZEI_REFRESH_INTERVAL, 10)

    fun setMuzeiRefreshInterval(interval:Int) = getPrefs().edit().putInt(MUZEI_REFRESH_INTERVAL,
                                                                         interval).apply()

    fun getMuzeiRefreshOnWiFiOnly():Boolean = getPrefs().getBoolean(MUZEI_REFRESH_ON_WIFI_ONLY,
                                                                    false)

    fun setMuzeiRefreshOnWiFiOnly(onWifiOnly:Boolean) = getPrefs().edit().putBoolean(
            MUZEI_REFRESH_ON_WIFI_ONLY, onWifiOnly).apply()

    fun getTheme():Int = getPrefs().getInt(THEME,
                                           ResourceUtils.getInteger(context,
                                                                    R.integer.app_theme) - 1)

    fun setTheme(theme:Int) = getPrefs().edit().putInt(THEME, theme).apply()

    fun setColoredNavbar(colored:Boolean) = getPrefs().edit().putBoolean(COLORED_NAVBAR,
                                                                         colored).apply()

    fun hasColoredNavbar():Boolean = getPrefs().getBoolean(COLORED_NAVBAR, true)

}