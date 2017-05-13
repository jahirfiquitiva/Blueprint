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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

public class ResourceUtils {

    public static String getString(Context context, @StringRes int res) {
        return context.getResources().getString(res);
    }

    public static String getString(Context context, @StringRes int res, Object... args) {
        return context.getResources().getString(res, args);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int res) {
        return ContextCompat.getDrawable(context, res);
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }

    public static boolean getBoolean(Context context, @BoolRes int res) {
        try {
            return context.getResources().getBoolean(res);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getBoolean(Context context, @BoolRes int res, boolean defaultValue) {
        try {
            return getBoolean(context, res);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInteger(Context context, @IntegerRes int res) {
        try {
            return context.getResources().getInteger(res);
        } catch (Exception e) {
            return -1;
        }
    }
}