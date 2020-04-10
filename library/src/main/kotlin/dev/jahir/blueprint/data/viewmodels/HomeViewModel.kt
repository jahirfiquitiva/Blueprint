package dev.jahir.blueprint.data.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.HomeItem
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.utils.lazyMutableLiveData
import dev.jahir.frames.ui.activities.base.BaseLicenseCheckerActivity.Companion.PLAY_STORE_LINK_PREFIX
import dev.jahir.kuper.extensions.isAppInstalled

class HomeViewModel : ViewModel() {

    private val iconsPreviewData: MutableLiveData<List<Icon>> by lazyMutableLiveData()
    val iconsPreviewList: List<Icon>
        get() = iconsPreviewData.value.orEmpty()

    private val homeItemsData: MutableLiveData<List<HomeItem>> by lazyMutableLiveData()
    val homeItems: List<HomeItem>
        get() = homeItemsData.value.orEmpty()

    fun loadPreviewIcons(context: Context?, force: Boolean = false) {
        context ?: return
        if (iconsPreviewList.isEmpty() || force) {
            val nextIcons = ArrayList<Icon>()
            context.stringArray(R.array.icons_preview).filter { it.hasContent() }.forEach {
                nextIcons.add(Icon(it, context.drawableRes(it)))
            }
            iconsPreviewData.postValue(nextIcons.distinctBy { it.name }.shuffled())
        }
    }

    fun loadHomeItems(context: Context?) {
        context ?: return
        val list = ArrayList<HomeItem>()
        val titles = context.stringArray(R.array.home_list_titles).filter { it.hasContent() }
        val descriptions =
            context.stringArray(R.array.home_list_descriptions).filter { it.hasContent() }
        val icons = context.stringArray(R.array.home_list_icons).filter { it.hasContent() }
        val urls = context.stringArray(R.array.home_list_links).filter { it.hasContent() }
        if (titles.size == descriptions.size && descriptions.size == icons.size
            && icons.size == urls.size) {
            for (i in titles.indices) {
                if (list.size >= 6) break
                val url = urls[i]
                val isAnApp =
                    url.lower().startsWith(PLAY_STORE_LINK_PREFIX) ||
                            url.lower().startsWith("market://details?id=")
                var isInstalled = false
                var intent: Intent? = null
                if (isAnApp) {
                    val packageName = url.substring(url.lastIndexOf("="))
                    isInstalled = context.isAppInstalled(packageName)
                    intent = context.packageManager.getLaunchIntentForPackage(packageName)
                }

                list.add(
                    HomeItem(
                        titles[i],
                        descriptions[i],
                        urls[i],
                        context.drawable(icons[i]),
                        if (isAnApp)
                            if (isInstalled) R.drawable.ic_open_app else R.drawable.ic_download
                        else R.drawable.ic_open_app,
                        isAnApp,
                        isInstalled,
                        intent
                    )
                )
            }
        }
        homeItemsData.postValue(list)
    }

    fun observeIconsPreviewList(owner: LifecycleOwner, onUpdated: (List<Icon>) -> Unit) {
        iconsPreviewData.observe(owner, Observer { onUpdated(it) })
    }

    fun observeHomeItems(owner: LifecycleOwner, onUpdated: (List<HomeItem>) -> Unit) {
        homeItemsData.observe(owner, Observer(onUpdated))
    }

    fun destroy(owner: LifecycleOwner) {
        iconsPreviewData.removeObservers(owner)
        homeItemsData.removeObservers(owner)
    }
}