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

import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.pitchedapps.butler.iconrequest.App
import com.pitchedapps.butler.iconrequest.IconRequest
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.frames.helpers.extensions.releaseFromGlide
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly

class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val icon: ImageView = itemView.findViewById(R.id.icon)
    val text: TextView = itemView.findViewById(R.id.name)
    val checkbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
    
    fun setItem(app: App, listener: (checkbox: AppCompatCheckBox, item: App) -> Unit) {
        app.loadIcon(icon)
        text.text = app.name.formatCorrectly().replace("_", " ")
        val request = IconRequest.get()
        checkbox.isChecked = (request != null && request.isAppSelected(app))
        with(itemView) {
            isActivated = (request != null && request.isAppSelected(app))
            setOnClickListener { listener(checkbox, app) }
        }
    }
    
    fun doOnRecycle() {
        icon.releaseFromGlide()
    }
}