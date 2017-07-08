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

package jahirfiquitiva.libs.blueprint.ui.views

import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.extensions.getSecondaryTextColor
import jahirfiquitiva.libs.blueprint.extensions.makeGone
import jahirfiquitiva.libs.blueprint.extensions.makeVisible
import jahirfiquitiva.libs.blueprint.extensions.makeVisibleIf

class EmptyViewRecyclerView:RecyclerView {
    var loadingView:View? = null
    var emptyView:View? = null
    var textView:TextView? = null
    var loadingTextRes:Int = -1
    var emptyTextRes:Int = -1

    var state = STATE_LOADING
        set(@STATE value) {
            field = value
            updateStateViews()
        }

    constructor(context:Context):super(context)
    constructor(context:Context, attributeSet:AttributeSet):super(context, attributeSet)
    constructor(context:Context, attributeSet:AttributeSet, defStyleAttr:Int)
            :super(context, attributeSet, defStyleAttr)

    private fun updateStateViews() {
        when (state) {
            STATE_LOADING -> {
                loadingView?.makeVisible()
                emptyView?.makeGone()
                textView?.text = context.getString(
                        if (loadingTextRes != -1) loadingTextRes else R.string.loading_section)
                makeGone()
            }
            STATE_NORMAL -> {
                if (adapter != null) {
                    val items = adapter.itemCount
                    if (items > 0) {
                        loadingView?.makeGone()
                        emptyView?.makeGone()
                        makeVisible()
                    } else {
                        state = STATE_EMPTY
                    }
                } else {
                    state = STATE_LOADING
                }
            }
            STATE_EMPTY -> {
                loadingView?.makeGone()
                emptyView?.makeVisible()
                textView?.text = context.getString(
                        if (emptyTextRes != -1) emptyTextRes else R.string.empty_section)
                makeGone()
            }
        }
        textView?.setTextColor(context.getSecondaryTextColor())
        textView?.makeVisibleIf(state != STATE_NORMAL)
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

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STATE_EMPTY.toLong(), STATE_LOADING.toLong(), STATE_NORMAL.toLong())
    annotation class STATE
}