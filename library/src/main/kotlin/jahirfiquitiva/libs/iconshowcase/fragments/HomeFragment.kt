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

package jahirfiquitiva.libs.iconshowcase.fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.adapters.HomeCardsAdapter
import jahirfiquitiva.libs.iconshowcase.models.HomeCard
import jahirfiquitiva.libs.iconshowcase.ui.views.EmptyViewRecyclerView
import jahirfiquitiva.libs.iconshowcase.utils.*
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils
import java.util.*

class HomeFragment:Fragment() {
    override fun onCreateView(inflater:LayoutInflater?, container:ViewGroup?,
                              savedInstanceState:Bundle?):View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val content = inflater?.inflate(R.layout.section_home, container, false) as View
        initRV(content)
        return content
    }

    private fun initRV(content:View) {
        val rv = content.findViewById(R.id.home_rv) as EmptyViewRecyclerView
        rv.emptyView = content.findViewById(R.id.empty_view)
        rv.textView = content.findViewById(R.id.empty_text) as TextView?
        rv.state = EmptyViewRecyclerView.STATE_LOADING
        rv.updateStateViews()

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

        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val deco = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        deco.setDrawable(
                ColorDrawable(ColorUtils.getMaterialDividerColor(ThemeUtils.isDarkTheme())))
        rv.addItemDecoration(deco)
        rv.adapter = HomeCardsAdapter(cards)
        rv.state = EmptyViewRecyclerView.STATE_NORMAL
        rv.updateStateViews()
    }
}