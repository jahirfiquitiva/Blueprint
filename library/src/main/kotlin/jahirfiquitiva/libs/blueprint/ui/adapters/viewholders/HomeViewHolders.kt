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
import ca.allanwang.kau.utils.integer
import ca.allanwang.kau.utils.tint
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.ui.adapters.IconsAdapter
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.getDimensionPixelSize
import jahirfiquitiva.libs.kauextensions.extensions.getIconResource
import jahirfiquitiva.libs.kauextensions.extensions.getInteger
import jahirfiquitiva.libs.kauextensions.extensions.getStringArray
import jahirfiquitiva.libs.kauextensions.ui.decorations.GridSpacingItemDecoration
import java.util.Collections

class PreviewCardHolder(itemView: View) : SectionedViewHolder(itemView) {
    
    private val iconsAdapter: IconsAdapter by lazy { IconsAdapter(true) }
    private val image: ImageView? by itemView.bind(R.id.wallpaper)
    private val iconsPreviewRV: RecyclerView? by itemView.bind(R.id.icons_preview_grid)
    
    fun bind(wallpaper: Drawable?) {
        image?.setImageDrawable(wallpaper)
        initIconsPreview()
    }
    
    private fun initIconsPreview() {
        iconsPreviewRV?.layoutManager =
                object : GridLayoutManager(
                        itemView.context, itemView.context.integer(R.integer.icons_columns)) {
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
        iconsPreviewRV?.addItemDecoration(
                GridSpacingItemDecoration(
                        itemView.context.getInteger(R.integer.icons_columns),
                        itemView.context.getDimensionPixelSize(R.dimen.cards_margin)))
        itemView.findViewById<LinearLayout>(R.id.icons_preview_container).setOnClickListener {
            loadIconsIntoAdapter()
        }
        loadIconsIntoAdapter()
    }
    
    private fun loadIconsIntoAdapter() {
        try {
            val icons = ArrayList<Icon>()
            val list = itemView.context.getStringArray(R.array.icons_preview)
            list.forEach {
                icons.add(Icon(it, it.getIconResource(itemView.context)))
            }
            if (icons.isNotEmpty()) {
                icons.distinct().sorted()
                Collections.shuffle(icons)
                val correctList = ArrayList<Icon>()
                for (i in 0 until itemView.context.integer(R.integer.icons_columns)) {
                    try {
                        correctList.add(icons[i])
                    } catch (ignored: Exception) {
                    }
                }
                iconsPreviewRV?.adapter = iconsAdapter
                iconsAdapter.setItems(correctList)
            }
        } catch (ignored: Exception) {
        }
    }
}

class ApplyCardHolder(itemView: View) : SectionedViewHolder(itemView) {
    val applyTitle: TextView? by itemView.bind(R.id.apply_title)
    val applyContent: TextView? by itemView.bind(R.id.apply_content)
    val dismissButton: AppCompatButton? by itemView.bind(R.id.apply_dismiss)
    val applyButton: AppCompatButton? by itemView.bind(R.id.apply_ok)
}

class CounterItemHolder(itemView: View) : SectionedViewHolder(itemView) {
    val iconsCounter: LinearLayout? by itemView.bind(R.id.icons_counter)
    val iconsCounterTitle: TextView? by itemView.bind(R.id.icons_counter_title)
    val iconsCounterCount: TextView? by itemView.bind(R.id.icons_counter_count)
    val iconsCounterIcon: ImageView? by itemView.bind(R.id.icons_counter_icon)
    
    val wallsCounter: LinearLayout? by itemView.bind(R.id.walls_counter)
    val wallsCounterTitle: TextView? by itemView.bind(R.id.walls_counter_title)
    val wallsCounterCount: TextView? by itemView.bind(R.id.walls_counter_count)
    val wallsCounterIcon: ImageView? by itemView.bind(R.id.walls_counter_icon)
    
    val kwgtCounter: LinearLayout? by itemView.bind(R.id.kwgt_counter)
    val kwgtCounterTitle: TextView? by itemView.bind(R.id.kwgt_counter_title)
    val kwgtCounterCount: TextView? by itemView.bind(R.id.kwgt_counter_count)
    val kwgtCounterIcon: ImageView? by itemView.bind(R.id.kwgt_counter_icon)
    
    val zooperCounter: LinearLayout? by itemView.bind(R.id.zooper_counter)
    val zooperCounterTitle: TextView? by itemView.bind(R.id.zooper_counter_title)
    val zooperCounterCount: TextView? by itemView.bind(R.id.zooper_counter_count)
    val zooperCounterIcon: ImageView? by itemView.bind(R.id.zooper_counter_icon)
}

class AppLinkItemHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val title: TextView? by itemView.bind(R.id.home_app_link_title)
    private val description: TextView? by itemView.bind(R.id.home_app_link_description)
    private val icon: ImageView? by itemView.bind(R.id.home_app_link_image)
    private val openIcon: ImageView? by itemView.bind(R.id.home_app_link_open_icon)
    
    fun setItem(item: HomeItem, listener: (HomeItem) -> Unit) =
            with(itemView) {
                title?.text = item.title
                description?.text = item.description
                icon?.setImageDrawable(item.icon)
                openIcon?.setImageDrawable(item.openIcon?.tint(context.activeIconsColor))
                openIcon?.setOnClickListener { listener(item) }
            }
}