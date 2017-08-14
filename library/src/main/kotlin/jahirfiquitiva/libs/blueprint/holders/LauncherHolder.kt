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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.isAppInstalled
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.extensions.bpKonfigs
import jahirfiquitiva.libs.blueprint.models.Launcher
import jahirfiquitiva.libs.kauextensions.extensions.bestSwatch
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly
import jahirfiquitiva.libs.kauextensions.extensions.getBoolean
import jahirfiquitiva.libs.kauextensions.extensions.getIconResource
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.withAlpha
import jahirfiquitiva.libs.kauextensions.ui.views.CustomCardView

class LauncherHolder(itemView:View?):RecyclerView.ViewHolder(itemView) {
    var lastPosition = -1
    
    val itemLayout:CustomCardView? = itemView?.findViewById(R.id.launcher_item)
    val bg:LinearLayout? = itemView?.findViewById(R.id.launcher_bg)
    val icon:ImageView? = itemView?.findViewById(R.id.launcher_icon)
    val text:TextView? = itemView?.findViewById(R.id.launcher_name)
    
    private val bnwFilter:ColorFilter
        get() {
            val matrix = ColorMatrix()
            matrix.setSaturation(0F)
            return ColorMatrixColorFilter(matrix)
        }
    
    fun bind(item:Launcher, listener:(Launcher) -> Unit = {}) = with(itemView) {
        val formattedName = item.name.replace("launcher", "", true).formatCorrectly()
        val iconName = formattedName.toLowerCase()
        text?.text = formattedName.blueprintFormat()
        var bits:Int
        try {
            bits = ("ic_" + iconName).getIconResource(context)
        } catch (ignored:Exception) {
            bits = "ic_na_launcher".getIconResource(context)
        }
        Glide.with(itemView?.context)
                .load(bits)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(object:BitmapImageViewTarget(icon) {
                    override fun setResource(resource:Bitmap) {
                        val isInstalled = isLauncherInstalled(context, item.packageNames)
                        setIconResource(resource, isInstalled)
                        Palette.from(resource).generate().bestSwatch?.let {
                            val rightColor = if (isInstalled) it.rgb else context.secondaryTextColor
                            if (context.getBoolean(R.bool.enable_colored_cards)) {
                                itemLayout?.radius = 0F
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                    itemLayout?.elevation = 0F
                                itemLayout?.cardElevation = 0F
                                itemLayout?.maxCardElevation = 0F
                                bg?.setBackgroundColor(rightColor.withAlpha(0.8F))
                            }
                            text?.setBackgroundColor(rightColor)
                            text?.setTextColor(context.getPrimaryTextColorFor(rightColor, 0.6F))
                        }
                    }
                })
        setOnClickListener { listener(item) }
    }
    
    private fun isLauncherInstalled(context:Context, packages:Array<String>):Boolean {
        packages.forEach {
            if (context.isAppInstalled(it)) return true
        }
        return false
    }
    
    private fun setIconResource(resource:Bitmap, isInstalled:Boolean) {
        val filter:ColorFilter? = if (isInstalled) null else bnwFilter
        if (adapterPosition > lastPosition) {
            if (itemView.context.bpKonfigs.animationsEnabled) {
                icon?.alpha = 0F
                icon?.setImageBitmap(resource)
                icon?.colorFilter = filter
                icon?.animate()?.setDuration(250)?.alpha(1f)?.start()
            } else {
                icon?.setImageBitmap(resource)
                icon?.colorFilter = filter
            }
        } else {
            icon?.setImageBitmap(resource)
            icon?.colorFilter = filter
            clearAnimations()
        }
        lastPosition = adapterPosition
    }
    
    private fun clearAnimations() {
        itemView?.clearAnimation()
        icon?.clearAnimation()
    }
}