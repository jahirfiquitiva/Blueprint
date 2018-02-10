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

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.utils.scaleXY
import com.bumptech.glide.RequestManager
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.Icon
import jahirfiquitiva.libs.blueprint.helpers.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION
import jahirfiquitiva.libs.blueprint.helpers.utils.ICONS_ANIMATION_DURATION_DELAY
import jahirfiquitiva.libs.frames.helpers.extensions.loadResource
import jahirfiquitiva.libs.frames.helpers.extensions.releaseFromGlide
import jahirfiquitiva.libs.frames.helpers.utils.GlideRequestCallback
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.clearChildrenAnimations

class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private var lastPosition = -1
    val icon: ImageView? by bind(R.id.icon)
    
    fun bind(
            manager: RequestManager?,
            animate: Boolean,
            item: Icon,
            clickable: Boolean = true
            ) =
            with(itemView) {
                icon?.loadResource(
                        manager, item.icon, true, animate, true,
                        object : GlideRequestCallback<Drawable>() {
                            override fun onLoadSucceed(resource: Drawable): Boolean {
                                if (context.bpKonfigs.animationsEnabled && animate) {
                                    scaleXY = 0F
                                    lastPosition = adapterPosition
                                    animate().scaleX(1F)
                                            .scaleY(1F)
                                            .setStartDelay(ICONS_ANIMATION_DURATION_DELAY)
                                            .setDuration(ICONS_ANIMATION_DURATION)
                                            .start()
                                } else {
                                    icon?.setImageDrawable(resource)
                                    itemView.clearChildrenAnimations()
                                }
                                return false
                            }
                        })
                if (!clickable) {
                    icon?.isClickable = false
                    icon?.isFocusable = false
                    itemView.isClickable = false
                    itemView.isFocusable = false
                    icon?.isEnabled = false
                    icon?.background = null
                    itemView.isEnabled = false
                    itemView.background = null
                }
            }
    
    fun bind(
            manager: RequestManager?,
            animate: Boolean,
            item: Icon,
            clickable: Boolean = true,
            listener: (Icon) -> Unit = {}
            ) = with(itemView) {
        bind(manager, animate, item, clickable)
        setOnClickListener { listener(item) }
    }
    
    fun unbind() {
        icon?.releaseFromGlide()
    }
}