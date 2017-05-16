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
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v13.view.ViewCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.TintableBackgroundView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;

import jahirfiquitiva.libs.iconshowcase.R;
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;

public class TintUtils {

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @Nullable
    public static Drawable createTintedDrawable(@Nullable Drawable drawable, @ColorInt int color) {
        if (drawable == null) return null;
        drawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    // This returns a NEW Drawable because of the mutate() call. The mutate() call is necessary
    // because Drawables with the same resource have shared states otherwise.
    @CheckResult
    @Nullable
    public static Drawable createTintedDrawable(
            @Nullable Drawable drawable, @NonNull ColorStateList sl) {
        if (drawable == null) return null;
        drawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(drawable, sl);
        return drawable;
    }

    public static void setCursorTint(@NonNull EditText editText, @ColorInt int color) {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(editText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[0] = createTintedDrawable(drawables[0], color);
            drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
            drawables[1] = createTintedDrawable(drawables[1], color);
            fCursorDrawable.set(editor, drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("PrivateResource")
    public static void setTintAuto(final @NonNull View view, final @ColorInt int color,
                                   boolean background, final boolean isDark) {
        if (!background) {
            if (view instanceof RadioButton) {
                setTint((RadioButton) view, color, isDark);
            } else if (view instanceof SeekBar) {
                setTint((SeekBar) view, color, isDark);
            } else if (view instanceof ProgressBar) {
                setTint((ProgressBar) view, color);
            } else if (view instanceof EditText) {
                setTint((EditText) view, color, isDark);
            } else if (view instanceof CheckBox) {
                setTint((CheckBox) view, color, isDark);
            } else if (view instanceof ImageView) {
                setTint((ImageView) view, color);
            } else {
                if (!(view instanceof Switch) && !(view instanceof SwitchCompat))
                    background = true;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && !background
                    && view.getBackground() instanceof RippleDrawable) {
                // Ripples for the above views (e.g. when you tap and hold a switch or checkbox)
                RippleDrawable rd = (RippleDrawable) view.getBackground();
                final int unchecked = ContextCompat.getColor(view.getContext(),
                        isDark ? R.color.ripple_material_dark :
                                R.color.ripple_material_light);
                final int checked = ColorUtils.adjustAlpha(color, 0.4f);
                final ColorStateList sl =
                        new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_activated,
                                                -android.R.attr.state_checked},
                                        new int[]{android.R.attr.state_activated},
                                        new int[]{android.R.attr.state_checked}
                                },
                                new int[]{unchecked, checked, checked});
                rd.setColor(sl);
            }
        }
        if (background) {
            // Need to tint the background of a view
            if (view instanceof FloatingActionButton || view instanceof Button) {
                setTintSelector(view, color, false, isDark);
            } else if (view.getBackground() != null) {
                Drawable drawable = view.getBackground();
                if (drawable != null) {
                    if (view instanceof TextInputEditText) {
                        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    } else {
                        drawable = createTintedDrawable(drawable, color);
                        setBackgroundCompat(view, drawable);
                    }
                }
            }
        }
    }

    public static void setTint(@NonNull RadioButton radioButton, @ColorInt int color,
                               boolean useDarker) {
        ColorStateList sl = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_enabled,
                                -android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled,
                                android.R.attr.state_checked}
                },
                new int[]{
                        // Radio button includes own alpha for disabled state
                        ColorUtils.stripAlpha(ColorUtils.getMaterialDisabledHintTextColor(useDarker)),
                        ColorUtils.getMaterialSecondaryTextColor(useDarker),
                        color
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.setButtonTintList(sl);
        } else {
            @SuppressLint("PrivateResource")
            Drawable d = createTintedDrawable(
                    ContextCompat.getDrawable(
                            radioButton.getContext(), R.drawable.abc_btn_radio_material),
                    sl);
            radioButton.setButtonDrawable(d);
        }
    }

    public static void setTint(@NonNull SeekBar seekBar, @ColorInt int color, boolean useDarker) {
        final ColorStateList s1 = getDisabledColorStateList(color,
                ColorUtils.getMaterialDisabledHintTextColor(useDarker));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.setThumbTintList(s1);
            seekBar.setProgressTintList(s1);
        } else {
            Drawable progressDrawable = createTintedDrawable(seekBar.getProgressDrawable(), s1);
            seekBar.setProgressDrawable(progressDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Drawable thumbDrawable = createTintedDrawable(seekBar.getThumb(), s1);
                seekBar.setThumb(thumbDrawable);
            }
        }
    }

    public static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color) {
        setTint(progressBar, color, false);
    }

    private static void setTint(@NonNull ProgressBar progressBar, @ColorInt int color,
                                boolean skipIndeterminate) {
        ColorStateList sl = ColorStateList.valueOf(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(sl);
            progressBar.setSecondaryProgressTintList(sl);
            if (!skipIndeterminate) {
                progressBar.setIndeterminateTintList(sl);
            }
        } else {
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
            if (!skipIndeterminate && progressBar.getIndeterminateDrawable() != null) {
                progressBar.getIndeterminateDrawable().setColorFilter(color, mode);
            }
            if (progressBar.getProgressDrawable() != null) {
                progressBar.getProgressDrawable().setColorFilter(color, mode);
            }
        }
    }

    public static void setTint(@NonNull EditText editText, @ColorInt int color, boolean useDarker) {
        final ColorStateList editTextColorStateList =
                new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled},
                                new int[]{
                                        android.R.attr.state_enabled,
                                        -android.R.attr.state_pressed,
                                        -android.R.attr.state_focused
                                },
                                new int[]{}
                        },
                        new int[]{
                                ColorUtils.getMaterialDisabledHintTextColor(useDarker),
                                ColorUtils.getMaterialSecondaryTextColor(useDarker),
                                color
                        });
        if (editText instanceof TintableBackgroundView) {
            ViewCompat.setBackgroundTintList(editText, editTextColorStateList);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintList(editTextColorStateList);
        }
        setCursorTint(editText, color);
    }

    public static void setTint(@NonNull CheckBox box, @ColorInt int color, boolean useDarker) {
        ColorStateList sl = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_enabled, -android.R.attr
                                .state_checked},
                        new int[]{android.R.attr.state_enabled, android.R.attr
                                .state_checked}
                },
                new int[]{
                        ColorUtils.getMaterialDisabledHintTextColor(useDarker),
                        ColorUtils.getMaterialSecondaryTextColor(useDarker),
                        color
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            box.setButtonTintList(sl);
        } else {
            @SuppressLint("PrivateResource")
            Drawable drawable =
                    createTintedDrawable(
                            ContextCompat.getDrawable(box.getContext(), R.drawable
                                    .abc_btn_check_material), sl);
            box.setButtonDrawable(drawable);
        }
    }

    public static void setTint(@NonNull ImageView image, @ColorInt int color) {
        image.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @NonNull
    public static ColorStateList getDisabledColorStateList(@ColorInt int normal, @ColorInt int
            disabled) {
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled}, new int[]{android.R.attr
                        .state_enabled}
                },
                new int[]{disabled, normal});
    }

    @SuppressWarnings("deprecation")
    public static void setTintSelector(@NonNull View view, @ColorInt final int color,
                                       final boolean darker, final boolean useDarkTheme) {
        final boolean isLightColor = ColorUtils.isLightColor(color);
        final int disabled = ColorUtils.getMaterialDisabledHintTextColor(useDarkTheme);
        final int pressed = ColorUtils.shiftColor(color, darker ? 0.9f : 1.1f);
        final int activated = ColorUtils.shiftColor(color, darker ? 1.1f : 0.9f);
        final int rippleColor = ColorUtils.getDefaultRippleColor(view.getContext(), isLightColor);
        final int textColor = ColorUtils.getMaterialPrimaryTextColor(isLightColor);

        final ColorStateList sl;
        if (view instanceof Button) {
            sl = getDisabledColorStateList(color, disabled);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && view.getBackground() instanceof RippleDrawable) {
                RippleDrawable rd = (RippleDrawable) view.getBackground();
                rd.setColor(ColorStateList.valueOf(rippleColor));
            }

            // Disabled text color state for buttons, may get overridden later by ATE tags
            final Button button = (Button) view;
            button.setTextColor(
                    getDisabledColorStateList(
                            textColor,
                            ColorUtils.getMaterialDisabledHintTextColor(useDarkTheme)));
        } else if (view instanceof FloatingActionButton) {
            // FloatingActionButton doesn't support disabled state?
            sl =
                    new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_pressed}, new int[]{android.R
                                    .attr.state_pressed}
                            },
                            new int[]{color, pressed});

            final FloatingActionButton fab = (FloatingActionButton) view;
            fab.setRippleColor(rippleColor);
            fab.setBackgroundTintList(sl);
            if (fab.getDrawable() != null)
                fab.setImageDrawable(createTintedDrawable(fab.getDrawable(), textColor));
            return;
        } else {
            sl = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled,
                                    android.R.attr.state_pressed},
                            new int[]{android.R.attr.state_enabled,
                                    android.R.attr.state_activated},
                            new int[]{android.R.attr.state_enabled,
                                    android.R.attr.state_checked}
                    },
                    new int[]{disabled, color, pressed, activated, activated});
        }

        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable = createTintedDrawable(drawable, sl);
            setBackgroundCompat(view, drawable);
        }

        if (view instanceof TextView && !(view instanceof Button)) {
            final TextView tv = (TextView) view;
            tv.setTextColor(
                    getDisabledColorStateList(
                            textColor,
                            ColorUtils.getMaterialDisabledHintTextColor(isLightColor)));
        }
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundCompat(@NonNull View view, @Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

}