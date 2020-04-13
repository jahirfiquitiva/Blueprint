package dev.jahir.blueprint.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.ui.activities.BlueprintActivity
import dev.jahir.blueprint.ui.activities.IconsCategoryActivity
import dev.jahir.blueprint.ui.adapters.IconsCategoriesAdapter
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.ui.fragments.base.BaseFramesFragment

class IconsCategoriesFragment : BaseFramesFragment<IconsCategory>() {

    private val iconsCategoriesAdapter: IconsCategoriesAdapter by lazy {
        IconsCategoriesAdapter(::onOpenCategory, ::onIconClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = iconsCategoriesAdapter
        recyclerView?.setHasFixedSize(true)
        loadData()
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

    private fun onOpenCategory(category: IconsCategory) {
        context?.startActivity(
            Intent(context, IconsCategoryActivity::class.java).apply {
                putExtra(IconsCategoryActivity.CATEGORY_KEY, category)
            })
    }

    private fun onIconClick(icon: Icon) {
        (activity as? BlueprintActivity)?.showIconDialog(icon)
    }

    companion object {
        internal const val TAG = "icons_categories_fragment"
    }
}