/*
 * Copyright (c) 2017.  Jahir Fiquitiva
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
 *
 * Special thanks to the project contributors and collaborators
 *   https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.models.holders

import android.support.v7.widget.AppCompatCheckBox

class FilterCheckBoxHolder {

    var checkBox:AppCompatCheckBox? = null
    var title:String = ""
    var listener:StateChangeListener? = null

    fun setup(checkBox:AppCompatCheckBox, title:String, listener:StateChangeListener?) {
        if (this.checkBox != null) return
        this.checkBox = checkBox
        this.title = title
        this.listener = listener
    }

    fun apply(checked:Boolean, fireFiltersListener:Boolean = true) {
        checkBox?.isChecked = checked
        listener?.onStateChanged(checked, title, fireFiltersListener)
    }

    fun isChecked():Boolean = checkBox?.isChecked ?: false

    interface StateChangeListener {
        fun onStateChanged(checked:Boolean, title:String, fireFiltersListener:Boolean)
    }
}