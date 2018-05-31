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
package jahirfiquitiva.libs.blueprint.ui.widgets

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

class NonSwipeableViewPager : ViewPager {
    constructor(context: Context) : super(context) {
        setCustomScroller()
    }
    
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        setCustomScroller()
    }
    
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = false
    override fun onTouchEvent(ev: MotionEvent?): Boolean = false
    
    private fun setCustomScroller() = try {
        val klass = ViewPager::class.java
        val scrollerField = klass.getDeclaredField("mScroller")
        scrollerField.isAccessible = true
        scrollerField.set(this, CustomScroller(context))
    } catch (ignored: Exception) {
    }
    
    internal class CustomScroller(context: Context) : Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(
            startX: Int, startY: Int, dx: Int, dy: Int,
            duration: Int
                                ) = super.startScroll(startX, startY, dx, dy, 350)
    }
}