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

package jahirfiquitiva.libs.iconshowcase.ui.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import jahirfiquitiva.libs.iconshowcase.R
import jahirfiquitiva.libs.iconshowcase.utils.ColorUtils
import jahirfiquitiva.libs.iconshowcase.utils.ResourceUtils
import jahirfiquitiva.libs.iconshowcase.utils.themes.ThemeUtils

class EmptyViewRecyclerView:RecyclerView {
    var loadingView:View? = null
    var emptyView:View? = null
    var textView:TextView? = null
    var loadingTextRes:Int = - 1
    var emptyTextRes:Int = - 1

    var state = STATE_LOADING

    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr)

    fun updateState(nState:Int) {
        state = nState
        updateStateViews()
    }

    private fun updateStateViews() {
        when (state) {
            STATE_LOADING -> {
                loadingView?.visibility = VISIBLE
                emptyView?.visibility = GONE
                textView?.text = ResourceUtils.getString(context,
                        if (loadingTextRes != - 1) loadingTextRes else R.string.loading_section)
                visibility = GONE
            }
            STATE_NORMAL -> {
                if (adapter != null) {
                    val items = adapter.itemCount
                    if (items > 0) {
                        loadingView?.visibility = GONE
                        emptyView?.visibility = GONE
                        visibility = VISIBLE
                    } else {
                        updateState(STATE_EMPTY)
                    }
                } else {
                    updateState(STATE_LOADING)
                }
            }
            STATE_EMPTY -> {
                loadingView?.visibility = GONE
                emptyView?.visibility = VISIBLE
                textView?.text = ResourceUtils.getString(context,
                        if (emptyTextRes != - 1) emptyTextRes else R.string.empty_section)
                visibility = GONE
            }
        }
        textView?.setTextColor(ColorUtils.getMaterialSecondaryTextColor(ThemeUtils.isDarkTheme()))
        textView?.visibility = if (state != STATE_NORMAL) VISIBLE else GONE
    }

    internal val observer:RecyclerView.AdapterDataObserver = object:RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateStateViews()
        }

        override fun onItemRangeChanged(positionStart:Int, itemCount:Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            updateStateViews()
        }

        override fun onItemRangeInserted(positionStart:Int, itemCount:Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            updateStateViews()
        }

        override fun onItemRangeRemoved(positionStart:Int, itemCount:Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            updateStateViews()
        }
    }

    override fun setAdapter(adapter:Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        updateStateViews()
    }

    companion object {
        const val STATE_LOADING = 0
        const val STATE_NORMAL = 1
        const val STATE_EMPTY = 2
    }
}