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

package jahirfiquitiva.libs.iconshowcase.utils.themes;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;

public class ToolbarThemer {

    public static void tintToolbarMenu(@NonNull Toolbar toolbar, @NonNull Menu menu,
                                       @ColorInt int titleIconColor) {
        // The collapse icon displays when action views are expanded (e.g. SearchView)
        try {
            final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
            field.setAccessible(true);
            Drawable collapseIcon = (Drawable) field.get(toolbar);
            if (collapseIcon != null)
                field.set(toolbar, TintUtils.createTintedDrawable(collapseIcon, titleIconColor));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Theme menu action views
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getActionView() instanceof SearchView) {
                themeSearchView(titleIconColor, (SearchView) item.getActionView());
            }
        }
    }

    public static void setOverflowButtonColor(@NonNull final Toolbar toolbar,
                                              final @ColorInt int color) {
        @SuppressLint("PrivateResource")
        final String overflowDescription =
                toolbar.getResources().getString(R.string.abc_action_menu_overflow_description);
        final ArrayList<View> outViews = new ArrayList<>();
        toolbar.findViewsWithText(
                outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        if (outViews.isEmpty()) return;
        final AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
        overflow.setImageDrawable(TintUtils.createTintedDrawable(overflow.getDrawable(), color));
    }

    private static void themeSearchView(int tintColor, SearchView view) {
        final Class<?> cls = view.getClass();
        try {
            final Field mSearchSrcTextViewField = cls.getDeclaredField("mSearchSrcTextView");
            mSearchSrcTextViewField.setAccessible(true);
            final EditText mSearchSrcTextView = (EditText) mSearchSrcTextViewField.get(view);
            mSearchSrcTextView.setTextColor(tintColor);
            mSearchSrcTextView.setHintTextColor(
                    ColorUtils.getMaterialDisabledHintTextColor(!ColorUtils.isLightColor
                            (tintColor)));
            TintUtils.setCursorTint(mSearchSrcTextView, tintColor);

            Field field = cls.getDeclaredField("mSearchButton");
            tintImageView(view, field, tintColor);
            field = cls.getDeclaredField("mGoButton");
            tintImageView(view, field, tintColor);
            field = cls.getDeclaredField("mCloseButton");
            tintImageView(view, field, tintColor);
            field = cls.getDeclaredField("mVoiceButton");
            tintImageView(view, field, tintColor);

            field = cls.getDeclaredField("mSearchPlate");
            field.setAccessible(true);
            TintUtils.setTintAuto((View) field.get(view), tintColor, true,
                    !ColorUtils.isLightColor(tintColor));

            field = cls.getDeclaredField("mSearchHintIcon");
            field.setAccessible(true);
            field.set(view, TintUtils.createTintedDrawable((Drawable) field.get(view), tintColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tintImageView(Object target, Field field, int tintColor) throws Exception {
        field.setAccessible(true);
        final ImageView imageView = (ImageView) field.get(target);
        if (imageView.getDrawable() != null) {
            imageView.setImageDrawable(
                    TintUtils.createTintedDrawable(imageView.getDrawable(), tintColor));
        }
    }
}