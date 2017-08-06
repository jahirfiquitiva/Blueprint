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

package jahirfiquitiva.libs.blueprint.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visibleIf
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.extensions.defaultLauncher
import jahirfiquitiva.libs.blueprint.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.holders.HomeItemsHolder
import jahirfiquitiva.libs.blueprint.models.HomeItem
import jahirfiquitiva.libs.frames.adapters.BaseListAdapter
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsColor
import jahirfiquitiva.libs.kauextensions.extensions.chipsIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor

class HomeItemsAdapter(val context:Context,
                       val listener:(HomeItem) -> Unit,
                       val iconsAmount:Int = 0, val wallpapersAmount:Int = 0,
                       val zooperAmount:Int = 0,
                       val kustomAmount:Int = 0):BaseListAdapter<HomeItem, RecyclerView.ViewHolder>() {
    
    var shouldShowApplyCard = false
    var firstLinkPosition = -1
    
    override fun doBind(holder:RecyclerView.ViewHolder, position:Int) {
        try {
            if (shouldShowApplyCard && position == 0 &&
                    holder is HomeItemsHolder.ApplyCardHolder) {
                val initCard = context.defaultLauncher?.isActuallySupported ?: false
                if (initCard) {
                    holder.applyTitle?.setTextColor(
                            context.getPrimaryTextColorFor(context.accentColor))
                    holder.applyTitle?.text = context.getString(R.string.apply_title,
                                                                context.getAppName())
                    holder.applyContent?.setTextColor(
                            context.getPrimaryTextColorFor(context.accentColor))
                    holder.applyContent?.text = context.getString(R.string.apply_content,
                                                                  context.defaultLauncher?.name)
                    holder.dismissButton?.setTextColor(context.getPrimaryTextColorFor(
                            context.accentColor))
                    holder.dismissButton?.setOnClickListener {
                        val anim = AnimationUtils.loadAnimation(context,
                                                                android.R.anim.slide_out_right)
                        anim.setAnimationListener(object:Animation.AnimationListener {
                            override fun onAnimationRepeat(p0:Animation?) {
                            }
                            
                            override fun onAnimationEnd(p0:Animation?) {
                                context.bpKonfigs.isApplyCardDismissed = true
                                notifyItemRemoved(0)
                                notifyDataSetChanged()
                            }
                            
                            override fun onAnimationStart(p0:Animation?) {
                            }
                        })
                        holder.itemView.startAnimation(anim)
                    }
                    holder.applyButton?.setTextColor(context.getPrimaryTextColorFor(
                            context.accentColor))
                    holder.applyButton?.setOnClickListener {
                        context.executeLauncherIntent(context.defaultLauncher?.name ?: "")
                    }
                }
            } else if (holder is HomeItemsHolder.ChipsItemHolder) {
                val labelColor = context.primaryTextColor
                val chipsIconColor = context.chipsIconsColor
                val chipsColor = context.chipsColor
                
                holder.chipsTitle?.setTextColor(context.secondaryTextColor)
                
                holder.iconsChip?.visibleIf(iconsAmount >= 0)
                holder.iconsChip?.label = context.getString(R.string.themed_icons,
                                                            iconsAmount.toString())
                holder.iconsChip?.setAvatarIcon(
                        "ic_icons_chip".getDrawable(context).tint(chipsIconColor))
                holder.iconsChip?.setLabelColor(labelColor)
                holder.iconsChip?.setChipBackgroundColor(chipsColor)
                holder.iconsChip?.requestLayout()
                
                holder.wallsChip?.visibleIf(wallpapersAmount >= 0)
                holder.wallsChip?.label = context.getString(R.string.available_wallpapers,
                                                            wallpapersAmount.toString())
                holder.wallsChip?.setAvatarIcon(
                        "ic_wallpapers_chip".getDrawable(context).tint(chipsIconColor))
                holder.wallsChip?.setChipBackgroundColor(chipsColor)
                holder.wallsChip?.requestLayout()
                
                holder.zooperChip?.visibleIf(zooperAmount >= 0)
                holder.zooperChip?.label = context.getString(R.string.included_zooper,
                                                             zooperAmount.toString())
                holder.zooperChip?.setAvatarIcon(
                        "ic_zooper_chip".getDrawable(context).tint(chipsIconColor))
                holder.zooperChip?.setChipBackgroundColor(chipsColor)
                holder.zooperChip?.requestLayout()
                
                holder.kustomChip?.visibleIf(kustomAmount >= 0)
                holder.kustomChip?.label = context.getString(R.string.included_kwgt,
                                                             kustomAmount.toString())
                holder.kustomChip?.setAvatarIcon(
                        "ic_kustom_chip".getDrawable(context).tint(chipsIconColor))
                holder.kustomChip?.setChipBackgroundColor(chipsColor)
                holder.kustomChip?.requestLayout()
                
                holder.widgetsChips?.visibleIf(zooperAmount >= 0 && kustomAmount >= 0)
                
            } else if (holder is HomeItemsHolder.AppLinkItemHolder) {
                val rightPosition = if (shouldShowApplyCard) position - 2 else position - 1
                val item = list[rightPosition]
                if (!item.isAnApp && firstLinkPosition < 0) {
                    firstLinkPosition = rightPosition
                }
                holder.setItem(
                        item,
                        (rightPosition == 0 || rightPosition == firstLinkPosition),
                        listener)
            }
        } catch (ignored:Exception) {
        }
    }
    
    override fun onCreateViewHolder(parent:ViewGroup?, viewType:Int):RecyclerView.ViewHolder {
        if (viewType == 0 && shouldShowApplyCard) {
            return HomeItemsHolder.ApplyCardHolder(parent?.inflate(R.layout.item_home_apply_card))
        } else if (viewType == 1) {
            return HomeItemsHolder.ChipsItemHolder(parent?.inflate(R.layout.item_home_chips))
        } else {
            return HomeItemsHolder.AppLinkItemHolder(parent?.inflate(R.layout.item_home_app_link))
        }
    }
    
    override fun getItemCount():Int = list.size + (if (shouldShowApplyCard) 2 else 1)
    
    override fun getItemViewType(position:Int):Int =
            if (shouldShowApplyCard) position else position + 1
}