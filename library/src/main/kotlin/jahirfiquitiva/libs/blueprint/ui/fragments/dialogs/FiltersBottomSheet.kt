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
package jahirfiquitiva.libs.blueprint.ui.fragments.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.setPaddingTop
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.models.Filter
import jahirfiquitiva.libs.blueprint.ui.adapters.FiltersAdapter
import jahirfiquitiva.libs.kext.extensions.cardBackgroundColor
import jahirfiquitiva.libs.kext.extensions.context
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kext.extensions.isInHorizontalMode
import jahirfiquitiva.libs.kext.extensions.string

class FiltersBottomSheet : BottomSheetDialogFragment() {
    
    private var recyclerView: RecyclerView? = null
    private var progress: ProgressBar? = null
    private var titleView: TextView? = null
    private var clearIcon: ImageView? = null
    private var collapseIcon: ImageView? = null
    
    private var behavior: BottomSheetBehavior<*>? = null
    
    private val filters = ArrayList<Filter>()
    private val activeFilters = ArrayList<Filter>()
    
    private val adapter: FiltersAdapter by lazy {
        FiltersAdapter { filter, checked ->
            adapter.toggleFilter(filter, checked)
            doOnFiltersChange(adapter.selectedFilters)
        }
    }
    
    private val sheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            var correctAlpha = slideOffset + 1
            if (correctAlpha < 0) correctAlpha = 0.0F
            if (correctAlpha > 1) correctAlpha = 1.0F
            bottomSheet.alpha = correctAlpha
        }
        
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
        }
    }
    
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        
        val detailView = View.inflate(context, R.layout.filters_dialog, null)
        
        titleView = detailView?.findViewById(R.id.dialog_title)
        titleView?.text = string(R.string.filter, "Filter")
        
        clearIcon = detailView?.findViewById(R.id.clear_icon)
        collapseIcon = detailView?.findViewById(R.id.collapse_icon)
        
        context {
            val iconsColor = it.getActiveIconsColorFor(it.cardBackgroundColor)
            clearIcon?.setImageDrawable(it.drawable("ic_clear")?.tint(iconsColor))
            collapseIcon?.setImageDrawable(it.drawable("ic_section_expand")?.tint(iconsColor))
        }
        
        clearIcon?.setOnClickListener {
            adapter.updateSelectedFilters(ArrayList())
            doOnFiltersChange(ArrayList())
        }
        
        collapseIcon?.setOnClickListener {
            animateHide()
        }
        
        progress = detailView?.findViewById(R.id.loading_view)
        progress?.visible()
        
        recyclerView = detailView?.findViewById(R.id.info_rv)
        recyclerView?.gone()
        recyclerView?.setPaddingTop(8.dpToPx)
        
        adapter.setHasStableIds(false)
        adapter.updateSelectedFilters(activeFilters)
        adapter.setItems(filters)
        recyclerView?.adapter = adapter
        
        val layoutManager = GridLayoutManager(
            context, if (context?.isInHorizontalMode == true) 3 else 2,
            GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = layoutManager
        
        recyclerView?.itemAnimator = DefaultItemAnimator()
        
        dialog?.setContentView(detailView)
        
        val params = (detailView.parent as? View)?.layoutParams as? CoordinatorLayout.LayoutParams
        val parentBehavior = params?.behavior
        
        if (parentBehavior != null && parentBehavior is BottomSheetBehavior<*>) {
            behavior = parentBehavior
            behavior?.setBottomSheetCallback(sheetCallback)
        }
        
        progress?.gone()
        recyclerView?.visible()
    }
    
    fun show(context: FragmentActivity) {
        show(context.supportFragmentManager, TAG)
    }
    
    override fun show(manager: FragmentManager?, tag: String?) {
        super.show(manager, tag)
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }
    
    fun animateHide() {
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }
    
    private var doOnFiltersChange: (ArrayList<Filter>) -> Unit = {}
    
    companion object {
        private const val TAG = "FiltersBottomSheet"
        
        fun build(
            filters: ArrayList<Filter>,
            activeFilters: ArrayList<Filter>,
            onFiltersChange: (ArrayList<Filter>) -> Unit = {}
                 ): FiltersBottomSheet =
            FiltersBottomSheet().apply {
                this.filters.clear()
                this.filters.addAll(filters)
                this.activeFilters.clear()
                this.activeFilters.addAll(activeFilters)
                this.doOnFiltersChange = onFiltersChange
            }
        
        fun show(
            context: FragmentActivity,
            filters: ArrayList<Filter>,
            activeFilters: ArrayList<Filter>,
            onFiltersChange: (ArrayList<Filter>) -> Unit = {}
                ) =
            build(filters, activeFilters, onFiltersChange).show(context.supportFragmentManager, TAG)
    }
}