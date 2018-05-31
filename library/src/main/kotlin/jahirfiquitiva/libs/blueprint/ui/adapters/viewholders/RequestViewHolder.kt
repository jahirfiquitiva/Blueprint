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
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.quest.App
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.frames.helpers.extensions.releaseFromGlide
import jahirfiquitiva.libs.frames.helpers.utils.GlideRequestCallback
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.context
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.formatCorrectly
import jahirfiquitiva.libs.kext.ui.widgets.SquaredImageView

class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val icon: SquaredImageView? by bind(R.id.icon)
    private val text: TextView? by bind(R.id.name)
    private val checkbox: AppCompatCheckBox? by bind(R.id.checkbox)
    private val progressBar: ProgressBar? by bind(R.id.icon_progress)
    
    fun setItem(
        manager: RequestManager?,
        app: App,
        listener: (checkbox: AppCompatCheckBox, item: App) -> Unit
               ) {
        icon?.let {
            (manager ?: Glide.with(context))
                .load(app.icon)
                .apply(
                    RequestOptions()
                        .priority(Priority.IMMEDIATE)
                        .error(context.drawable(R.drawable.ic_na_launcher)))
                .listener(object : GlideRequestCallback<Drawable>() {
                    override fun onLoadSucceed(resource: Drawable): Boolean {
                        progressBar?.gone()
                        return false
                    }
                    
                    override fun onLoadFailed(): Boolean {
                        progressBar?.gone()
                        it.setImageDrawable(context.drawable(R.drawable.ic_na_launcher))
                        return super.onLoadFailed()
                    }
                })
                .into(it)
                .clearOnDetach()
        }
        
        text?.text = app.name.formatCorrectly().replace("_", " ")
        val request = IconRequest.get()
        checkbox?.isChecked = (request != null && request.isAppSelected(app))
        with(itemView) {
            isActivated = (request != null && request.isAppSelected(app))
            checkbox?.let { check ->
                setOnClickListener { listener(check, app) }
            }
        }
    }
    
    fun unbind() {
        icon?.releaseFromGlide()
    }
}