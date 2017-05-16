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

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import jahirfiquitiva.libs.iconshowcase.R;

public enum DrawerItem {

    HOME("Home", 0, R.string.section_home, R.drawable.ic_home),
    PREVIEW("Previews", 1, R.string.section_icons, R.drawable.ic_previews),
    WALLPAPERS("Wallpapers", 2, R.string.section_wallpapers, R.drawable.ic_wallpapers),
    REQUESTS("Requests", 3, R.string.section_icon_request, R.drawable.ic_request),
    APPLY("Apply", 4, R.string.section_apply, R.drawable.ic_apply),
    FAQS("FAQs", 5, R.string.faqs_section, R.drawable.ic_questions),
    ZOOPER("Zooper", 6, R.string.zooper_section_title, R.drawable.ic_zooper_kustom),
    KUSTOM("Kustom", 7, R.string.section_kustom, R.drawable.ic_zooper_kustom),
    ABOUT("About", 8, R.string.section_about, -1),
    SETTINGS("Settings", 9, R.string.title_settings, -1);

    private String stringId;
    private long id;
    @StringRes
    private int text;
    @DrawableRes
    private int icon;

    DrawerItem(String stringId, long id, @StringRes int text, @DrawableRes int icon) {
        this.stringId = stringId;
        this.id = id;
        this.text = text;
        this.icon = icon;
    }

    public String getStringId() {
        return stringId;
    }

    public long getId() {
        return id;
    }

    @StringRes
    public int getText() {
        return text;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "DrawerItem: [StringId: " + getStringId() + ", Id: " + getId() + "]";
    }

    public static DrawerItem getItemWithId(String id) {
        for (DrawerItem item : values()) {
            if (item.getStringId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        throw new IllegalArgumentException("The drawer item with id: \'" + id + "\' is not " +
                "defined.");
    }

    public static DrawerItem getItemWithId(long id) {
        for (DrawerItem item : values()) {
            if (item.getId() == id) {
                return item;
            }
        }
        throw new IllegalArgumentException("The drawer item with id: \'" + id + "\' is not " +
                "defined.");
    }

}