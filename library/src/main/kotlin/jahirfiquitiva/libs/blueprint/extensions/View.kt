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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import jahirfiquitiva.libs.blueprint.R

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun View.makeInvisibleIf(
        makeInvisible:Boolean) = if (makeInvisible) makeInvisible() else makeVisible()

fun View.makeVisibleIf(makeVisible:Boolean) = if (makeVisible) makeVisible() else makeGone()

fun View.makeGoneIf(makeGone:Boolean) = makeVisibleIf(!makeGone)

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun ViewGroup.inflate(layoutId:Int, attachToRoot:Boolean = false):View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun View.updateLeftMargin(left:Int = -1) = updateMargins(left)

fun View.updateTopMargin(top:Int = -1) = updateMargins(-1, top)

fun View.updateRightMargin(right:Int = -1) = updateMargins(-1, -1, right)

fun View.updateBottomMargin(bottom:Int = -1) = updateMargins(-1, -1, -1, bottom)

fun View.updateMargins(left:Int = -1, top:Int = -1, right:Int = -1, bottom:Int = -1) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        val newLeft = if (left >= 0) left else p.leftMargin
        val newTop = if (top >= 0) top else p.topMargin
        val newRight = if (right >= 0) right else p.rightMargin
        val newBottom = if (bottom >= 0) bottom else p.bottomMargin
        p.setMargins(newLeft, newTop, newRight, newBottom)
        requestLayout()
    }
}

fun ImageView.loadFromUrl(url:String) {
    if (url.isEmpty()) {
        Glide.with(context).load(R.drawable.ic_wallpapers).into(this)
    } else {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(this)
    }
}