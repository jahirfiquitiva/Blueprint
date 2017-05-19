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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jahirfiquitiva.libs.iconshowcase.R;

@SuppressWarnings("SameParameterValue")
public class ColorUtils {

    @ColorInt
    public static int blendColors(@ColorInt int color1,
                                  @ColorInt int color2,
                                  @FloatRange(from = 0f, to = 1f) float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @SuppressWarnings("SameParameterValue")
    @ColorInt
    public static int adjustAlpha(@ColorInt int color,
                                  @FloatRange(from = 0.0, to = 1.0) float factor) {
        float a = Color.alpha(color) * factor;
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int changeAlpha(@ColorInt int color,
                                  @FloatRange(from = 0.0, to = 1.0) float newAlpha) {
        float a = 255 * newAlpha;
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float
            by) {
        if (by == 1f) return color;
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return (alpha << 24) + (0x00ffffff & Color.HSVToColor(hsv));
    }

    @ColorInt
    public static int stripAlpha(@ColorInt int color) {
        return Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
    }

    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    @ColorInt
    public static int lightenColor(@ColorInt int color) {
        return shiftColor(color, 1.1f);
    }

    public static boolean isLightColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        if (palette.getSwatches().size() > 0) {
            return isLightColor(palette);
        }
        return isLightColor(palette);
    }

    private static boolean isLightColor(Palette palette) {
        return isLightColor(ColorUtils.getPaletteSwatch(palette).getRgb());
    }

    public static boolean isLightColor(@ColorInt int color) {
        return getColorDarkness(color) < 0.475;
    }

    private static double getColorDarkness(@ColorInt int color) {
        if (color == Color.BLACK) return 1.0;
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return 0.0;
        return (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue
                (color)) / 255);
    }

    public static Palette.Swatch getPaletteSwatch(Bitmap bitmap) {
        //Test areas of 10 and 50*50
        return getPaletteSwatch(Palette.from(bitmap).resizeBitmapArea(50 * 50).generate());
    }

    public static Palette.Swatch getPaletteSwatch(Palette palette) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                return palette.getVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch();
            } else if (palette.getDarkVibrantSwatch() != null) {
                return palette.getDarkVibrantSwatch();
            } else if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch();
            } else if (palette.getLightVibrantSwatch() != null) {
                return palette.getLightVibrantSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch();
            } else if (!palette.getSwatches().isEmpty()) {
                return getPaletteSwatch(palette.getSwatches());
            }
        }
        return null;
    }

    private static Palette.Swatch getPaletteSwatch(List<Palette.Swatch> swatches) {
        if (swatches == null) return null;
        return Collections.max(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                int a = opt1 != null ? opt1.getPopulation() : 0;
                int b = opt2 != null ? opt2.getPopulation() : 0;
                return a - b;
            }
        });
    }

    @ColorRes
    public static int getToolbarTextColor(boolean darkTheme) {
        return darkTheme ? R.color.toolbar_text_dark : R.color.toolbar_text_light;
    }

    @ColorRes
    public static int getAccentColor(boolean darkTheme) {
        return darkTheme ? R.color.dark_theme_accent : R.color.light_theme_accent;
    }

    @ColorInt
    public static int getMaterialPrimaryTextColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#ffffffff") : Color.parseColor("#de000000");
    }

    @ColorInt
    public static int getMaterialSecondaryTextColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#b3ffffff") : Color.parseColor("#8a000000");
    }

    @ColorInt
    public static int getMaterialDisabledHintTextColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#80ffffff") : Color.parseColor("#61000000");
    }

    @ColorInt
    public static int getMaterialDividerColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#1fffffff") : Color.parseColor("#1f000000");
    }

    @ColorInt
    public static int getMaterialActiveIconsColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#ffffffff") : Color.parseColor("#8a000000");
    }

    @ColorInt
    public static int getMaterialInactiveIconsColor(boolean darkTheme) {
        return darkTheme ? Color.parseColor("#80ffffff") : Color.parseColor("#61000000");
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getDefaultRippleColor(@NonNull Context context, boolean useDarkRipple) {
        // Light ripple is actually translucent black, and vice versa
        return ContextCompat.getColor(
                context, useDarkRipple ? R.color.ripple_material_light : R.color
                        .ripple_material_dark);
    }

}