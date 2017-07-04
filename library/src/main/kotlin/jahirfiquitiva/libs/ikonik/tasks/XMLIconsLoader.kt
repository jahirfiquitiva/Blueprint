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
import jahirfiquitiva.libs.ikonik.R
import jahirfiquitiva.libs.ikonik.models.Icon
import jahirfiquitiva.libs.ikonik.models.IconsCategory
import jahirfiquitiva.libs.ikonik.utils.IconUtils
import org.xmlpull.v1.XmlPullParser


class XMLIconsLoader(context:Context, listener:TaskListener? = null):
        BasicTaskLoader<ArrayList<IconsCategory>>(context, listener) {

    override fun loadInBackground():ArrayList<IconsCategory> {
        val list = ArrayList<IconsCategory>()
        val parser = context.resources.getXml(R.xml.drawable)
        try {
            var event = parser.eventType
            var category:IconsCategory? = null
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_TAG -> {
                        val tag = parser.name
                        if (tag == "category")
                            category = IconsCategory(
                                    IconUtils.formatText(parser.getAttributeValue(null, "title")))
                        else if (tag == "item")
                            if (category != null) {
                                val iconName = parser.getAttributeValue(null, "drawable")
                                category.icons.add(Icon(IconUtils.formatText(iconName),
                                        IconUtils.getIconResourceWithName(context, iconName)))
                            }
                    }
                }
                event = parser.next()
            }
        } catch (ignored:Exception) {
        } finally {
            parser?.close()
        }
        if (list.size > 0) {
            val finalList = ArrayList<IconsCategory>()
            list.forEach {
                if (it.icons.size > 0) finalList.add(it)
            }
            list.clear()
            list.addAll(finalList)
        }
        return list
    }

}