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
import jahirfiquitiva.libs.archhelpers.viewmodels.ListViewModel
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.models.IconsCategory
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.helpers.utils.BL
import jahirfiquitiva.libs.kext.extensions.boolean
import jahirfiquitiva.libs.kext.extensions.formatCorrectly
import jahirfiquitiva.libs.kext.extensions.resource
import jahirfiquitiva.libs.kext.extensions.stringArray
import org.xmlpull.v1.XmlPullParser

class IconsViewModel : ListViewModel<Context, IconsCategory>() {
    override fun internalLoad(param: Context): ArrayList<IconsCategory> {
        val categories: ArrayList<IconsCategory> = ArrayList()
        val readFromDrawableXml = param.boolean(R.bool.xml_drawable_enabled)
        val fileName = if (readFromDrawableXml) "drawable.xml" else "icon_pack.xml"
        
        if (readFromDrawableXml) {
            val parser = param.resources.getXml(R.xml.drawable)
            try {
                var event = parser.eventType
                var category: IconsCategory? = null
                while (event != XmlPullParser.END_DOCUMENT) {
                    when (event) {
                        XmlPullParser.START_TAG -> {
                            val tag = parser.name
                            if (tag == "category") {
                                if (category != null && category.hasIcons()) {
                                    categories.add(category)
                                }
                                category = IconsCategory(
                                    parser.getAttributeValue(null, "title")
                                        .formatCorrectly().blueprintFormat())
                            } else if (tag == "item") {
                                if (category != null) {
                                    val iconName = parser.getAttributeValue(null, "drawable")
                                    val iconRes = param.resource(iconName)
                                    if (iconRes != 0) {
                                        category.addIcon(
                                            Icon(
                                                iconName.formatCorrectly().blueprintFormat(),
                                                iconRes))
                                    } else {
                                        reportIconNotFound(iconName, fileName)
                                    }
                                }
                            }
                        }
                    }
                    event = parser.next()
                }
                if (category != null && category.hasIcons()) {
                    categories.add(category)
                }
            } catch (e: Exception) {
                BL.e("Error", e)
            } finally {
                parser?.close()
            }
        } else {
            param.stringArray(R.array.icon_filters).orEmpty().forEach {
                try {
                    val icons = ArrayList<Icon>()
                    param.stringArray(
                        param.resources.getIdentifier(it, "array", param.packageName)).orEmpty()
                        .forEach {
                            val iconRes = param.resource(it)
                            if (iconRes != 0) {
                                icons += Icon(it.formatCorrectly().blueprintFormat(), iconRes)
                            } else {
                                reportIconNotFound(it, fileName)
                            }
                        }
                    val filteredIcons = ArrayList(icons.distinctBy { it.name }.sortedBy { it.name })
                    if (filteredIcons.isNotEmpty()) {
                        val category = IconsCategory(it.formatCorrectly().blueprintFormat())
                        category.setIcons(filteredIcons)
                        categories.add(category)
                    }
                } catch (e: Exception) {
                    BL.e("Error", e)
                }
            }
        }
        return ArrayList(categories.distinctBy { it.title })
    }
    
    private fun reportIconNotFound(iconName: String, fileName: String) {
        BL.e("Could NOT find icon '$iconName' listed in '$fileName'")
    }
}