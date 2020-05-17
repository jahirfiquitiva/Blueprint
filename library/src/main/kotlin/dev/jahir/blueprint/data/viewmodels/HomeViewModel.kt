package dev.jahir.blueprint.data.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.DefHomeItem
import dev.jahir.blueprint.data.models.HomeItem
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.blueprint.ui.fragments.HomeFragment
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.utils.context
import dev.jahir.frames.extensions.utils.lazyMutableLiveData
import dev.jahir.frames.extensions.utils.tryToObserve
import dev.jahir.frames.ui.activities.base.BaseLicenseCheckerActivity.Companion.PLAY_STORE_LINK_PREFIX
import dev.jahir.kuper.extensions.isAppInstalled
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val iconsPreviewData: MutableLiveData<List<Icon>> by lazyMutableLiveData()
    val iconsPreviewList: List<Icon>
        get() = iconsPreviewData.value.orEmpty()

    private val homeItemsData: MutableLiveData<List<HomeItem>> by lazyMutableLiveData()
    val homeItems: List<HomeItem>
        get() = homeItemsData.value.orEmpty()

    private val iconsCountData: MutableLiveData<Int> by lazyMutableLiveData()
    private val wallpapersCountData: MutableLiveData<Int> by lazyMutableLiveData()
    private val kustomCountData: MutableLiveData<Int> by lazyMutableLiveData()
    private val zooperCountData: MutableLiveData<Int> by lazyMutableLiveData()

    fun loadPreviewIcons(force: Boolean = false) {
        if (iconsPreviewList.isEmpty() || force) {
            val nextIcons = ArrayList<Icon>()
            context.stringArray(R.array.icons_preview).filter { it.hasContent() }.forEach {
                nextIcons.add(Icon(it, context.drawableRes(it)))
            }
            iconsPreviewData.postValue(nextIcons.distinctBy { it.name }.shuffled())
        }
    }

    private suspend fun internalLoadHomeItems(): ArrayList<HomeItem> = withContext(Default) {
        val list = ArrayList<HomeItem>()
        val titles = context.stringArray(R.array.home_list_titles).filter { it.hasContent() }
        val descriptions =
            context.stringArray(R.array.home_list_descriptions).filter { it.hasContent() }
        val icons = context.stringArray(R.array.home_list_icons).filter { it.hasContent() }
        val urls = context.stringArray(R.array.home_list_links).filter { it.hasContent() }
        if (titles.size == descriptions.size && descriptions.size == icons.size
            && icons.size == urls.size) {
            val defaultItems =
                titles.mapIndexed { i, s -> DefHomeItem(s, descriptions[i], icons[i], urls[i]) }
                    .distinctBy { it.url }
                    .map {
                        val isAnApp =
                            it.url.lower().startsWith(PLAY_STORE_LINK_PREFIX) ||
                                    it.url.lower().startsWith("market://details?id=")
                        var isInstalled = false
                        var intent: Intent? = null
                        if (isAnApp) {
                            try {
                                val packageName = it.url.substring(it.url.lastIndexOf("=") + 1)
                                isInstalled = context.isAppInstalled(packageName)
                                intent =
                                    context.packageManager.getLaunchIntentForPackage(packageName)
                            } catch (e: Exception) {
                            }
                        }
                        val openIcon = if (isAnApp)
                            if (isInstalled) R.drawable.ic_open_app else R.drawable.ic_download
                        else R.drawable.ic_open_app
                        HomeItem(
                            it.title,
                            it.desc,
                            it.url,
                            context.drawable(it.icon),
                            openIcon,
                            isAnApp,
                            isInstalled,
                            intent
                        )
                    }
            list.clear()
            list.addAll(defaultItems)
        }
        val maxSize = if (list.size > 6) 6 else list.size
        return@withContext ArrayList(list.subList(0, maxSize))
    }

    fun loadHomeItems() {
        viewModelScope.launch {
            val items = internalLoadHomeItems()
            homeItemsData.postValue(items)
        }
    }

    fun observeIconsPreviewList(owner: LifecycleOwner, onUpdated: (List<Icon>) -> Unit) {
        iconsPreviewData.tryToObserve(owner, onUpdated)
    }

    fun observeHomeItems(owner: LifecycleOwner, onUpdated: (List<HomeItem>) -> Unit) {
        homeItemsData.tryToObserve(owner, onUpdated)
    }

    fun postIconsCount(count: Int? = 0) {
        iconsCountData.postValue(count)
    }

    fun postWallpapersCount(count: Int? = 0) {
        wallpapersCountData.postValue(count)
    }

    fun postKustomCount(count: Int? = 0) {
        kustomCountData.postValue(count)
    }

    fun postZooperCount(count: Int? = 0) {
        zooperCountData.postValue(count)
    }

    private fun observeIconsCount(owner: LifecycleOwner, onUpdated: (Int) -> Unit) {
        onUpdated(iconsCountData.value ?: 0)
        iconsCountData.tryToObserve(owner, onUpdated)
    }

    private fun observeWallpapersCount(owner: LifecycleOwner, onUpdated: (Int) -> Unit) {
        onUpdated(wallpapersCountData.value ?: 0)
        wallpapersCountData.tryToObserve(owner, onUpdated)
    }

    private fun observeKustomCount(owner: LifecycleOwner, onUpdated: (Int) -> Unit) {
        onUpdated(kustomCountData.value ?: 0)
        kustomCountData.tryToObserve(owner, onUpdated)
    }

    private fun observeZooperCount(owner: LifecycleOwner, onUpdated: (Int) -> Unit) {
        onUpdated(zooperCountData.value ?: 0)
        zooperCountData.tryToObserve(owner, onUpdated)
    }

    fun observeCounters(owner: LifecycleOwner, fragment: HomeFragment? = null) {
        observeIconsCount(owner) { fragment?.updateIconsCount(it) }
        observeWallpapersCount(owner) { fragment?.updateWallpapersCount(it) }
        observeKustomCount(owner) { fragment?.updateKustomCount(it) }
        observeZooperCount(owner) { fragment?.updateZooperCount(it) }
    }

    fun repostCounters() {
        iconsCountData.postValue(iconsCountData.value ?: 0)
        wallpapersCountData.postValue(wallpapersCountData.value ?: 0)
        kustomCountData.postValue(kustomCountData.value ?: 0)
        zooperCountData.postValue(zooperCountData.value ?: 0)
    }

    fun destroy(owner: LifecycleOwner) {
        iconsPreviewData.removeObservers(owner)
        homeItemsData.removeObservers(owner)
        iconsCountData.removeObservers(owner)
        wallpapersCountData.removeObservers(owner)
        kustomCountData.removeObservers(owner)
        zooperCountData.removeObservers(owner)
    }
}