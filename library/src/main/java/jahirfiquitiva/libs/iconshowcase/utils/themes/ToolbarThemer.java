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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.callbacks.CollapsingToolbarCallback;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils;

public class ToolbarThemer {

    public static void updateToolbarColors(@NonNull AppCompatActivity activity,
                                           @NonNull Toolbar toolbar,
                                           int offset) {
        final int defaultIconsColor = ResourceUtils.getColor(activity, android.R.color.white);
        double ratio = CoreUtils.round(offset / 255.0, 1);
        if (ratio > 1) ratio = 1;
        else if (ratio < 0) ratio = 0;
        int rightIconsColor = ColorUtils.blendColors(defaultIconsColor,
                ColorUtils.getMaterialActiveIconsColor(
                        ColorUtils.isDarkColor(
                                AttributeExtractor.getPrimaryColorFrom(activity))), (float) ratio);
        tintToolbar(toolbar, rightIconsColor);
        updateStatusBarStyle(activity, ratio > 0.7
                ? CollapsingToolbarCallback.State.COLLAPSED
                : CollapsingToolbarCallback.State.EXPANDED);
    }

    public static void tintToolbar(Toolbar toolbar, final int toolbarIconsColor) {
        if (toolbar == null) return;

        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(toolbarIconsColor,
                PorterDuff.Mode.SRC_IN);

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            if (v instanceof ImageButton) {
                ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
            }

            if (v instanceof ActionMenuView) {
                for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
                    //Step 2: Changing the color of any ActionMenuViews - icons that are not back
                    // button, nor text, nor overflow menu icon.
                    final View innerView = ((ActionMenuView) v).getChildAt(j);
                    if (innerView instanceof ActionMenuItemView) {
                        for (int k = 0; k < ((ActionMenuItemView) innerView).getCompoundDrawables
                                ().length; k++) {
                            if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] !=
                                    null) {
                                final int finalK = k;
                                innerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ActionMenuItemView) innerView).getCompoundDrawables()
                                                [finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Changing the color of title and subtitle.
        toolbar.setTitleTextColor(ColorUtils.getMaterialPrimaryTextColor(
                !ColorUtils.isLightColor(
                        AttributeExtractor.getPrimaryColorFrom(toolbar.getContext()))
        ));
        toolbar.setSubtitleTextColor(ColorUtils.getMaterialSecondaryTextColor(
                !ColorUtils.isLightColor(
                        AttributeExtractor.getPrimaryColorFrom(toolbar.getContext()))
        ));

        // Step 4: Tint toolbar menu.
        tintToolbarMenu(toolbar, toolbar.getMenu(), toolbarIconsColor);

        // Step 5: Change the color of overflow menu icon.
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            toolbar.setOverflowIcon(TintUtils.createTintedDrawable(drawable, toolbarIconsColor));
        }
        // setOverflowButtonColor(toolbar, toolbarIconsColor);
    }

    public static void tintToolbarMenu(@NonNull Toolbar toolbar, @NonNull Menu menu,
                                       @ColorInt int iconsColor) {
        // The collapse icon displays when action views are expanded (e.g. SearchView)
        try {
            final Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
            field.setAccessible(true);
            Drawable collapseIcon = (Drawable) field.get(toolbar);
            if (collapseIcon != null)
                field.set(toolbar, TintUtils.createTintedDrawable(collapseIcon, iconsColor));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Theme menu action views
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getActionView() instanceof SearchView) {
                themeSearchView(iconsColor, (SearchView) item.getActionView());
            }
        }

        // Display icons for easy UI understanding
        /*
        try {
            Class<?> MenuBuilder = menu.getClass();
            Method setOptionalIconsVisible = MenuBuilder.getDeclaredMethod
                    ("setOptionalIconsVisible", boolean.class);
            if (!setOptionalIconsVisible.isAccessible())
                setOptionalIconsVisible.setAccessible(true);
            setOptionalIconsVisible.invoke(menu, true);
        } catch (Exception ignored) {
        }
        */
    }

    private static void setOverflowButtonColor(@NonNull final Toolbar toolbar,
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

    private static void updateStatusBarStyle(@NonNull AppCompatActivity activity,
                                             CollapsingToolbarCallback.State state) {
        if (state == CollapsingToolbarCallback.State.COLLAPSED) {
            ThemeUtils.setStatusBarModeTo(activity);
        } else {
            ThemeUtils.setStatusBarModeTo(activity, false);
        }
    }

}