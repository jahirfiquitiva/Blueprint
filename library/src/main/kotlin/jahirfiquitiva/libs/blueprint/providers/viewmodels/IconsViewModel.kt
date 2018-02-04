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
import ca.allanwang.kau.utils.boolean
import jahirfiquitiva.libs.archhelpers.viewmodels.ListViewModel
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.data.models.IconsCategory
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly
import jahirfiquitiva.libs.kauextensions.extensions.getIconResource
import jahirfiquitiva.libs.kauextensions.extensions.stringArray
import org.xmlpull.v1.XmlPullParser

class IconsViewModel : ListViewModel<Context, IconsCategory>() {
    override fun internalLoad(param: Context): ArrayList<IconsCategory> {
        if (param.boolean(R.bool.xml_drawable_enabled)) {
            val list = ArrayList<IconsCategory>()
            val parser = param.resources.getXml(R.xml.drawable)
            try {
                var event = parser.eventType
                var category: IconsCategory? = null
                while (event != XmlPullParser.END_DOCUMENT) {
                    when (event) {
                        XmlPullParser.START_TAG -> {
                            val tag = parser.name
                            if (tag == "category")
                                category = IconsCategory(
                                        parser.getAttributeValue(null, "title")
                                                .formatCorrectly().blueprintFormat())
                            else if (tag == "item")
                                if (category != null) {
                                    val iconName = parser.getAttributeValue(null, "drawable")
                                    category.icons.add(
                                            Icon(
                                                    iconName.formatCorrectly().blueprintFormat(),
                                                    iconName.getIconResource(param)))
                                }
                        }
                    }
                    event = parser.next()
                }
            } catch (ignored: Exception) {
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
        } else {
            val categories: ArrayList<IconsCategory> = ArrayList()
            param.stringArray(R.array.icon_filters).forEach {
                val icons = ArrayList<Icon>()
                val list = ArrayList<String>()
                list.addAll(
                        param.stringArray(
                                param.resources.getIdentifier(it, "array", param.packageName)))
                list.forEach {
                    icons.add(
                            Icon(it.formatCorrectly().blueprintFormat(), it.getIconResource(param)))
                }
                categories.add(IconsCategory(it, ArrayList(icons.distinct().sorted())))
            }
            return categories
        }
    }
}