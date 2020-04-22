package dev.jahir.blueprint.ui.activities

import android.os.Bundle
import android.view.MenuItem
import com.github.javiersantos.piracychecker.PiracyChecker
import dev.jahir.blueprint.R
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.ui.fragments.WallpapersFragment
import dev.jahir.kuper.ui.activities.KuperActivity

class BlueprintKuperActivity : KuperActivity() {
    override val wallpapersFragment: WallpapersFragment? = null
    override fun getLicKey(): String? = ""
    override fun getLicenseChecker(): PiracyChecker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavigation?.menu?.clear()
        bottomNavigation?.inflateMenu(R.menu.kuper_navigation_menu)
        bottomNavigation?.setSelectedItemId(initialItemId, false)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) supportFinishAfterTransition()
        return super.onOptionsItemSelected(item)
    }

    override fun getMenuRes(): Int = R.menu.templates_toolbar_menu
    override fun getToolbarTitleForItem(itemId: Int): String? = string(R.string.templates)

    override val initialFragmentTag: String = "RequiredAppsFragment"
    override val initialItemId: Int = R.id.setup
    override fun shouldShowToolbarLogo(itemId: Int): Boolean = false
}