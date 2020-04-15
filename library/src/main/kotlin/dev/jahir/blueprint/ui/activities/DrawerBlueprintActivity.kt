package dev.jahir.blueprint.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import dev.jahir.blueprint.R
import dev.jahir.blueprint.extensions.getOptimalDrawerWidth
import dev.jahir.blueprint.extensions.setOptimalDrawerHeaderHeight
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.color
import dev.jahir.frames.extensions.context.currentVersionName
import dev.jahir.frames.extensions.context.drawable
import dev.jahir.frames.extensions.context.findView
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.resolveColor
import dev.jahir.frames.extensions.views.visibleIf
import dev.jahir.kuper.data.models.Component

abstract class DrawerBlueprintActivity : BlueprintActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private val drawerLayout: DrawerLayout? by findView(R.id.drawer_layout)
    private val navigationView: NavigationView? by findView(R.id.navigation_view)
    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle?.let { drawerLayout?.addDrawerListener(it) }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val header =
            navigationView?.findViewById(R.id.navigation_header) ?: navigationView?.getHeaderView(0)
        header?.let { setOptimalDrawerHeaderHeight(it) }

        val headerDrawable = drawable(R.drawable.drawer_header)
        headerDrawable?.let { header?.background = it }
            ?: header?.setBackgroundColor(resolveColor(R.attr.colorAccent, color(R.color.accent)))

        val drawerTitle: TextView? = header?.findViewById(R.id.drawer_title)
        drawerTitle?.text = getAppName()
        drawerTitle?.visibleIf(boolean(R.bool.with_drawer_texts))

        val drawerSubtitle: TextView? = header?.findViewById(R.id.drawer_subtitle)
        drawerSubtitle?.text = currentVersionName
        drawerSubtitle?.visibleIf(boolean(R.bool.with_drawer_texts))

        navigationView?.post {
            val params = navigationView?.layoutParams as? DrawerLayout.LayoutParams
            params?.width = getOptimalDrawerWidth()
            navigationView?.layoutParams = params
        }

        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf()
        )

        /*
        val iconColors = intArrayOf(activeIconsColor, accentColor, activeIconsColor)
        val iconColorsList = ColorStateList(states, iconColors)

        val textColors = intArrayOf(primaryTextColor, accentColor, primaryTextColor)
        val textColorsList = ColorStateList(states, textColors)

        navigationView?.itemTextColor = textColorsList
        navigationView?.itemIconTintList = iconColorsList

        val selectedItemBgColor = Color.parseColor(if (usesDarkTheme()) "#202020" else "#e8e8e8")
        val transparent = Color.parseColor("#00000000")

        val selectedBgDrawable = ColorDrawable(selectedItemBgColor)
        val normalBgDrawable = ColorDrawable(transparent)

        val bgDrawable = StateListDrawable()
        bgDrawable.addState(intArrayOf(android.R.attr.state_pressed), selectedBgDrawable)
        bgDrawable.addState(intArrayOf(android.R.attr.state_checked), selectedBgDrawable)
        bgDrawable.addState(intArrayOf(android.R.attr.state_focused), selectedBgDrawable)
        bgDrawable.addState(intArrayOf(android.R.attr.state_activated), selectedBgDrawable)
        bgDrawable.addState(intArrayOf(), normalBgDrawable)

        navigationView?.itemBackground = bgDrawable
        navigationView?.invalidate()
         */

        initDrawerItems()
        navigationView?.setNavigationItemSelectedListener(this)
        navigationView?.menu?.findItem(initialItemId)?.isChecked = true
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle?.onOptionsItemSelected(item) == true) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val checked = changeFragment(item.itemId)
        if (!checked) onOptionsItemSelected(item)
        else updateFab(item.itemId)
        drawerLayout?.closeDrawer(GravityCompat.START, true)
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

    override fun getLayoutRes(): Int = R.layout.activity_drawer
}