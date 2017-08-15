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

package jahirfiquitiva.libs.blueprint.views

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import jahirfiquitiva.libs.kauextensions.ui.layouts.FixedElevationAppBarLayout

class CustomAppBarLayout:FixedElevationAppBarLayout {
    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    
    fun allowScroll(allow:Boolean) {
        isActivated = allow
        isEnabled = allow
        try {
            val params = layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior as AppBarLayout.Behavior
            behavior.setDragCallback(object:Behavior.DragCallback() {
                override fun canDrag(appBarLayout:AppBarLayout):Boolean = allow
            })
            params.behavior = behavior
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}