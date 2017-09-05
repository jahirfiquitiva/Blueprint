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
 */

package jahirfiquitiva.libs.blueprint.ui.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.inflate
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.HomeItem
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.extensions.defaultLauncher
import jahirfiquitiva.libs.blueprint.helpers.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.HomeItemsViewHolders
import jahirfiquitiva.libs.frames.ui.adapters.BaseListAdapter
import jahirfiquitiva.libs.frames.ui.widgets.SimpleAnimationListener
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.applyColorFilter
import jahirfiquitiva.libs.kauextensions.extensions.chipsColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

class HomeItemsAdapter(private val context:Context,
                       private val listener:(HomeItem) -> Unit,
                       private val iconsAmount:Int = 0, private val wallsAmount:Int = 0,
                       private val kwgtAmount:Int = 0, private val zooperAmount:Int = 0):
        BaseListAdapter<HomeItem, RecyclerView.ViewHolder>() {
    
    var shouldShowApplyCard = false
    private var firstLinkPosition = -1
    // TODO Change to 0
    private val MINIMAL_AMOUNT = -1
    
    override fun doBind(holder:RecyclerView.ViewHolder, position:Int, shouldAnimate:Boolean) {
        try {
            if (shouldShowApplyCard && position == 0 &&
                    holder is HomeItemsViewHolders.ApplyCardHolder) {
                val initCard = context.defaultLauncher?.isActuallySupported == true
                if (initCard) {
                    val titleColor = context.getPrimaryTextColorFor(context.accentColor)
                    val contentColor = context.getSecondaryTextColorFor(context.accentColor)
                    holder.applyTitle.setTextColor(titleColor)
                    holder.applyTitle.text = context.getString(R.string.apply_title,
                                                               context.getAppName())
                    holder.applyContent.setTextColor(contentColor)
                    holder.applyContent.text = context.getString(R.string.apply_content,
                                                                 context.defaultLauncher?.name)
                    holder.dismissButton.setTextColor(contentColor)
                    holder.dismissButton.setOnClickListener {
                        val anim = AnimationUtils.loadAnimation(context,
                                                                android.R.anim.slide_out_right)
                        anim.setAnimationListener(object:SimpleAnimationListener() {
                            override fun onAnimationEnd(animation:Animation?) {
                                super.onAnimationEnd(animation)
                                context.bpKonfigs.isApplyCardDismissed = true
                                shouldShowApplyCard = false
                                notifyItemRemoved(0)
                                notifyDataSetChanged()
                            }
                        })
                        holder.itemView.startAnimation(anim)
                    }
                    holder.applyButton.setTextColor(contentColor)
                    holder.applyButton.setOnClickListener {
                        context.executeLauncherIntent(context.defaultLauncher?.name ?: "")
                    }
                }
            } else if (holder is HomeItemsViewHolders.CounterItemHolder) {
                val labelColor = context.primaryTextColor
                val counterColor = context.secondaryTextColor
                val iconColor = context.chipsIconsColor
                val bgColor = context.chipsColor
                
                if (iconsAmount > MINIMAL_AMOUNT) {
                    holder.iconsCounter.setBackgroundColor(bgColor)
                    holder.iconsCounterIcon.setImageDrawable(
                            ContextCompat.getDrawable(context, NavigationItem.ICONS.icon)
                                    .applyColorFilter(iconColor))
                    holder.iconsCounterTitle.setTextColor(labelColor)
                    holder.iconsCounterCount.setTextColor(counterColor)
                    holder.iconsCounterCount.text = iconsAmount.toString()
                } else {
                    holder.iconsCounter.gone()
                }
                
                if (wallsAmount > MINIMAL_AMOUNT) {
                    holder.wallsCounter.setBackgroundColor(bgColor)
                    holder.wallsCounterIcon.setImageDrawable(
                            ContextCompat.getDrawable(context, NavigationItem.ICONS.icon)
                                    .applyColorFilter(iconColor))
                    holder.wallsCounterTitle.setTextColor(labelColor)
                    holder.wallsCounterCount.setTextColor(counterColor)
                    holder.wallsCounterCount.text = wallsAmount.toString()
                } else {
                    holder.wallsCounter.gone()
                }
                
                if (kwgtAmount > MINIMAL_AMOUNT) {
                    holder.kwgtCounter.setBackgroundColor(bgColor)
                    holder.kwgtCounterIcon.setImageDrawable(
                            ContextCompat.getDrawable(context, NavigationItem.ICONS.icon)
                                    .applyColorFilter(iconColor))
                    holder.kwgtCounterTitle.setTextColor(labelColor)
                    holder.kwgtCounterCount.setTextColor(counterColor)
                    holder.kwgtCounterCount.text = kwgtAmount.toString()
                } else {
                    holder.kwgtCounter.gone()
                }
                
                if (zooperAmount > MINIMAL_AMOUNT) {
                    holder.zooperCounter.setBackgroundColor(bgColor)
                    holder.zooperCounterIcon.setImageDrawable(
                            ContextCompat.getDrawable(context, NavigationItem.ICONS.icon)
                                    .applyColorFilter(iconColor))
                    holder.zooperCounterTitle.setTextColor(labelColor)
                    holder.zooperCounterCount.setTextColor(counterColor)
                    holder.zooperCounterCount.text = zooperAmount.toString()
                } else {
                    holder.zooperCounter.gone()
                }
            } else if (holder is HomeItemsViewHolders.AppLinkItemHolder) {
                val rightPosition = if (shouldShowApplyCard) position - 2 else position - 1
                val item = list[rightPosition]
                if (!item.isAnApp && firstLinkPosition < 0) {
                    firstLinkPosition = rightPosition
                }
                holder.setItem(item, (rightPosition == 0 || rightPosition == firstLinkPosition),
                               listener)
            }
        } catch (ignored:Exception) {
        }
    }
    
    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):RecyclerView.ViewHolder? {
        return if (viewType == 0 && shouldShowApplyCard) {
            parent?.inflate(
                    R.layout.item_home_apply_card)?.let { HomeItemsViewHolders.ApplyCardHolder(it) }
        } else if (viewType == 1) {
            parent?.inflate(
                    R.layout.item_home_counters)?.let { HomeItemsViewHolders.CounterItemHolder(it) }
        } else {
            parent?.inflate(
                    R.layout.item_home_app_link)?.let { HomeItemsViewHolders.AppLinkItemHolder(it) }
        }
    }
    
    override fun getItemCount():Int = list.size + (if (shouldShowApplyCard) 2 else 1)
    
    override fun getItemViewType(position:Int):Int =
            if (shouldShowApplyCard) position else position + 1
}