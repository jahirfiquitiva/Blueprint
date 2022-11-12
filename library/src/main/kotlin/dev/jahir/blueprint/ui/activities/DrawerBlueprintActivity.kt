package dev.jahir.blueprint.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.enableTranslucentStatusBar
import dev.jahir.blueprint.extensions.getOptimalDrawerWidth
import dev.jahir.blueprint.extensions.setOptimalDrawerHeaderHeight
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.currentVersionName
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.views.tint
import dev.jahir.frames.extensions.views.visibleIf
import dev.jahir.kuper.data.models.Component

abstract class DrawerBlueprintActivity : BlueprintActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout: DrawerLayout? by findView(R.id.drawer_layout)
    private val navigationView: NavigationView? by findView(R.id.navigation_view)
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableTranslucentStatusBar()

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle?.drawerArrowDrawable?.color =
            resolveColor(R.attr.colorOnPrimary, color(R.color.onPrimary))
        toggle?.let { drawerLayout?.addDrawerListener(it) }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar?.tint()

        val header =
            navigationView?.findViewById(R.id.navigation_header) ?: navigationView?.getHeaderView(0)
        header?.let { setOptimalDrawerHeaderHeight(it) }

        val headerDrawable = drawable(R.drawable.drawer_header)
        headerDrawable?.let { header?.background = it }
            ?: header?.setBackgroundColor(resolveColor(R.attr.colorAccent, color(R.color.accent)))

        val drawerTexts: View? = header?.findViewById(R.id.drawer_texts)
        drawerTexts?.visibleIf(boolean(R.bool.with_drawer_texts))

        val drawerTitle: TextView? = header?.findViewById(R.id.drawer_title)
        drawerTitle?.text = getAppName()

        val drawerSubtitle: TextView? = header?.findViewById(R.id.drawer_subtitle)
        drawerSubtitle?.text = currentVersionName

        navigationView?.post {
            val params = navigationView?.layoutParams as? DrawerLayout.LayoutParams
            params?.width = getOptimalDrawerWidth()
            navigationView?.layoutParams = params
        }

        initDrawerItems()
        navigationView?.setNavigationItemSelectedListener(this)

        if (isIconsPicker && currentItemId != R.id.icons) {
            selectNavigationItem(R.id.icons)
            lockDrawer()
        } else selectNavigationItem(initialItemId)
    }

    private fun initDrawerItems() {
        navigationView?.menu?.setGroupCheckable(R.id.first_group, true, true)
        navigationView?.invalidate()
    }

    override fun onBillingClientReady() {
        super.onBillingClientReady()
        navigationView?.menu?.findItem(R.id.donate)?.isVisible =
            isBillingClientReady && getDonationItemsIds().isNotEmpty()
    }

    override fun onTemplatesLoaded(templates: ArrayList<Component>) {
        super.onTemplatesLoaded(templates)
        navigationView?.menu?.findItem(R.id.templates)?.isVisible = templates.isNotEmpty()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle?.onConfigurationChanged(newConfig)
        toggle?.drawerArrowDrawable?.color =
            resolveColor(R.attr.colorOnPrimary, color(R.color.onPrimary))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle?.onOptionsItemSelected(item) == true) return true
        return super.onOptionsItemSelected(item)
    }

    override fun selectNavigationItem(itemId: Int) {
        super.selectNavigationItem(itemId)
        navigationView?.menu?.findItem(itemId)?.let {
            onNavigationItemSelected(it)
            navigationView?.setCheckedItem(it)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout?.closeDrawer(GravityCompat.START, true)
        if (isIconsPicker && item.itemId != R.id.icons) return false
        when (item.itemId) {
            R.id.home, R.id.icons, R.id.wallpapers, R.id.apply, R.id.request ->
                updateFab(item.itemId, true)
            else -> {
            }
        }
        val checked = changeFragment(item.itemId)
        if (!checked) onOptionsItemSelected(item)
        return checked
    }

    override fun onBackPressed() {
        val isDrawerOpen = drawerLayout?.isDrawerOpen(GravityCompat.START) ?: false
        if (!isIconsPicker) {
            when {
                isDrawerOpen -> drawerLayout?.closeDrawer(GravityCompat.START, true)
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun lockDrawer() {
        drawerLayout?.closeDrawer(GravityCompat.START, true)
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        toggle?.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
        toggle?.isDrawerIndicatorEnabled = false
        toggle?.syncState()
    }

    override fun getMenuRes(): Int = R.menu.drawer_toolbar_menu
    override fun getLayoutRes(): Int = R.layout.activity_drawer
    override val snackbarAnchorId: Int
        get() = R.id.fab_btn
}