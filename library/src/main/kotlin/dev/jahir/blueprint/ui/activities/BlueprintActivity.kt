package dev.jahir.blueprint.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.data.viewmodels.HomeViewModel
import dev.jahir.blueprint.data.viewmodels.IconsCategoriesViewModel
import dev.jahir.blueprint.ui.fragments.HomeFragment
import dev.jahir.blueprint.ui.fragments.IconsCategoriesFragment
import dev.jahir.blueprint.ui.fragments.dialogs.IconDialog
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.utils.lazyViewModel
import dev.jahir.frames.ui.activities.FramesActivity
import dev.jahir.frames.ui.fragments.CollectionsFragment
import dev.jahir.frames.ui.fragments.WallpapersFragment
import dev.jahir.kuper.data.viewmodels.ComponentsViewModel
import dev.jahir.kuper.ui.fragments.KuperWallpapersFragment

abstract class BlueprintActivity : FramesActivity() {

    override val collectionsFragment: CollectionsFragment? = null
    override val favoritesFragment: WallpapersFragment? = null

    override val wallpapersFragment: WallpapersFragment? by lazy {
        KuperWallpapersFragment.create(ArrayList(wallpapersViewModel.wallpapers))
    }

    private val homeFragment: HomeFragment by lazy { HomeFragment() }
    private val iconsCategoriesFragment: IconsCategoriesFragment by lazy { IconsCategoriesFragment() }

    private val homeViewModel: HomeViewModel by lazyViewModel()
    private val iconsViewModel: IconsCategoriesViewModel by lazyViewModel()
    private val templatesViewModel: ComponentsViewModel by lazyViewModel()

    private var iconDialog: IconDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavigation?.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        wallpapersViewModel.observeWallpapers(this) {
            wallpapersFragment?.updateItems(ArrayList(it))
            homeFragment.updateWallpapersCount(it.size)
        }

        homeViewModel.observeIconsPreviewList(this) { homeFragment.updateIconsPreview(it) }
        homeViewModel.observeHomeItems(this) { homeFragment.updateHomeItems(it) }
        iconsViewModel.observe(this) {
            iconsCategoriesFragment.updateItems(it)
            homeFragment.updateIconsCount(iconsViewModel.iconsCount)
        }
        templatesViewModel.observe(this) { homeFragment.updateComponentsCount(it) }

        homeViewModel.loadHomeItems(this)
        loadPreviewIcons()
        if (boolean(R.bool.show_overview)) templatesViewModel.loadComponents(this)
        loadIconsCategories()
        requestStoragePermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val created = super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.select_all)?.isVisible = currentItemId == R.id.request
        return created
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.templates -> startActivity(Intent(this, BlueprintKuperActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissIconDialog()
        homeViewModel.destroy(this)
        iconsViewModel.destroy(this)
        templatesViewModel.destroy(this)
    }

    private fun dismissIconDialog() {
        try {
            iconDialog?.dismiss()
            iconDialog = null
        } catch (e: Exception) {
        }
    }

    override fun getNextFragment(itemId: Int): Pair<Pair<String?, Fragment?>?, Boolean>? =
        when (itemId) {
            R.id.home -> Pair(Pair(HomeFragment.TAG, homeFragment), true)
            R.id.icons -> Pair(Pair(IconsCategoriesFragment.TAG, iconsCategoriesFragment), true)
            else -> super.getNextFragment(itemId)
        }

    override fun internalOnPermissionsGranted(result: List<PermissionStatus>) {
        super.internalOnPermissionsGranted(result)
        homeFragment.updateWallpaper()
    }

    override fun canShowSearch(itemId: Int): Boolean =
        when (itemId) {
            R.id.home -> false
            else -> super.canShowSearch(itemId)
        }

    override val initialFragmentTag: String = HomeFragment.TAG
    override val initialItemId: Int = R.id.home
    override fun getMenuRes(): Int = R.menu.blueprint_toolbar_menu
    override fun shouldLoadCollections(): Boolean = false
    override fun shouldLoadFavorites(): Boolean = false

    internal fun loadPreviewIcons(force: Boolean = false) {
        homeViewModel.loadPreviewIcons(this, force)
    }

    internal fun loadIconsCategories() {
        iconsViewModel.loadIconsCategories(this)
    }

    internal fun showIconDialog(icon: Icon?) {
        icon ?: return
        dismissIconDialog()
        iconDialog = IconDialog.create(icon)
        iconDialog?.show(this)
    }
}