package dev.jahir.blueprint.data.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.extensions.blueprintFormat
import dev.jahir.blueprint.extensions.clean
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.context.withXml
import dev.jahir.frames.extensions.resources.getAttributeValue
import dev.jahir.frames.extensions.resources.nextOrNull
import dev.jahir.frames.extensions.utils.context
import dev.jahir.frames.extensions.utils.lazyMutableLiveData
import dev.jahir.frames.extensions.utils.tryToObserve
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

@Suppress("MemberVisibilityCanBePrivate")
class IconsCategoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val iconsCategoriesData: MutableLiveData<ArrayList<IconsCategory>> by lazyMutableLiveData()
    val iconsCategories: ArrayList<IconsCategory>
        get() = ArrayList(iconsCategoriesData.value.orEmpty())

    val iconsCount: Int
        get() {
            val icons = ArrayList<Icon>()
            iconsCategories.forEach { icons.addAll(it.getIcons()) }
            return icons.distinctBy { it.resId }.size
        }

    private suspend fun loadCategoriesFromDrawable(): ArrayList<IconsCategory> {
        if (iconsCategories.isNotEmpty()) return ArrayList(iconsCategories)
        val categories: ArrayList<IconsCategory> = ArrayList()
        return withContext(IO) {
            context.withXml(R.xml.drawable) { parser ->
                var event: Int? = parser.eventType
                var category: IconsCategory? = null
                while (event != null && event != XmlPullParser.END_DOCUMENT) {
                    when (event) {
                        XmlPullParser.START_TAG -> {
                            val tag = parser.name
                            if (tag == "category") {
                                if (category != null && category.hasIcons())
                                    categories.add(category)
                                category = IconsCategory(
                                    parser.getAttributeValue("title").orEmpty().clean()
                                        .blueprintFormat()
                                )
                            } else if (tag == "item") {
                                if (category != null) {
                                    val iconName = parser.getAttributeValue("drawable").orEmpty()
                                    val iconRes = context.drawableRes(iconName)
                                    if (iconRes != 0) {
                                        category.addIcon(
                                            Icon(iconName.clean().blueprintFormat(), iconRes)
                                        )
                                    } else {
                                        reportIconNotFound(
                                            iconName, "drawable.xml", context.getAppName()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    event = parser.nextOrNull()
                }
                if (category != null && category.hasIcons())
                    categories.add(category)
            }
            categories
        }
    }

    private suspend fun loadCategoriesFromIconPack(): ArrayList<IconsCategory> {
        val categories: ArrayList<IconsCategory> = ArrayList()
        return withContext(IO) {
            context.stringArray(R.array.icon_filters).forEach { filter ->
                try {
                    val icons = ArrayList<Icon>()
                    context.stringArray(
                        context.resources.getIdentifier(filter, "array", context.packageName)
                    ).forEach { iconName ->
                        val iconRes = context.drawableRes(iconName)
                        if (iconRes != 0) {
                            icons.add(Icon(iconName.clean().blueprintFormat(), iconRes))
                        } else {
                            reportIconNotFound(iconName, "icon_pack.xml", context.getAppName())
                        }
                    }
                    val filteredIcons = ArrayList(icons.distinctBy { it.name }.sortedBy { it.name })
                    if (filteredIcons.isNotEmpty()) {
                        val category = IconsCategory(filter.clean().blueprintFormat())
                        category.setIcons(filteredIcons)
                        categories.add(category)
                    }
                } catch (e: Exception) {
                }
            }
            categories
        }
    }

    fun loadIconsCategories() {
        val readFromDrawable = context.boolean(R.bool.xml_drawable_enabled)
        viewModelScope.launch {
            val categories =
                if (readFromDrawable) loadCategoriesFromDrawable()
                else loadCategoriesFromIconPack()
            iconsCategoriesData.postValue(categories)
        }
    }

    fun observe(owner: LifecycleOwner, onUpdated: (ArrayList<IconsCategory>) -> Unit) {
        iconsCategoriesData.tryToObserve(owner, onUpdated)
    }

    fun destroy(owner: LifecycleOwner) {
        iconsCategoriesData.removeObservers(owner)
    }

    private fun reportIconNotFound(
        iconName: String, fileName: String, appName: String? = "Blueprint"
    ) {
        Log.e(appName, "Could NOT find icon '$iconName' listed in '$fileName'")
    }
}