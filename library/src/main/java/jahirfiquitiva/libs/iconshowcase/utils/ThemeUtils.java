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

package jahirfiquitiva.libs.iconshowcase.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences;

@SuppressWarnings("ResourceAsColor")
public class ThemeUtils {

    public static final int LIGHT = 0;
    public static final int DARK = 1;
    public static final int AMOLED = 2;
    public static final int AUTO_DARK = 3;
    public static final int AUTO_AMOLED = 4;

    private static int currentTheme = 0;
    private static boolean coloredNavbar;

    @ColorRes
    public static int darkOrLight(@ColorRes int dark, @ColorRes int light) {
        return isDarkTheme() ? dark : light;
    }

    @ColorInt
    public static int darkOrLight(@NonNull Context context, @ColorRes int dark,
                                  @ColorRes int light) {
        return ContextCompat.getColor(context, darkOrLight(dark, light));
    }

    public static int getCurrentTheme() {
        return currentTheme;
    }

    public static boolean isDarkTheme() {
        return currentTheme == DARK || currentTheme == AMOLED;
    }

    public static boolean hasColoredNavbar() {
        return coloredNavbar;
    }

    public static void setThemeTo(Activity activity) {
        int enterAnimation = android.R.anim.fade_in;
        int exitAnimation = android.R.anim.fade_out;
        activity.overridePendingTransition(enterAnimation, exitAnimation);

        final Preferences prefs = new Preferences(activity);
        int prefTheme = prefs.getTheme();

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        switch (prefTheme) {
            default:
            case LIGHT:
                activity.setTheme(R.style.AppTheme);
                break;
            case DARK:
                activity.setTheme(R.style.AppThemeDark);
                break;
            case AMOLED:
                activity.setTheme(R.style.AppThemeAmoled);
                break;
            case AUTO_DARK:
                if (timeOfDay >= 7 && timeOfDay < 20) {
                    activity.setTheme(R.style.AppTheme);
                    currentTheme = LIGHT;
                } else {
                    activity.setTheme(R.style.AppThemeDark);
                    currentTheme = DARK;
                }
                break;
            case AUTO_AMOLED:
                if (timeOfDay >= 7 && timeOfDay < 20) {
                    activity.setTheme(R.style.AppTheme);
                    currentTheme = LIGHT;
                } else {
                    activity.setTheme(R.style.AppThemeAmoled);
                    currentTheme = AMOLED;
                }
                break;
        }
        setNavbarColorTo(activity, prefs.hasColoredNavbar());
    }

    private static void setNavbarColorTo(Activity activity, boolean colorEnabled) {
        coloredNavbar = colorEnabled;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        activity.getWindow().setNavigationBarColor(colorEnabled ? isDarkTheme() ?
                ContextCompat.getColor(activity, R.color.dark_theme_navigation_bar) :
                ContextCompat.getColor(activity, R.color.light_theme_navigation_bar) :
                ContextCompat.getColor(activity, android.R.color.black));
    }

    public static void restartActivity(Activity activity) {
        Intent intent = activity.getIntent();
        intent.removeCategory(Intent.CATEGORY_LAUNCHER);
        activity.startActivity(intent);
        activity.finish();
    }

}