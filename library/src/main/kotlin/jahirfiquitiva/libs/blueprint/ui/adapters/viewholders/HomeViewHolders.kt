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

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.tint
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.context
import jahirfiquitiva.libs.kauextensions.extensions.dimenPixelSize
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getIconResource
import jahirfiquitiva.libs.kauextensions.extensions.integer
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import jahirfiquitiva.libs.kauextensions.extensions.stringArray
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import jahirfiquitiva.libs.kauextensions.ui.widgets.CustomCardView
import java.util.Collections

class PreviewCardHolder(
        private val iconsAdapter: IconsAdapter,
        itemView: View
                       ) : SectionedViewHolder(itemView) {
    
    private val decoration: GridSpacingItemDecoration by lazy {
        GridSpacingItemDecoration(
                integer(R.integer.icons_columns),
                dimenPixelSize(R.dimen.cards_margin))
    }
    
    private val card: CustomCardView? by bind(R.id.icons_preview_card)
    private val image: ImageView? by bind(R.id.wallpaper)
    private val iconsPreviewRV: RecyclerView? by bind(R.id.icons_preview_grid)
    private val correctList = ArrayList<Icon>()
    
    fun bind(wallpaper: Drawable?) {
        image?.setImageDrawable(wallpaper)
        initIconsPreview()
    }
    
    private fun initIconsPreview() {
        iconsPreviewRV?.removeItemDecoration(decoration)
        iconsPreviewRV?.isNestedScrollingEnabled = false
        iconsPreviewRV?.layoutManager =
                object : GridLayoutManager(context, integer(R.integer.icons_columns)) {
                    override fun canScrollVertically(): Boolean = false
                    override fun canScrollHorizontally(): Boolean = false
                    override fun requestChildRectangleOnScreen(
                            parent: RecyclerView?, child: View?,
                            rect: Rect?,
                            immediate: Boolean
                                                              ): Boolean = false
                    
                    override fun requestChildRectangleOnScreen(
                            parent: RecyclerView?, child: View?,
                            rect: Rect?, immediate: Boolean,
                            focusedChildVisible: Boolean
                                                              ): Boolean = false
                }
        iconsPreviewRV?.addItemDecoration(decoration)
        card?.setOnClickListener { loadIconsIntoAdapter() }
        loadIconsIntoAdapter()
    }
    
    private fun loadIconsIntoAdapter() {
        try {
            val icons = ArrayList<Icon>()
            val list = stringArray(R.array.icons_preview)
            list.forEach {
                icons.add(Icon(it, it.getIconResource(context)))
            }
            if (icons.isNotEmpty()) {
                icons.distinctBy { it.name }
                Collections.shuffle(icons)
                correctList.clear()
                for (i in 0 until integer(R.integer.icons_columns)) {
                    try {
                        correctList.add(icons[i])
                    } catch (ignored: Exception) {
                    }
                }
                iconsAdapter.setItems(correctList)
                if (iconsPreviewRV?.adapter == null)
                    iconsPreviewRV?.adapter = iconsAdapter
            }
        } catch (ignored: Exception) {
        }
    }
}

class ButtonsItemHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val rateBtn: AppCompatButton? by bind(R.id.rate_btn)
    private val shareBtn: AppCompatButton? by bind(R.id.share_btn)
    private val donateBtn: AppCompatButton? by bind(R.id.donate_btn)
    
    fun bind(showDonateBtn: Boolean = false, onDonate: () -> Unit = {}) {
        rateBtn?.setOnClickListener {
            context.openLink(PLAY_STORE_LINK_PREFIX + context.packageName)
        }
        shareBtn?.setOnClickListener {
            context.shareText(
                    context.getString(
                            R.string.share_this_app, context.getAppName(),
                            PLAY_STORE_LINK_PREFIX + context.packageName))
        }
        if (showDonateBtn) {
            donateBtn?.setOnClickListener { onDonate() }
        } else {
            donateBtn?.gone()
        }
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
        icon?.setImageDrawable(item.icon)
        openIcon?.setImageDrawable(item.openIcon?.tint(context.activeIconsColor))
        itemView?.setOnClickListener { listener(item) }
    }
}