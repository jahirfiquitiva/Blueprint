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
package jahirfiquitiva.libs.blueprint.helpers.extensions

import android.graphics.PorterDuff
import android.support.v7.widget.Toolbar
import ca.allanwang.kau.utils.blendWith
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.statusBarLight
import com.mikepenz.materialdrawer.Drawer
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryDarkColor
import jahirfiquitiva.libs.kauextensions.extensions.round
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.ui.activities.ThemedActivity
import jahirfiquitiva.libs.kauextensions.ui.callbacks.CollapsingToolbarCallback

fun ThemedActivity.updateToolbarColors(
        toolbar: Toolbar, drawer: Drawer?, offset: Int,
        darkness: Float = 0.5F
                                      ) {
    val defaultIconsColor = color(android.R.color.white)
    var ratio = (offset / 255.0).round(1)
    if (ratio > 1) ratio = 1.0
    else if (ratio < 0) ratio = 0.0
    val rightIconsColor = defaultIconsColor.blendWith(
            getActiveIconsColorFor(primaryColor, darkness),
            ratio.toFloat())
    toolbar.tint(rightIconsColor)
    try {
        drawer?.actionBarDrawerToggle?.drawerArrowDrawable?.setColorFilter(
                rightIconsColor,
                PorterDuff.Mode.SRC_ATOP)
        drawer?.actionBarDrawerToggle?.syncState()
    } catch (ignored: Exception) {
    }
    updateStatusBarStyle(
            if (ratio > 0.9) CollapsingToolbarCallback.State.COLLAPSED else CollapsingToolbarCallback.State.EXPANDED,
            darkness)
}

fun ThemedActivity.updateStatusBarStyle(state: CollapsingToolbarCallback.State, darkness: Float) {
    statusBarLight = if (state == CollapsingToolbarCallback.State.COLLAPSED)
        primaryDarkColor.isColorLight(darkness) else false
}