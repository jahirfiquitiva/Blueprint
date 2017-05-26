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
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.libs.iconshowcase.callbacks

import android.support.design.widget.AppBarLayout

abstract class CollapsingToolbarCallback:AppBarLayout.OnOffsetChangedListener {

    var currentState = State.IDLE

    override fun onOffsetChanged(appBarLayout:AppBarLayout?, verticalOffset:Int) {
        onVerticalOffsetChanged(appBarLayout, Math.abs(verticalOffset))
        /*
        if (verticalOffset == 0) {
            if (currentState != State.EXPANDED)
                onStateChanged(appBarLayout, State.EXPANDED)
            currentState = State.EXPANDED
        } else if (Math.abs(verticalOffset) >= appBarLayout?.totalScrollRange) {
            if (currentState != State.COLLAPSED)
                onStateChanged(appBarLayout, State.COLLAPSED)
            currentState = State.COLLAPSED
        } else {
            if (currentState != State.IDLE)
                onStateChanged(appBarLayout, State.IDLE)
            currentState = State.IDLE
        }
        */
    }

    open fun onStateChanged(appBar:AppBarLayout?, state:State) {}

    open fun onVerticalOffsetChanged(appBar:AppBarLayout?, verticalOffset:Int) {}

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }
}