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
package jahirfiquitiva.libs.blueprint.ui.adapters.viewholders

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.isVisible
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import com.bumptech.glide.RequestManager
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.kext.extensions.activeIconsColor
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.context
import jahirfiquitiva.libs.kext.extensions.dimenPixelSize
import jahirfiquitiva.libs.kext.extensions.int
import jahirfiquitiva.libs.kext.extensions.resource
import jahirfiquitiva.libs.kext.extensions.stringArray
import jahirfiquitiva.libs.kext.helpers.TRANSPARENT
import jahirfiquitiva.libs.kext.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kext.ui.widgets.CustomCardView

class PreviewCardHolder(itemView: View) : SectionedViewHolder(itemView) {
    
    private val decoration: GridSpacingItemDecoration by lazy {
        GridSpacingItemDecoration(
            int(R.integer.icons_columns),
            dimenPixelSize(R.dimen.cards_margin))
    }
    
    private val card: CustomCardView? by bind(R.id.icons_preview_card)
    private val image: ImageView? by bind(R.id.toolbar_wallpaper)
    private val iconsPreviewRV: RecyclerView? by bind(R.id.icons_preview_grid)
    private val correctList = ArrayList<Icon>()
    
    private var manager: RequestManager? = null
    
    init {
        initIconsPreview()
    }
    
    fun bind(drawable: Drawable?, onlyPicture: Boolean, manager: RequestManager? = null) {
        this.manager = manager
        image?.setImageDrawable(drawable)
        val isTransparent = BPKonfigs(context).currentTheme == TRANSPARENT
        if (isTransparent) card?.forceSetCardBackgroundColor(Color.TRANSPARENT)
        if (!onlyPicture) initIconsPreview()
    }
    
    fun setPool(pool: RecyclerView.RecycledViewPool?) {
        iconsPreviewRV?.setRecycledViewPool(pool)
    }
    
    private fun initIconsPreview() {
        try {
            iconsPreviewRV?.removeItemDecoration(decoration)
            iconsPreviewRV?.isNestedScrollingEnabled = false
            iconsPreviewRV?.layoutManager =
                PreviewGridLayoutManager(context, int(R.integer.icons_columns))
            iconsPreviewRV?.addItemDecoration(decoration)
            card?.setOnClickListener { loadIconsIntoAdapter(true) }
            loadIconsIntoAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadIconsIntoAdapter(force: Boolean = false) {
        try {
            if (correctList.isEmpty() || force) {
                val icons = ArrayList<Icon>()
                stringArray(R.array.icons_preview)?.forEach {
                    icons.add(Icon(it, context.resource(it)))
                }
                if (icons.isNotEmpty()) {
                    val shuffled = icons.distinctBy { it.name }.shuffled()
                    correctList.clear()
                    for (i in 0 until int(R.integer.icons_columns)) {
                        try {
                            correctList.add(shuffled[i])
                        } catch (ignored: Exception) {
                        }
                    }
                }
            }
            val adapter = IconsAdapter(manager, true)
            adapter.setItems(correctList)
            iconsPreviewRV?.adapter = adapter
            if (iconsPreviewRV?.isVisible == false)
                postDelayed(50) { iconsPreviewRV?.visible() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private inner class PreviewGridLayoutManager(context: Context, span: Int) :
        GridLayoutManager(context, span, RecyclerView.VERTICAL, false) {
        override fun canScrollHorizontally(): Boolean = false
        override fun canScrollVertically(): Boolean = false
        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean
                                                  ): Boolean = false
        
        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean,
            focusedChildVisible: Boolean
                                                  ): Boolean = false
    }
    
}

class CounterItemHolder(itemView: View) : SectionedViewHolder(itemView) {
    val iconsCounter: LinearLayout? by bind(R.id.icons_counter)
    val iconsCounterTitle: TextView? by bind(R.id.icons_counter_title)
    val iconsCounterCount: TextView? by bind(R.id.icons_counter_count)
    val iconsCounterIcon: ImageView? by bind(R.id.icons_counter_icon)
    
    val wallsCounter: LinearLayout? by bind(R.id.walls_counter)
    val wallsCounterTitle: TextView? by bind(R.id.walls_counter_title)
    val wallsCounterCount: TextView? by bind(R.id.walls_counter_count)
    val wallsCounterIcon: ImageView? by bind(R.id.walls_counter_icon)
    
    val kwgtCounter: LinearLayout? by bind(R.id.kwgt_counter)
    val kwgtCounterTitle: TextView? by bind(R.id.kwgt_counter_title)
    val kwgtCounterCount: TextView? by bind(R.id.kwgt_counter_count)
    val kwgtCounterIcon: ImageView? by bind(R.id.kwgt_counter_icon)
    
    val zooperCounter: LinearLayout? by bind(R.id.zooper_counter)
    val zooperCounterTitle: TextView? by bind(R.id.zooper_counter_title)
    val zooperCounterCount: TextView? by bind(R.id.zooper_counter_count)
    val zooperCounterIcon: ImageView? by bind(R.id.zooper_counter_icon)
}

class AppLinkItemHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val title: TextView? by bind(R.id.home_app_link_title)
    private val description: TextView? by bind(R.id.home_app_link_description)
    private val icon: ImageView? by bind(R.id.home_app_link_image)
    private val openIcon: ImageView? by bind(R.id.home_app_link_open_icon)
    
    fun setItem(item: HomeItem, listener: (HomeItem) -> Unit) = with(itemView) {
        title?.text = item.title
        description?.text = item.description
        item.icon?.let {
            icon?.setImageDrawable(it)
        } ?: icon?.gone()
        item.openIcon?.let {
            openIcon?.setImageDrawable(it.tint(context.activeIconsColor))
        } ?: openIcon?.gone()
        itemView.setOnClickListener { listener(item) }
    }
}