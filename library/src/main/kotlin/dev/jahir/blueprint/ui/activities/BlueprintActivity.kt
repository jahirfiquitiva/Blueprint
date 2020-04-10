package dev.jahir.blueprint.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import dev.jahir.blueprint.R
import dev.jahir.blueprint.ui.fragments.BlueprintWallpapersFragment
import dev.jahir.blueprint.ui.fragments.HomeFragment
import dev.jahir.frames.ui.activities.FramesActivity
import dev.jahir.frames.ui.fragments.CollectionsFragment
import dev.jahir.frames.ui.fragments.WallpapersFragment

abstract class BlueprintActivity : FramesActivity() {

    override val collectionsFragment: CollectionsFragment? = null
    override val favoritesFragment: WallpapersFragment? = null

    override val wallpapersFragment: WallpapersFragment? by lazy {
        BlueprintWallpapersFragment.create(ArrayList(wallpapersViewModel.wallpapers))
    }

    private val homeFragment: HomeFragment by lazy { HomeFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigation?.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        requestStoragePermission()
    }

    override fun getNextFragment(itemId: Int): Pair<Pair<String?, Fragment?>?, Boolean>? =
        when (itemId) {
            R.id.home -> Pair(Pair(HomeFragment.TAG, homeFragment), true)
            else -> super.getNextFragment(itemId)
        }

    override fun internalOnPermissionsGranted(result: List<PermissionStatus>) {
        super.internalOnPermissionsGranted(result)
        homeFragment.updateWallpaper()
    }

    override val initialFragmentTag: String = HomeFragment.TAG
    override val initialItemId: Int = R.id.home
}