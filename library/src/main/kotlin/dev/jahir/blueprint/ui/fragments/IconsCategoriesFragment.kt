package dev.jahir.blueprint.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.extensions.defaultLauncher
import dev.jahir.blueprint.extensions.pickIcon
import dev.jahir.blueprint.ui.activities.BlueprintActivity
import dev.jahir.blueprint.ui.activities.IconsCategoryActivity
import dev.jahir.blueprint.ui.adapters.IconsCategoriesAdapter
import dev.jahir.frames.extensions.resources.dpToPx
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.views.setPaddingBottom
import dev.jahir.frames.ui.activities.base.BaseSystemUIVisibilityActivity
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment

class IconsCategoriesFragment : BaseFramesFragment<IconsCategory>() {

    private val iconsCategoriesAdapter: IconsCategoriesAdapter by lazy {
        IconsCategoriesAdapter(::onOpenCategory, ::onIconClick)
    }

    private val fabHeight: Int
        get() {
            (activity as? BlueprintActivity)?.let {
                return if (it.initialItemId == R.id.icons && it.defaultLauncher != null)
                    it.fabBtn?.measuredHeight ?: 0
                else 0
            } ?: return 0
        }

    private val extraHeight: Int
        get() = if (fabHeight > 0) 16.dpToPx else 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
                isItemPrefetchEnabled = true
            }
        recyclerView?.adapter = iconsCategoriesAdapter
        recyclerView?.setHasFixedSize(true)
        loadData()
    }

    override fun setupContentBottomOffset(view: View?) {
        (view ?: getView())?.let { v ->
            v.post {
                val bottomNavigationHeight =
                    (context as? BaseSystemUIVisibilityActivity<*>)?.bottomNavigation?.measuredHeight
                        ?: 0
                v.setPaddingBottom(bottomNavigationHeight)
                recyclerView?.setupBottomOffset(fabHeight + extraHeight)
            }
        }
    }

    override fun loadData() {
        (activity as? BlueprintActivity)?.loadIconsCategories()
    }

    override fun getFilteredItems(
        originalItems: ArrayList<IconsCategory>,
        filter: String
    ): ArrayList<IconsCategory> {
        if (!filter.hasContent()) return originalItems
        val filteredItems: ArrayList<IconsCategory> = ArrayList()
        originalItems.forEach {
            val filteredIcons =
                it.getIcons().filter { icon -> icon.name.lower().contains(filter.lower()) }
            if (it.title.lower().contains(filter.lower()) || filteredIcons.isNotEmpty()) {
                val pair: Pair<ArrayList<Icon>, Boolean> =
                    if (filteredIcons.isNotEmpty())
                        Pair(ArrayList(filteredIcons), true)
                    else Pair(it.getIcons(), false)
                filteredItems.add(IconsCategory(it.title, pair.first, pair.second))
            }
        }
        return filteredItems
    }

    override fun updateItemsInAdapter(items: ArrayList<IconsCategory>) {
        iconsCategoriesAdapter.categories = items
    }

    internal fun notifyShapeChange() {
        iconsCategoriesAdapter.notifyDataSetChanged()
    }

    private fun onOpenCategory(category: IconsCategory) {
        val pickerKey = (activity as? BlueprintActivity)?.pickerKey ?: 0
        startActivityForResult(
            Intent(context, IconsCategoryActivity::class.java).apply {
                putExtra(IconsCategoryActivity.CATEGORY_KEY, category)
                putExtra(IconsCategoryActivity.PICKER_KEY, pickerKey)
            }, if (pickerKey != 0) 55 else 54
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 55) {
            activity?.let {
                it.setResult(resultCode, data)
                it.finish()
            }
        }
    }

    private fun onIconClick(icon: Icon, drawable: Drawable?) {
        val pickerKey = (activity as? BlueprintActivity)?.pickerKey ?: 0
        if (pickerKey != 0) activity?.pickIcon(icon, drawable, pickerKey)
        else (activity as? BlueprintActivity)?.showIconDialog(icon)
    }

    companion object {
        const val TAG = "icons_categories_fragment"
    }
}