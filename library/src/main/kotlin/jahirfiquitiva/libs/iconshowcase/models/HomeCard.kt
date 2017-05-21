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

package jahirfiquitiva.libs.iconshowcase.models

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import jahirfiquitiva.libs.iconshowcase.utils.CoreUtils
import jahirfiquitiva.libs.iconshowcase.utils.IconUtils
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils

data class HomeCard(private var title:String, private var description:String,
                    private var url:String, private var icon:Drawable) {
    private var isAnApp:Boolean = false
    private var isInstalled:Boolean = false
    private var intent:Intent = null !!

    constructor(context:Context, title:String, description:String, url:String, icon:String)
            :this(title, description, url, IconUtils.getDrawableWithName(context, icon))

    constructor(context:Context, title:String, description:String, url:String, icon:Drawable,
                parent:HomeCard):this(title, description, url, icon) {
        parent.isAnApp = url.toLowerCase().startsWith(NetworkUtils.PLAY_STORE_LINK_PREFIX)
        if (isAnApp) {
            val packageName:String = url.substring(url.lastIndexOf("=" + 1))
            parent.isInstalled = CoreUtils.isAppInstalled(context, packageName)
            parent.intent = context.packageManager.getLaunchIntentForPackage(packageName)
        }
    }
}