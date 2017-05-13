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

package jahirfiquitiva.libs.iconshowcase.utils.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import jahirfiquitiva.libs.iconshowcase.R;

/**
 * Helper class that provides easy access to the colors of the passing Context's theme
 */
public final class AttributeExtractor {

    private static final int[] PRIMARY_DARK = new int[]{R.attr.colorPrimaryDark};
    private static final int[] PRIMARY = new int[]{R.attr.colorPrimary};
    private static final int[] ACCENT = new int[]{R.attr.colorAccent};
    private static final int[] CARD_BG = new int[]{R.attr.cardColorBackground};

    /**
     * Extracts the colorPrimary color attribute of the passing Context's theme
     */
    @ColorInt
    public static int getPrimaryColorFrom(Context context) {
        return extractIntAttribute(context, PRIMARY);
    }

    /**
     * Extracts the colorPrimaryDark color attribute of the passing Context's theme
     */
    @ColorInt
    public static int getPrimaryDarkColorFrom(Context context) {
        return extractIntAttribute(context, PRIMARY_DARK);
    }

    /**
     * Extracts the colorAccent color attribute of the passing Context's theme
     */
    @ColorInt
    public static int getAccentColorFrom(Context context) {
        return extractIntAttribute(context, ACCENT);
    }

    /**
     * Extracts the cardBackgroundColor color attribute of the passing Context's theme
     */
    @ColorInt
    public static int getCardBgColorFrom(Context context) {
        return extractIntAttribute(context, CARD_BG);
    }

    /**
     * Extracts the drawable of the passing Context's theme
     */
    @ColorInt
    private static int extractIntAttribute(Context context, int[] attribute) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, attribute);
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static Drawable extractDrawable(Context context, @AttrRes int drawableAttributeId) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data,
                new int[]{drawableAttributeId});
        Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
    }
}