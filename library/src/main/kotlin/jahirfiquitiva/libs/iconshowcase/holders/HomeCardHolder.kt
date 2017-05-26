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

package jahirfiquitiva.libs.iconshowcase.holders

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.models.HomeCard
import jahirfiquitiva.libs.iconshowcase.utils.NetworkUtils

class HomeCardHolder {

    class ExtraCardHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val root:LinearLayout = itemView.findViewById(R.id.home_extra_card) as LinearLayout
        val title:TextView = itemView.findViewById(R.id.home_extra_card_title) as TextView
        val description:TextView = itemView.findViewById(R.id.home_extra_card_description)
                as TextView
        val icon:ImageView = itemView.findViewById(R.id.home_extra_card_image) as ImageView

        fun setItem(item:HomeCard) {
            root.setOnClickListener { view -> launchIntent(view.context, item) }
            title.text = item.title
            description.text = item.description
            icon.setImageDrawable(item.icon)
        }

        private fun launchIntent(context:Context, item:HomeCard) {
            when {
                item.intent != null -> context.startActivity(item.intent)
                else -> NetworkUtils.openLink(context, item.url)
            }
        }
    }

}