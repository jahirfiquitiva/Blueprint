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
package jahirfiquitiva.libs.blueprint.ui.adapters.viewholders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.utils.scaleXY
import com.bumptech.glide.Glide
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION_DELAY
import jahirfiquitiva.libs.frames.helpers.extensions.clearChildrenAnimations
import jahirfiquitiva.libs.frames.helpers.extensions.loadResource
import jahirfiquitiva.libs.frames.helpers.extensions.releaseFromGlide
import jahirfiquitiva.libs.frames.helpers.utils.GlideRequestCallback
import jahirfiquitiva.libs.frames.ui.adapters.viewholders.GlideViewHolder

class IconViewHolder(itemView:View):GlideViewHolder(itemView) {
    
    var lastPosition = -1
    val icon:ImageView = itemView.findViewById(R.id.icon)
    
    fun bind(animate:Boolean, item:Icon) {
        bind(animate, item, {})
        itemView.isClickable = false
        itemView.isFocusable = false
    }
    
    fun bind(animate:Boolean, item:Icon, listener:(Icon) -> Unit) = with(itemView) {
        icon.loadResource(Glide.with(itemView.context), item.icon, true, animate, true,
                          object:GlideRequestCallback<Drawable>() {
                              override fun onLoadSucceed(resource:Drawable):Boolean {
                                  if (itemView.context.bpKonfigs.animationsEnabled && animate) {
                                      scaleXY = 0F
                                      setIconResource(resource)
                                      animate().scaleX(1F)
                                              .scaleY(1F)
                                              .setStartDelay(ICONS_ANIMATION_DURATION_DELAY)
                                              .setDuration(ICONS_ANIMATION_DURATION)
                                              .start()
                                  } else {
                                      icon.setImageDrawable(resource)
                                      itemView.clearChildrenAnimations()
                                  }
                                  return true
                              }
                          })
        setOnClickListener { listener(item) }
    }
    
    private fun setIconResource(resource:Drawable) {
        icon.setImageDrawable(resource)
        lastPosition = adapterPosition
    }
    
    override fun doOnRecycle() {
        icon.releaseFromGlide()
    }
}