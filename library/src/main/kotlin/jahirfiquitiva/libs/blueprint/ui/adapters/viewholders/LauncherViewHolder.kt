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
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.withAlpha
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Launcher
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.frames.helpers.extensions.releaseFromGlide
import jahirfiquitiva.libs.frames.helpers.utils.GlideRequestCallback
import jahirfiquitiva.libs.kext.extensions.bestSwatch
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.boolean
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.formatCorrectly
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.resource
import jahirfiquitiva.libs.kext.extensions.secondaryTextColor
import jahirfiquitiva.libs.kext.extensions.toBitmap
import jahirfiquitiva.libs.kext.ui.widgets.CustomCardView
import jahirfiquitiva.libs.kuper.helpers.extensions.isAppInstalled

internal class LauncherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemLayout: CustomCardView? by bind(R.id.launcher_item)
    val bg: LinearLayout? by bind(R.id.launcher_bg)
    val iconView: ImageView? by bind(R.id.launcher_icon)
    val text: TextView? by bind(R.id.launcher_name)
    
    private val bnwFilter: ColorFilter
        get() {
            val matrix = ColorMatrix()
            matrix.setSaturation(0F)
            return ColorMatrixColorFilter(matrix)
        }
    
    fun bind(manager: RequestManager?, item: Launcher, listener: (Launcher) -> Unit = {}) =
        with(itemView) {
            val formattedName = item.name.replace("launcher", "", true).formatCorrectly()
            val iconName = formattedName.toLowerCase()
            text?.text = formattedName.blueprintFormat()
            val iconRes = try {
                context.resource("ic_$iconName")
            } catch (ignored: Exception) {
                context.resource("ic_na_launcher")
            }
            
            iconView?.colorFilter = null
            text?.background = null
            text?.setTextColor(context.secondaryTextColor)
            
            iconView?.let {
                (manager ?: Glide.with(context))
                    .load(iconRes)
                    .apply(
                        RequestOptions()
                            .priority(Priority.IMMEDIATE)
                            .placeholder(
                                context.drawable(R.drawable.ic_na_launcher))
                            .error(context.drawable(R.drawable.ic_na_launcher)))
                    .listener(object : GlideRequestCallback<Drawable>() {
                        override fun onLoadSucceed(resource: Drawable): Boolean {
                            val isInstalled =
                                isLauncherInstalled(context, item.packageNames)
                            iconView?.colorFilter = if (isInstalled) null else bnwFilter
                            resource.toBitmap().bestSwatch?.let {
                                val rightColor =
                                    if (isInstalled) it.rgb else context.secondaryTextColor
                                if (boolean(R.bool.enable_colored_cards)) {
                                    itemLayout?.radius = 0F
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                        itemLayout?.elevation = 0F
                                    itemLayout?.cardElevation = 0F
                                    itemLayout?.maxCardElevation = 0F
                                    bg?.setBackgroundColor(rightColor.withAlpha(0.8F))
                                }
                                text?.setBackgroundColor(rightColor)
                                text?.setTextColor(
                                    context.getPrimaryTextColorFor(rightColor, 0.6F))
                            }
                            return false
                        }
                    })
                    .into(it)
                    .clearOnDetach()
            }
            
            setOnClickListener { listener(item) }
        }
    
    private fun isLauncherInstalled(context: Context, packages: Array<String>): Boolean {
        packages.forEach {
            if (context.isAppInstalled(it)) return true
        }
        return false
    }
    
    fun unbind() {
        iconView?.releaseFromGlide()
    }
}