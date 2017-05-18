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

package jahirfiquitiva.libs.iconshowcase.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils;
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils;

public class HomeCard {

    private String title;
    private String description;
    private String url;
    private Drawable icon;
    private boolean isAnApp;
    private boolean isInstalled;
    private Intent intent;

    public HomeCard(Context context, String title, String description, String url, String icon) {
        this(context, title, description, url, IconUtils.getDrawableWithName(context, icon));
    }

    private HomeCard(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    private HomeCard(Context context, String title, String description, String url, Drawable icon) {
        this(title, description, url);
        this.icon = icon;
        this.isAnApp = url.toLowerCase().contains("http://play.google.com/store/apps/details?id=");
        if (isAnApp) {
            String packageName = url.substring(url.lastIndexOf("=") + 1, url.length());
            this.isInstalled = CoreUtils.isAppInstalled(context, packageName);
            this.intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Drawable getIcon() {
        return icon;
    }

    public Intent getIntent() {
        return intent;
    }
}