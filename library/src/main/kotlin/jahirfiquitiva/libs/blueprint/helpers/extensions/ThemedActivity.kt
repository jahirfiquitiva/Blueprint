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

package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.support.v7.widget.Toolbar
import ca.allanwang.kau.utils.blendWith
import com.mikepenz.materialdrawer.Drawer
import jahirfiquitiva.libs.kauextensions.activities.ThemedActivity
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getColorFromRes
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.round
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.extensions.updateStatusBarStyle
import jahirfiquitiva.libs.kauextensions.ui.views.callbacks.CollapsingToolbarCallback

fun ThemedActivity.updateToolbarColors(toolbar:Toolbar, drawer:Drawer?, offset:Int) {
    val defaultIconsColor = getColorFromRes(android.R.color.white)
    var ratio = round(offset / 255.0, 1)
    if (ratio > 1) ratio = 1.0
    else if (ratio < 0) ratio = 0.0
    val rightIconsColor = defaultIconsColor.blendWith(getActiveIconsColorFor(primaryColor),
                                                      ratio.toFloat())
    toolbar.tint(rightIconsColor)
    try {
        drawer?.actionBarDrawerToggle?.drawerArrowDrawable?.color = rightIconsColor
    } catch (ignored:Exception) {
    }
    updateStatusBarStyle(
            if (ratio > 0.7) CollapsingToolbarCallback.State.COLLAPSED else CollapsingToolbarCallback.State.EXPANDED)
}