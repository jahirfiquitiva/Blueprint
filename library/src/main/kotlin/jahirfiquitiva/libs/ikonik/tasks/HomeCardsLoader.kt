/*
 * Copyright (c) 2017. Jahir Fiquitiva
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
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.tasks

import android.content.Context
import android.content.Intent
import jahirfiquitiva.libs.ikonik.R
import jahirfiquitiva.libs.ikonik.models.HomeCard
import jahirfiquitiva.libs.ikonik.utils.CoreUtils
import jahirfiquitiva.libs.ikonik.utils.IconUtils
import jahirfiquitiva.libs.ikonik.utils.NetworkUtils
import jahirfiquitiva.libs.ikonik.utils.ResourceUtils

class HomeCardsLoader(context:Context, listener:TaskListener? = null):
        BasicTaskLoader<ArrayList<HomeCard>>(context, listener) {

    override fun loadInBackground():ArrayList<HomeCard> {
        val cards = ArrayList<HomeCard>()
        val titles = ResourceUtils.getStringArray(context, R.array.home_list_titles)
        val descriptions = ResourceUtils.getStringArray(context,
                R.array.home_list_descriptions)
        val icons = ResourceUtils.getStringArray(context, R.array.home_list_icons)
        val urls = ResourceUtils.getStringArray(context, R.array.home_list_links)
        if (titles.size == descriptions.size && descriptions.size == icons.size
            && icons.size == urls.size) {
            val maxSize = (if (titles.size > 4) 4 else titles.size) - 1
            for (i in 0..maxSize) {
                val url = urls[i]
                val isAnApp = url.toLowerCase().startsWith(NetworkUtils.PLAY_STORE_LINK_PREFIX)
                var isInstalled = false
                var intent:Intent? = null
                if (isAnApp) {
                    val packageName = url.substring(url.lastIndexOf("="))
                    isInstalled = CoreUtils.isAppInstalled(context, packageName)
                    intent = context.packageManager.getLaunchIntentForPackage(packageName)
                }
                cards.add(HomeCard(titles[i], descriptions[i], urls[i],
                        IconUtils.getDrawableWithName(context, icons[i]), isAnApp,
                        isInstalled, intent))
            }
        }
        return cards
    }
}