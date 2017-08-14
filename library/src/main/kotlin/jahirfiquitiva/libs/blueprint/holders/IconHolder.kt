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

package jahirfiquitiva.libs.blueprint.holders

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.utils.scaleXY
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.models.Icon
import jahirfiquitiva.libs.blueprint.utils.ICONS_ANIMATION_DURATION
import jahirfiquitiva.libs.blueprint.utils.ICONS_ANIMATION_DURATION_DELAY

class IconHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
    
    var lastPosition = -1
    val icon:ImageView? = itemView?.findViewById(R.id.icon)
    
    fun bind(animate:Boolean, item:Icon) {
        bind(animate, item, {})
        itemView.isClickable = false
        itemView.isFocusable = false
    }
    
    fun bind(animate:Boolean, item:Icon, listener:(Icon) -> Unit) = with(itemView) {
        Glide.with(itemView?.context)
                .load(item.icon)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(object:BitmapImageViewTarget(icon) {
                    override fun setResource(resource:Bitmap) {
                        if (adapterPosition > lastPosition) {
                            if (itemView.context.bpKonfigs.animationsEnabled && animate) {
                                scaleXY = 0F
                                setIconResource(resource)
                                animate().scaleX(1F)
                                        .scaleY(1F)
                                        .setStartDelay(ICONS_ANIMATION_DURATION_DELAY)
                                        .setDuration(ICONS_ANIMATION_DURATION)
                                        .start()
                            } else {
                                setIconResource(resource)
                            }
                        } else {
                            icon?.setImageBitmap(resource)
                            clearAnimations()
                        }
                    }
                })
        setOnClickListener { listener(item) }
    }
    
    private fun setIconResource(resource:Bitmap) {
        icon?.alpha = 0F
        icon?.setImageBitmap(resource)
        icon?.animate()?.setDuration(ICONS_ANIMATION_DURATION)?.alpha(1f)?.start()
        lastPosition = adapterPosition
    }
    
    private fun clearAnimations() {
        itemView?.clearAnimation()
        icon?.clearAnimation()
    }
}