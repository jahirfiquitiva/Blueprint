package dev.jahir.blueprint.data.viewmodels

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.extensions.drawableRes
import dev.jahir.frames.extensions.context.stringArray
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.utils.lazyMutableLiveData

class HomeViewModel : ViewModel() {

    private val iconsPreviewData: MutableLiveData<List<Icon>> by lazyMutableLiveData()
    val iconsPreviewList: List<Icon>
        get() = iconsPreviewData.value.orEmpty()

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

    fun observeIconsPreviewList(owner: LifecycleOwner, onUpdated: (List<Icon>) -> Unit) {
        iconsPreviewData.observe(owner, Observer { onUpdated(it) })
    }

    fun destroy(owner: LifecycleOwner) {
        iconsPreviewData.removeObservers(owner)
    }
}