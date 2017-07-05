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
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/Blueprint#special-thanks
 */

package jahirfiquitiva.libs.blueprint.extensions

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.view.Menu

fun Menu.changeOptionVisibility(id:Int, visible:Boolean) {
    findItem(id)?.isVisible = visible
}

fun Menu.setItemTitle(id:Int, title:String) {
    findItem(id)?.title = title
}

fun Menu.setOptionIcon(id:Int, @DrawableRes iconRes:Int) {
    findItem(id)?.setIcon(iconRes)
}

fun Menu.setOptionIcon(id:Int, iconRes:Drawable) {
    findItem(id)?.icon = iconRes
}