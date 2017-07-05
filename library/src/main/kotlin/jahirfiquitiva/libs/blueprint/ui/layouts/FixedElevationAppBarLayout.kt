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

package jahirfiquitiva.libs.blueprint.ui.layouts

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.utils.CoreUtils
import jahirfiquitiva.libs.blueprint.utils.ResourceUtils

class FixedElevationAppBarLayout:AppBarLayout {

    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)

    val fElevation:Int = CoreUtils.convertDpToPx(context,
                                                 ResourceUtils.getInteger(context,
                                                                          R.integer.toolbar_elevation).toFloat())

    override fun setElevation(elevation:Float) = super.setElevation(fElevation.toFloat())
}