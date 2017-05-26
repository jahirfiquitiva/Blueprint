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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;

public class Preferences {

    private static final String
            PREFERENCES_NAME = "dashboard_preferences",
            THEME = "theme",
            COLORED_NAVBAR = "colored_navbar",
            WORKING_DASHBOARD = "working_dashboard",
            ASKED_PERMISSIONS = "asked_permissions",
            VERSION_CODE = "version_code",
            LAUNCHER_ICON = "launcher_icon_shown",
            WALLS_DOWNLOAD_FOLDER = "walls_download_folder",
            APPS_TO_REQUEST_LOADED = "apps_to_request_loaded",
            WALLS_LIST_LOADED = "walls_list_loaded",
            SETTINGS_MODIFIED = "settings_modified",
            ANIMATIONS_ENABLED = "animations_enabled",
            WALLPAPER_AS_TOOLBAR_HEADER = "wallpaper_as_toolbar_header",
            APPLY_DIALOG_DISMISSED = "apply_dialog_dismissed",
            WALLS_DIALOG_DISMISSED = "walls_dialog_dismissed",
            WALLS_COLUMNS_NUMBER = "walls_columns_number",
            MUZEI_REFRESH_INTERVAL = "muzei_refresh_interval",
            MUZEI_REFRESH_ON_WIFI_ONLY = "muzei_refresh_on_wifi_only";

    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDashboardWorking() {
        return getPrefs().getBoolean(WORKING_DASHBOARD, false);
    }

    public void setDashboardWorking(boolean enable) {
        getPrefs().edit().putBoolean(WORKING_DASHBOARD, enable).apply();
    }

    public boolean hasAskedPermissions() {
        return getPrefs().getBoolean(ASKED_PERMISSIONS, false);
    }

    @SuppressWarnings("SameParameterValue")
    public void setHasAskedPermissions(boolean asked) {
        getPrefs().edit().putBoolean(ASKED_PERMISSIONS, asked).apply();
    }

    public void setIconShown(boolean show) {
        getPrefs().edit().putBoolean(LAUNCHER_ICON, show).apply();
    }

    public boolean getLauncherIconShown() {
        return getPrefs().getBoolean(LAUNCHER_ICON, true);
    }

    public String getDownloadsFolder() {
        return getPrefs().getString(WALLS_DOWNLOAD_FOLDER,
                context != null
                        ? ResourceUtils.getString(context,
                        R.string.walls_save_location,
                        Environment.getExternalStorageDirectory().getAbsolutePath())
                        : Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/IconShowcase/Wallpapers");
    }

    public void setDownloadsFolder(String folder) {
        getPrefs().edit().putString(WALLS_DOWNLOAD_FOLDER, folder).apply();
    }

    public void setIfAppsToRequestLoaded(boolean loaded) {
        getPrefs().edit().putBoolean(APPS_TO_REQUEST_LOADED, loaded).apply();
    }

    public boolean didAppsToRequestLoad() {
        return getPrefs().getBoolean(APPS_TO_REQUEST_LOADED, false);
    }

    public boolean getWallsListLoaded() {
        return getPrefs().getBoolean(WALLS_LIST_LOADED, false);
    }

    public void setWallsListLoaded(boolean loaded) {
        getPrefs().edit().putBoolean(WALLS_LIST_LOADED, loaded).apply();
    }

    public boolean getSettingsModified() {
        return getPrefs().getBoolean(SETTINGS_MODIFIED, false);
    }

    public void setSettingsModified(boolean loaded) {
        getPrefs().edit().putBoolean(SETTINGS_MODIFIED, loaded).apply();
    }

    public boolean getAnimationsEnabled() {
        return getPrefs().getBoolean(ANIMATIONS_ENABLED, true);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        getPrefs().edit().putBoolean(ANIMATIONS_ENABLED, animationsEnabled).apply();
    }

    public boolean getWallpaperAsToolbarHeaderEnabled() {
        boolean value = context == null ||
                ResourceUtils.getBoolean(context, R.bool.show_user_wallpaper_in_toolbar_by_default);
        return getPrefs().getBoolean(WALLPAPER_AS_TOOLBAR_HEADER, value);
    }

    public void setWallpaperAsToolbarHeaderEnabled(boolean wallpaperAsToolbarHeader) {
        getPrefs().edit().putBoolean(WALLPAPER_AS_TOOLBAR_HEADER, wallpaperAsToolbarHeader).apply();
    }

    public boolean getApplyDialogDismissed() {
        return getPrefs().getBoolean(APPLY_DIALOG_DISMISSED, false);
    }

    public void setApplyDialogDismissed(boolean applyDialogDismissed) {
        getPrefs().edit().putBoolean(APPLY_DIALOG_DISMISSED, applyDialogDismissed).apply();
    }

    public boolean getWallsDialogDismissed() {
        return getPrefs().getBoolean(WALLS_DIALOG_DISMISSED, false);
    }

    public void setWallsDialogDismissed(boolean wallsDialogDismissed) {
        getPrefs().edit().putBoolean(WALLS_DIALOG_DISMISSED, wallsDialogDismissed).apply();
    }

    public int getWallsColumnsNumber() {
        return getPrefs().getInt(WALLS_COLUMNS_NUMBER,
                context.getResources().getInteger(R.integer.wallpapers_grid_width));
    }

    public void setWallsColumnsNumber(int columnsNumber) {
        getPrefs().edit().putInt(WALLS_COLUMNS_NUMBER, columnsNumber).apply();
    }

    public int getVersionCode() {
        return getPrefs().getInt(VERSION_CODE, 0);
    }

    public void setVersionCode(int versionCode) {
        getPrefs().edit().putInt(VERSION_CODE, versionCode).apply();
    }

    public int getMuzeiRefreshInterval() {
        return getPrefs().getInt(MUZEI_REFRESH_INTERVAL, 10);
    }

    public void setMuzeiRefreshInterval(int interval) {
        getPrefs().edit().putInt(MUZEI_REFRESH_INTERVAL, interval).apply();
    }

    public boolean getMuzeiRefreshOnWiFiOnly() {
        return getPrefs().getBoolean(MUZEI_REFRESH_ON_WIFI_ONLY, false);
    }

    public void setMuzeiRefreshOnWiFiOnly(boolean onWifiOnly) {
        getPrefs().edit().putBoolean(MUZEI_REFRESH_ON_WIFI_ONLY, onWifiOnly).apply();
    }

    public int getTheme() {
        return getPrefs().getInt(THEME, ResourceUtils.getInteger(context, R.integer.app_theme) - 1);
    }

    public void setTheme(int theme) {
        getPrefs().edit().putInt(THEME, theme).apply();
    }

    public void setColoredNavbar(boolean colored) {
        getPrefs().edit().putBoolean(COLORED_NAVBAR, colored).apply();
    }

    public boolean hasColoredNavbar() {
        return getPrefs().getBoolean(COLORED_NAVBAR, true);
    }

}