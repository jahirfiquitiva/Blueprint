package dev.jahir.blueprint.ui.activities

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.BlueprintPreferences
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.models.IconsCategory
import dev.jahir.blueprint.extensions.pickIcon
import dev.jahir.blueprint.ui.adapters.IconsAdapter
import dev.jahir.blueprint.ui.fragments.dialogs.IconDialog
import dev.jahir.frames.data.Preferences
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.dimenPixelSize
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.integer
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.fragments.mdDialog
import dev.jahir.frames.extensions.fragments.positiveButton
import dev.jahir.frames.extensions.fragments.singleChoiceItems
import dev.jahir.frames.extensions.fragments.title
import dev.jahir.frames.extensions.resources.hasContent
import dev.jahir.frames.extensions.resources.lower
import dev.jahir.frames.extensions.views.tint
import dev.jahir.frames.ui.activities.base.BaseSearchableActivity
import dev.jahir.frames.ui.decorations.GridSpacingItemDecoration

class IconsCategoryActivity : BaseSearchableActivity<Preferences>() {

    override val preferences: Preferences by lazy { Preferences(this) }

    private var pickerKey: Int = 0
    private var category: IconsCategory? = null
    private var iconDialog: IconDialog? = null

    private var iconsShapePickerDialog: AlertDialog? = null
    private val blueprintPrefs: BlueprintPreferences by lazy { BlueprintPreferences(this) }

    private val iconsAdapter: IconsAdapter by lazy {
        IconsAdapter(false, ::onIconClick).apply {
            icons = ArrayList(category?.getIcons().orEmpty())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icons_category)
        pickerKey = intent?.getIntExtra(PICKER_KEY, 0) ?: 0
        category = intent?.getParcelableExtra(CATEGORY_KEY)
        if (category == null) {
            finish()
            return
        }

        val toolbar: Toolbar? by findView(R.id.toolbar)
        setSupportActionBar(toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        toolbar?.title = category?.title ?: getAppName()
        toolbar?.tint()

        val recyclerView: FastScrollRecyclerView? by findView(R.id.recycler_view)
        recyclerView?.tint()
        recyclerView?.layoutManager =
            GridLayoutManager(
                this,
                integer(R.integer.icons_columns_count, 4),
                GridLayoutManager.VERTICAL,
                false
            )
        recyclerView?.addItemDecoration(
            GridSpacingItemDecoration(
                integer(
                    R.integer.icons_columns_count,
                    4
                ), dimenPixelSize(R.dimen.grids_spacing)
            )
        )
        recyclerView?.adapter = iconsAdapter
        recyclerView?.setHasFixedSize(true)
    }

    private fun dismissIconDialog() {
        try {
            iconDialog?.dismiss()
            iconDialog = null
        } catch (e: Exception) {
        }
    }

    private fun onIconClick(icon: Icon?, drawable: Drawable?) {
        icon ?: return
        if (pickerKey != 0) pickIcon(icon, drawable, pickerKey)
        else showIconDialog(icon)
    }

    private fun showIconDialog(icon: Icon?) {
        icon ?: return
        dismissIconDialog()
        iconDialog = IconDialog.create(icon)
        iconDialog?.show(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val created = super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.icons_shape)?.isVisible =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && boolean(R.bool.includes_adaptive_icons)
        return created
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        else if (item.itemId == R.id.icons_shape) showIconsShapePickerDialog()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissIconsShapesDialog()
        dismissIconDialog()
    }

    override fun getMenuRes(): Int = R.menu.icons_toolbar_menu
    override fun getSearchHint(itemId: Int): String = string(R.string.search_icons)

    override fun internalDoSearch(filter: String, closed: Boolean) {
        super.internalDoSearch(filter, closed)
        if (filter.hasContent() && !closed) {
            iconsAdapter.icons = ArrayList(
                category?.getIcons().orEmpty().filter { it.name.lower().contains(filter.lower()) })

        } else {
            iconsAdapter.icons = ArrayList(category?.getIcons().orEmpty())
        }
    }

    private fun dismissIconsShapesDialog() {
        try {
            iconsShapePickerDialog?.dismiss()
            iconsShapePickerDialog = null
        } catch (e: Exception) {
        }
    }

    private fun showIconsShapePickerDialog() {
        dismissIconsShapesDialog()
        iconsShapePickerDialog = mdDialog {
            title(R.string.icon_shape)
            singleChoiceItems(R.array.icon_shapes_options, blueprintPrefs.iconShape) { _, _ -> }
            positiveButton(android.R.string.ok) {
                val listView = (it as? AlertDialog)?.listView
                if ((listView?.checkedItemCount ?: 0) > 0) {
                    val prevOption = blueprintPrefs.iconShape
                    val selectedOption = listView?.checkedItemPosition ?: -1
                    if (selectedOption != prevOption) {
                        blueprintPrefs.iconShape = selectedOption
                        iconsAdapter.notifyDataSetChanged()
                    }
                }
                it.dismiss()
            }
        }
        iconsShapePickerDialog?.show()
    }

    companion object {
        internal const val CATEGORY_KEY = "category"
        internal const val PICKER_KEY = "picker_key"
    }
}