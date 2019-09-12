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
package jahirfiquitiva.libs.blueprint.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.widget.TextViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visibleIf
import com.google.android.material.navigation.NavigationView
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.getOptimalDrawerWidth
import jahirfiquitiva.libs.blueprint.helpers.extensions.setOptimalDrawerHeaderHeight
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_TEMPLATES_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_SECTION_ID
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.activeIconsColor
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.boolean
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.enableTranslucentStatusBar
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.getAppVersion
import jahirfiquitiva.libs.kext.extensions.primaryTextColor
import jahirfiquitiva.libs.kext.extensions.string

abstract class DrawerBlueprintActivity : BaseBlueprintActivity(),
                                         NavigationView.OnNavigationItemSelectedListener {
    
    private val drawerLayout: DrawerLayout? by bind(R.id.drawer_layout)
    private val navView: NavigationView? by bind(R.id.nav_view)
    private var toggle: ActionBarDrawerToggle? = null
    
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val v: View? by bind(R.id.bottom_navigation)
        v?.gone()
        
        enableTranslucentStatusBar()
        
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        toggle?.let { drawerLayout?.addDrawerListener(it) }
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        
        val header = navView?.findViewById(R.id.nav_header) ?: navView?.getHeaderView(0)
        header?.let { setOptimalDrawerHeaderHeight(it) }
        
        val headerDrawable = try {
            drawable("drawer_header")
        } catch (e: Exception) {
            null
        }
        headerDrawable?.let { header?.background = it } ?: header?.setBackgroundColor(accentColor)
        
        val drawerTitle: TextView? by header?.bind(R.id.drawer_title)
        drawerTitle?.text = getAppName()
        drawerTitle?.let { TextViewCompat.setTextAppearance(it, R.style.DrawerTextsWithShadow) }
        drawerTitle?.visibleIf(boolean(R.bool.with_drawer_texts))
        
        val drawerSubtitle: TextView? by header?.bind(R.id.drawer_subtitle)
        drawerSubtitle?.text = "v ${getAppVersion()}"
        drawerSubtitle?.let { TextViewCompat.setTextAppearance(it, R.style.DrawerTextsWithShadow) }
        drawerSubtitle?.visibleIf(boolean(R.bool.with_drawer_texts))
        
        navView?.post {
            val params = navView?.layoutParams as? DrawerLayout.LayoutParams
            params?.width = getOptimalDrawerWidth()
            navView?.layoutParams = params
        }
        
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf())
        
        val iconColors = intArrayOf(activeIconsColor, accentColor, activeIconsColor)
        val iconColorsList = ColorStateList(states, iconColors)
        
        val textColors = intArrayOf(primaryTextColor, accentColor, primaryTextColor)
        val textColorsList = ColorStateList(states, textColors)
        
        navView?.itemTextColor = textColorsList
        navView?.itemIconTintList = iconColorsList
        
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
        
        navView?.itemBackground = bgDrawable
        navView?.invalidate()
        
        initDrawerItems()
        navView?.setNavigationItemSelectedListener(this)
        navView?.menu?.findItem(getMenuIdForItemId(getNavigationItems()[0].id))?.isChecked = true
        
        if (isIconsPicker) lockDrawer()
    }
    
    private fun initDrawerItems() {
        val menu = navView?.menu
        
        getNavigationItems().forEachIndexed { index, it ->
            menu?.add(R.id.first_group, getMenuIdForItemId(it.id), index, it.title)
            menu?.findItem(getMenuIdForItemId(it.id))?.icon = drawable(it.icon)
        }
        
        if (hasTemplates) {
            menu?.add(
                R.id.first_group, getMenuIdForItemId(DEFAULT_TEMPLATES_SECTION_ID),
                getNavigationItems().size + 1, string(R.string.templates))
            menu?.findItem(getMenuIdForItemId(DEFAULT_TEMPLATES_SECTION_ID))?.icon =
                drawable(R.drawable.ic_widgets)
        }
        
        menu?.setGroupCheckable(R.id.first_group, true, true)
        
        navView?.invalidate()
    }
    
    private fun getMenuIdForItemId(id: Int): Int = when (id) {
        DEFAULT_HOME_SECTION_ID -> R.id.nav_home
        DEFAULT_ICONS_SECTION_ID -> R.id.nav_icons
        DEFAULT_WALLPAPERS_SECTION_ID -> R.id.nav_wallpapers
        DEFAULT_APPLY_SECTION_ID -> R.id.nav_apply
        DEFAULT_REQUEST_SECTION_ID -> R.id.nav_request
        DEFAULT_TEMPLATES_SECTION_ID -> R.id.nav_templates
        else -> -1
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
        drawerLayout?.closeDrawer(GravityCompat.START, true)
        
        val itemId = item.itemId
        val correctItem = itemId != R.id.nav_templates && itemId != R.id.nav_about &&
            itemId != R.id.nav_settings && itemId != R.id.nav_help
        
        navView?.menu?.findItem(itemId)?.isChecked = correctItem
        
        when (itemId) {
            R.id.nav_home -> navigateToItem(getNavigationItemWithId(DEFAULT_HOME_SECTION_ID), true)
            R.id.nav_icons ->
                navigateToItem(getNavigationItemWithId(DEFAULT_ICONS_SECTION_ID), true)
            R.id.nav_wallpapers ->
                navigateToItem(getNavigationItemWithId(DEFAULT_WALLPAPERS_SECTION_ID), true)
            R.id.nav_apply ->
                navigateToItem(getNavigationItemWithId(DEFAULT_APPLY_SECTION_ID), true)
            R.id.nav_request ->
                navigateToItem(getNavigationItemWithId(DEFAULT_REQUEST_SECTION_ID), true)
            R.id.nav_templates -> launchKuperActivity()
            R.id.nav_about -> startActivity(Intent(this, CreditsActivity::class.java))
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_help -> launchHelpActivity()
        }
        
        return correctItem
    }
    
    override fun onBackPressed() {
        val isDrawerOpen = drawerLayout?.isDrawerOpen(GravityCompat.START) ?: false
        if (!isIconsPicker) {
            when {
                isDrawerOpen -> drawerLayout?.closeDrawer(GravityCompat.START, true)
                currentSectionId != DEFAULT_HOME_SECTION_ID -> {
                    try {
                        navView?.menu?.findItem(getMenuIdForItemId(getNavigationItems()[0].id))
                            ?.isChecked = true
                        navigateToItem(getNavigationItems()[0], true)
                    } catch (e: Exception) {
                    }
                }
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
    
    override fun navigateToItem(item: NavigationItem, fromClick: Boolean, force: Boolean): Boolean {
        if (isIconsPicker) lockDrawer()
        val itemId = getMenuIdForItemId(item.id)
        val correctItem = itemId != R.id.nav_templates && itemId != R.id.nav_about &&
            itemId != R.id.nav_settings && itemId != R.id.nav_help
        navView?.menu?.findItem(itemId)?.isChecked = correctItem
        return super.navigateToItem(item, fromClick, force)
    }
    
    private fun lockDrawer() {
        drawerLayout?.closeDrawer(GravityCompat.START, true)
        drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START)
        toggle?.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
        toggle?.isDrawerIndicatorEnabled = false
        toggle?.syncState()
    }
    
    override fun hasBottomNavigation(): Boolean = false
}