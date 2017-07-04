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
 * 	https://github.com/jahirfiquitiva/IkoniK#special-thanks
 */

package jahirfiquitiva.libs.ikonik.ui.decorations

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class GridSpacingItemDecoration(private var spanCount:Int, private var spacing:Int,
                                private var includeEdge:Boolean):RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect:Rect?, view:View?, parent:RecyclerView?,
                                state:RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent!!.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {
            outRect?.left = spacing - column * spacing / spanCount // spacing - column * ((1f /
            // spanCount) * spacing)
            outRect?.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f /
            // spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect?.top = spacing
            }
            outRect?.bottom = spacing // item bottom
        } else run {
            outRect?.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect?.right = spacing - (column + 1) * spacing / spanCount // spacing - (column +
            // 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect?.top = spacing // item top
            }
        }
    }
}