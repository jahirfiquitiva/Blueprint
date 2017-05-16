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

package jahirfiquitiva.libs.iconshowcase.activities.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import com.afollestad.aesthetic.Aesthetic;

import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils;
import jahirfiquitiva.libs.iconshowcase.utils.preferences.Preferences;

public class AestheticThemedActivity extends AestheticThemedBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Aesthetic.isFirstTime()) {
            Aesthetic.get()
                    .activityTheme(getDefaultTheme())
                    .apply();
        }
    }

    @StyleRes
    private int getDefaultTheme() {
        Preferences mPrefs = new Preferences(this);
        return ThemeUtils.getTheme(mPrefs.getTheme());
    }
}