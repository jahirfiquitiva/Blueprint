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

package jahirfiquitiva.libs.iconshowcase.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils;
import jahirfiquitiva.libs.iconshowcase.utils.themes.TintUtils;
import jahirfiquitiva.libs.iconshowcase.utils.themes.ToolbarThemer;

public class ThemedToolbar extends Toolbar {

    private int titleIconColor;
    private int backgroundResId;

    public ThemedToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public ThemedToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThemedToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (context == null) return;
        if (attrs != null) {
            int[] attrsArray = new int[]{android.R.attr.background};
            TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
            this.backgroundResId = ta.getResourceId(0, 0);
            ta.recycle();
            setNewColors(context);
        }
    }

    private void setNewColors(Context context) {
        try {
            int color = ContextCompat.getColor(context, backgroundResId);
            setBackgroundColor(color);
            this.titleIconColor = ColorUtils.getMaterialActiveIconsColor(
                    !ColorUtils.isLightColor(color));
            setTitleTextColor(titleIconColor);
            ToolbarThemer.setOverflowButtonColor(this, titleIconColor);
            if (getNavigationIcon() != null) {
                setNavigationIcon(getNavigationIcon());
            }
            ToolbarThemer.tintToolbarMenu(this, getMenu(), titleIconColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        super.setNavigationIcon(TintUtils.createTintedDrawable(icon, titleIconColor));
    }

    public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
        super.setNavigationIcon(TintUtils.createTintedDrawable(icon, color));
    }

}