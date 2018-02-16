/*
 * Copyright (c) 2018. Jahir Fiquitiva
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
 */
package jahirfiquitiva.libs.blueprint.providers.viewmodels

import android.content.Context
import android.content.Intent
import ca.allanwang.kau.utils.isAppInstalled
import jahirfiquitiva.libs.archhelpers.viewmodels.ListViewModel
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.stringArray

class HomeItemViewModel : ListViewModel<Context, HomeItem>() {
    override fun internalLoad(param: Context): ArrayList<HomeItem> {
        val list = ArrayList<HomeItem>()
        val titles = param.stringArray(R.array.home_list_titles)
        val descriptions = param.stringArray(R.array.home_list_descriptions)
        val icons = param.stringArray(R.array.home_list_icons)
        val urls = param.stringArray(R.array.home_list_links)
        if (titles.size == descriptions.size && descriptions.size == icons.size
                && icons.size == urls.size) {
            for (i in 0 until titles.size) {
                if (list.size >= 6) break
                val url = urls[i]
                val isAnApp = url.toLowerCase().startsWith(PLAY_STORE_LINK_PREFIX)
                var isInstalled = false
                var intent: Intent? = null
                if (isAnApp) {
                    val packageName = url.substring(url.lastIndexOf("="))
                    isInstalled = param.isAppInstalled(packageName)
                    intent = param.packageManager.getLaunchIntentForPackage(packageName)
                }
                icons[i].getDrawable(param)?.let {
                    list.add(
                            HomeItem(
                                    titles[i], descriptions[i], urls[i], it,
                                    (if (isAnApp)
                                        if (isInstalled) "ic_open_app" else "ic_download"
                                    else "ic_open_app").getDrawable(param),
                                    isAnApp, isInstalled, intent)
                            )
                }
            }
        }
        return list
    }
}