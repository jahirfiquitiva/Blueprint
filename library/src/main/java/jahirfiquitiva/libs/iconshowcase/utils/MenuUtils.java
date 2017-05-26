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

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.Menu;
import android.view.MenuItem;

public class MenuUtils {
    public static void changeOptionVisibility(Menu menu, int id, boolean visible) {
        if (menu == null) return;
        MenuItem item = menu.findItem(id);
        item.setVisible(visible);
    }

    public static void setOptionTitle(Menu menu, int id, String title) {
        if (menu == null) return;
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    public static void setOptionIcon(Menu menu, int id, @DrawableRes int iconRes) {
        if (menu == null) return;
        MenuItem item = menu.findItem(id);
        item.setIcon(iconRes);
    }

    public static void setOptionIcon(Menu menu, int id, Drawable icon) {
        if (menu == null) return;
        MenuItem item = menu.findItem(id);
        item.setIcon(icon);
    }
}