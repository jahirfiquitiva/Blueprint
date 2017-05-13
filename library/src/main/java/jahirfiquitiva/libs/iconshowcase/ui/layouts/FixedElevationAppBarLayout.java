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

package jahirfiquitiva.libs.iconshowcase.ui.layouts;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import jahirfiquitiva.libs.iconshowcase.R;


/**
 * @author Alexandre Piveteau
 */
public class FixedElevationAppBarLayout extends AppBarLayout {

    /**
     * The pixel elevation of the {@link FixedElevationAppBarLayout}.
     */
    private int fElevation;

    public FixedElevationAppBarLayout(Context context) {
        super(context);
        setupElevation();
    }

    public FixedElevationAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupElevation();
    }

    @Override
    public void setElevation(float ignored) {
        super.setElevation(fElevation);
    }

    /**
     * A method for setting up the elevation. Improves performance if only done once.
     */
    private void setupElevation() {
        fElevation = dpToPx(getResources().getInteger(R.integer.toolbar_elevation));
    }

    /**
     * A helper method for converting dps to pixels.
     *
     * @param dp The dp parameters
     * @return The pixel-converted result
     */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}