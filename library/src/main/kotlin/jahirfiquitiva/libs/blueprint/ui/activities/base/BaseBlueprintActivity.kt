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
package jahirfiquitiva.libs.blueprint.ui.activities.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.goneIf
import ca.allanwang.kau.utils.hideIf
import ca.allanwang.kau.utils.isHidden
import ca.allanwang.kau.utils.setMarginBottom
import ca.allanwang.kau.utils.setMarginRight
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.showIf
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.xml.showChangelog
import com.andremion.counterfab.CounterFab
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_POSITION
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_POSITION
import jahirfiquitiva.libs.blueprint.ui.activities.BlueprintKuperActivity
import jahirfiquitiva.libs.blueprint.ui.activities.CreditsActivity
import jahirfiquitiva.libs.blueprint.ui.activities.HelpActivity
import jahirfiquitiva.libs.blueprint.ui.activities.SettingsActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.FilterCheckBoxHolder
import jahirfiquitiva.libs.blueprint.ui.fragments.ApplyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.RequestsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.WallpapersFragment
import jahirfiquitiva.libs.blueprint.ui.items.FilterDrawerItem
import jahirfiquitiva.libs.blueprint.ui.items.FilterTitleDrawerItem
import jahirfiquitiva.libs.fabsmenu.FABsMenu
import jahirfiquitiva.libs.fabsmenu.FABsMenuLayout
import jahirfiquitiva.libs.fabsmenu.TitleFAB
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.utils.PLAY_STORE_LINK_PREFIX
import jahirfiquitiva.libs.frames.ui.activities.base.BaseFramesActivity
import jahirfiquitiva.libs.frames.ui.widgets.CustomToolbar
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.activeIconsColor
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.changeOptionVisibility
import jahirfiquitiva.libs.kauextensions.extensions.enableTranslucentStatusBar
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getDrawable
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getStringArray
import jahirfiquitiva.libs.kauextensions.extensions.hideAllItems
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.openLink
import jahirfiquitiva.libs.kauextensions.extensions.overlayColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryDarkColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.rippleColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.showAllItems
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.ui.layouts.CustomCoordinatorLayout
import jahirfiquitiva.libs.kauextensions.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.kauextensions.ui.widgets.CustomSearchView
import jahirfiquitiva.libs.quest.IconRequest
import jahirfiquitiva.libs.quest.events.OnRequestProgress

abstract class BaseBlueprintActivity : BaseFramesActivity() {
    
    override fun lightTheme(): Int = R.style.BlueprintLightTheme
    override fun darkTheme(): Int = R.style.BlueprintDarkTheme
    override fun amoledTheme(): Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme(): Int = R.style.BlueprintTransparentTheme
    
    abstract fun hasBottomNavigation(): Boolean
    
    private val coordinatorLayout: CustomCoordinatorLayout? by bind(R.id.mainCoordinatorLayout)
    private val appbarLayout: FixedElevationAppBarLayout? by bind(R.id.appbar)
    
    private lateinit var filtersDrawer: Drawer
    
    internal val toolbar: CustomToolbar? by bind(R.id.toolbar)
    internal val fabsMenu: FABsMenu? by bind(R.id.fabs_menu)
    internal val fab: CounterFab? by bind(R.id.fab)
    
    internal var drawer: Drawer? = null
    private var searchView: CustomSearchView? = null
    private var activeFragment: Fragment? = null
    
    private var iconsFilters: ArrayList<String> = ArrayList()
    internal var currentItemId: Int = -1
    
    override fun fragmentsContainer(): Int = R.id.fragments_container
    
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableTranslucentStatusBar()
        statusBarLight = primaryDarkColor.isColorLight(0.6F)
        setContentView(R.layout.activity_blueprint)
        toolbar?.bindToActivity(this, false)
        initMainComponents(savedInstanceState)
    }
    
    override fun onBackPressed() {
        invalidateOptionsMenu()
        val isOpen = searchView?.isOpen ?: false
        if (isOpen) {
            doSearch()
            searchView?.onActionViewCollapsed()
        } else {
            if (!hasBottomNavigation()) {
                if (currentItemId != DEFAULT_HOME_POSITION) {
                    navigateToItem(getNavigationItemWithId(DEFAULT_HOME_POSITION))
                } else {
                    supportFinishAfterTransition()
                }
            } else {
                supportFinishAfterTransition()
            }
        }
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("toolbarTitle", toolbar?.title.toString())
        outState?.putInt("currentItemId", currentItemId)
        super.onSaveInstanceState(outState)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        toolbar?.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        supportActionBar?.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        navigateToItem(
                getNavigationItemWithId(
                        savedInstanceState?.getInt("currentItemId") ?: DEFAULT_HOME_POSITION))
    }
    
    private fun initMainComponents(savedInstance: Bundle?) {
        initFAB()
        initFABsMenu()
        initFiltersDrawer(savedInstance)
    }
    
    private fun initFAB() {
        fab?.setImageDrawable(
                "ic_send".getDrawable(this)?.tint(getActiveIconsColorFor(accentColor)))
        fab?.setMarginRight(16F.dpToPx.toInt())
        fab?.setMarginBottom((if (hasBottomNavigation()) 72F else 16F).dpToPx.toInt())
        fab?.setOnClickListener { startRequestsProcess() }
    }
    
    private fun initFABsMenu() {
        val fabsMenuOverlay: FABsMenuLayout by bind(R.id.fabs_menu_overlay)
        fabsMenuOverlay.overlayColor = overlayColor
        
        if (hasBottomNavigation()) {
            fabsMenu?.menuBottomMargin = 72F.dpToPx.toInt()
        }
        fabsMenu?.menuButtonIcon = "ic_plus".getDrawable(this)?.tint(
                getActiveIconsColorFor(accentColor))
        fabsMenu?.menuButtonRippleColor = rippleColor
        
        val rateFab: TitleFAB by bind(R.id.rate_fab)
        rateFab.setImageDrawable("ic_rate".getDrawable(this)?.tint(activeIconsColor))
        rateFab.titleTextColor = primaryTextColor
        rateFab.rippleColor = rippleColor
        rateFab.setOnClickListener { openLink(PLAY_STORE_LINK_PREFIX + packageName) }
        
        val shareFab: TitleFAB by bind(R.id.share_fab)
        shareFab.setImageDrawable("ic_share".getDrawable(this)?.tint(activeIconsColor))
        shareFab.titleTextColor = primaryTextColor
        shareFab.rippleColor = rippleColor
        shareFab.setOnClickListener {
            shareText(
                    getString(
                            R.string.share_this_app, getAppName(),
                            PLAY_STORE_LINK_PREFIX + packageName))
        }
        
        val donateFab: TitleFAB by bind(R.id.donate_fab)
        if (donationsEnabled) {
            donateFab.setImageDrawable("ic_donate".getDrawable(this)?.tint(activeIconsColor))
            donateFab.titleTextColor = primaryTextColor
            donateFab.rippleColor = rippleColor
            donateFab.setOnClickListener {
                doDonation()
            }
        } else {
            fabsMenu?.removeButton(donateFab)
        }
        
        val helpFab: TitleFAB by bind(R.id.help_fab)
        helpFab.setImageDrawable("ic_help".getDrawable(this)?.tint(activeIconsColor))
        helpFab.titleTextColor = primaryTextColor
        helpFab.rippleColor = rippleColor
        helpFab.setOnClickListener { launchHelpActivity() }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            val donationItem = it.findItem(R.id.donate)
            donationItem?.isVisible = donationsEnabled
            
            updateToolbarMenuItems(getNavigationItemWithId(currentItemId), it)
            
            val searchItem = it.findItem(R.id.search)
            searchView = searchItem.actionView as? CustomSearchView
            searchView?.onExpand = {
                it.hideAllItems()
            }
            searchView?.onCollapse = {
                it.showAllItems()
                doSearch()
            }
            searchView?.onQueryChanged = { doSearch(it) }
            searchView?.onQuerySubmit = { doSearch(it) }
            searchView?.bindToItem(searchItem)
            
            searchView?.queryHint = when (currentItemId) {
                DEFAULT_ICONS_POSITION -> getString(R.string.search_icons)
                DEFAULT_WALLPAPERS_POSITION -> getString(R.string.search_wallpapers)
                DEFAULT_APPLY_POSITION -> getString(R.string.search_launchers)
                DEFAULT_REQUEST_POSITION -> getString(R.string.search_apps)
                else -> ""
            }
            
            searchView?.tint(getPrimaryTextColorFor(primaryColor, 0.6F))
            it.tint(getActiveIconsColorFor(primaryColor, 0.6F))
        }
        toolbar?.tint(
                getPrimaryTextColorFor(primaryColor, 0.6F),
                getSecondaryTextColorFor(primaryColor, 0.6F),
                getActiveIconsColorFor(primaryColor, 0.6F))
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.filters -> filtersDrawer.openDrawer()
                R.id.refresh -> {
                    refreshWallpapers()
                    refreshRequests()
                }
                R.id.changelog -> showChangelog(R.xml.changelog, secondaryTextColor)
                R.id.select_all -> toggleSelectAll()
                R.id.help -> launchHelpActivity()
                R.id.about -> startActivity(Intent(this, CreditsActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun initFiltersDrawer(savedInstance: Bundle?) {
        val filtersDrawerBuilder = DrawerBuilder().withActivity(this)
        val hadFilters = iconsFilters.isNotEmpty()
        filtersDrawerBuilder.addDrawerItems(
                FilterTitleDrawerItem(hadFilters).withButtonListener(
                        object : FilterTitleDrawerItem.ButtonListener {
                            override fun onButtonPressed() {
                                filtersDrawer.drawerItems?.forEach {
                                    (it as? FilterDrawerItem)?.checkBoxHolder?.apply(false, false)
                                }
                                if (hadFilters) {
                                    iconsFilters.clear()
                                    onFiltersUpdated(iconsFilters)
                                }
                            }
                        }))
        var index = 0
        var colorIndex = 0
        val colors = getStringArray(R.array.filters_colors)
        val filters = getStringArray(R.array.icon_filters)
        if (filters.size > 1) {
            filters.forEach {
                if (colorIndex >= colors.size) colorIndex = 0
                val name = it.formatCorrectly().blueprintFormat()
                if (!(name.equals("all", true))) {
                    filtersDrawerBuilder.addDrawerItems(
                            FilterDrawerItem().withName(it.formatCorrectly().blueprintFormat())
                                    .withColor(Color.parseColor(colors[colorIndex]))
                                    .withListener(
                                            object : FilterCheckBoxHolder.StateChangeListener {
                                                override fun onStateChanged(
                                                        checked: Boolean, title: String,
                                                        fireFiltersListener: Boolean
                                                                           ) {
                                                    if (iconsFilters.contains(title)) {
                                                        if (!checked) {
                                                            iconsFilters.remove(title)
                                                            if (fireFiltersListener)
                                                                onFiltersUpdated(iconsFilters)
                                                        }
                                                    } else {
                                                        if (checked) {
                                                            iconsFilters.add(title)
                                                            if (fireFiltersListener)
                                                                onFiltersUpdated(iconsFilters)
                                                        }
                                                    }
                                                }
                                            })
                                    .withDivider(index < (filters.size - 1)))
                    index += 1
                    colorIndex += 1
                }
            }
        }
        filtersDrawerBuilder.withDrawerGravity(Gravity.END)
        if (savedInstance != null) filtersDrawerBuilder.withSavedInstance(savedInstance)
        filtersDrawer = filtersDrawerBuilder.build()
    }
    
    internal fun navigateToItem(item: NavigationItem): Boolean {
        val id = item.id
        if (currentItemId == id) return false
        try {
            currentItemId = id
            return internalNavigateToItem(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    
    internal open fun internalNavigateToItem(item: NavigationItem): Boolean {
        try {
            val isOpen = searchView?.isOpen ?: false
            if (isOpen) {
                doSearch()
                searchView?.onActionViewCollapsed()
            }
            
            invalidateOptionsMenu()
            
            fabsMenu?.collapse()
            if (fabsMenu?.menuButton?.isShown == true)
                fabsMenu?.menuButton?.hideIf(item.id != DEFAULT_HOME_POSITION)
            fabsMenu?.goneIf(item.id != DEFAULT_HOME_POSITION)
            if (fabsMenu?.menuButton?.isHidden == true)
                fabsMenu?.menuButton?.showIf(item.id == DEFAULT_HOME_POSITION)
            fab?.showIf(item.id == DEFAULT_REQUEST_POSITION)
            
            toolbar?.title = getString(
                    if (item.id == DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            supportActionBar?.title = getString(
                    if (item.id == DEFAULT_HOME_POSITION) R.string.app_name else item.title)
            
            val rightItem = getNavigationItemWithId(item.id)
            changeFragment(getFragmentForNavigationItem(item.id), rightItem.tag)
            lockFiltersDrawer(
                    item.id != DEFAULT_ICONS_POSITION ||
                            getStringArray(R.array.icon_filters).size <= 1)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    
    private fun updateToolbarMenuItems(item: NavigationItem, menu: Menu) {
        menu.changeOptionVisibility(R.id.search, item.id != DEFAULT_HOME_POSITION)
        menu.changeOptionVisibility(
                R.id.filters,
                item.id == DEFAULT_ICONS_POSITION &&
                        getStringArray(R.array.icon_filters).size > 1)
        menu.changeOptionVisibility(
                R.id.refresh,
                item.id == DEFAULT_WALLPAPERS_POSITION || item.id == DEFAULT_REQUEST_POSITION)
        menu.changeOptionVisibility(
                R.id.select_all,
                item.id == DEFAULT_REQUEST_POSITION)
        menu.tint(getActiveIconsColorFor(primaryColor))
    }
    
    private fun lockFiltersDrawer(lock: Boolean) {
        val drawerLayout = filtersDrawer.drawerLayout
        drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
    }
    
    open fun getFragmentForNavigationItem(id: Int): Fragment {
        val frag: Fragment = when (id) {
            DEFAULT_HOME_POSITION -> HomeFragment()
            DEFAULT_ICONS_POSITION -> IconsFragment()
            DEFAULT_WALLPAPERS_POSITION -> WallpapersFragment()
            DEFAULT_APPLY_POSITION -> ApplyFragment()
            DEFAULT_REQUEST_POSITION -> RequestsFragment()
            else -> EmptyFragment()
        }
        activeFragment = frag
        return frag
    }
    
    open fun getNavigationItems(): Array<NavigationItem> =
            arrayOf(
                    NavigationItem.HOME,
                    NavigationItem.ICONS,
                    NavigationItem.WALLPAPERS,
                    NavigationItem.APPLY,
                    NavigationItem.REQUESTS)
    
    internal fun getNavigationItemWithId(id: Int): NavigationItem {
        getNavigationItems().forEach { if (it.id == id) return it }
        return NavigationItem.HOME
    }
    
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray
                                           ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 41) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doSendRequest()
            } else {
                snackbar(R.string.permission_denied)
            }
        }
    }
    
    fun startRequestsProcess() {
        IconRequest.get()?.let {
            if (it.selectedApps.size > 0) {
                requestStoragePermission(getString(R.string.permission_request, getAppName())) {
                    doSendRequest()
                }
            } else {
                runOnUiThread {
                    destroyDialog()
                    dialog = buildMaterialDialog {
                        title(R.string.no_selected_apps_title)
                        content(R.string.no_selected_apps_content)
                        positiveText(android.R.string.ok)
                    }
                    dialog?.show()
                }
            }
        }
    }
    
    private fun doSendRequest() {
        destroyDialog()
        dialog = buildMaterialDialog {
            content(R.string.building_request_dialog)
            progress(true, 0)
            cancelable(false)
        }
        val ir = IconRequest.get()
        if (ir != null) {
            ir.send(
                    object : OnRequestProgress() {
                        override fun doWhenStarted() {
                            runOnUiThread { dialog?.show() }
                        }
                        
                        override fun doOnError() {
                            runOnUiThread {
                                destroyDialog()
                                dialog = buildMaterialDialog {
                                    title(R.string.error_title)
                                    content(R.string.requests_error)
                                    positiveText(android.R.string.ok)
                                }
                                dialog?.show()
                            }
                        }
                        
                        override fun doWhenReady() {
                            runOnUiThread {
                                destroyDialog()
                                unselectAll()
                            }
                        }
                    })
        } else {
            destroyDialog()
            dialog = buildMaterialDialog {
                title(R.string.error_title)
                content(R.string.requests_error)
                positiveText(android.R.string.ok)
            }
            dialog?.show()
        }
    }
    
    internal fun onFiltersUpdated(filters: ArrayList<String>) {
        if (activeFragment is IconsFragment) {
            (activeFragment as IconsFragment).applyFilters(filters)
        }
    }
    
    internal fun doSearch(search: String = "") {
        when (activeFragment) {
            is IconsFragment -> (activeFragment as IconsFragment).doSearch(search)
            is WallpapersFragment -> (activeFragment as WallpapersFragment).applyFilter(search)
            is ApplyFragment -> (activeFragment as ApplyFragment).applyFilter(search)
            is RequestsFragment -> (activeFragment as RequestsFragment).applyFilter(search)
        }
    }
    
    internal fun refreshWallpapers() {
        (activeFragment as? WallpapersFragment)?.reloadData(1)
    }
    
    internal fun refreshRequests() {
        (activeFragment as? RequestsFragment)?.refresh()
    }
    
    internal fun toggleSelectAll() {
        (activeFragment as? RequestsFragment)?.toggleSelectAll()
    }
    
    internal fun unselectAll() {
        (activeFragment as? RequestsFragment)?.unselectAll()
    }
    
    internal fun launchHelpActivity() {
        startActivity(Intent(this, HelpActivity::class.java))
    }
    
    internal fun launchKuperActivity() {
        startActivity(Intent(this, BlueprintKuperActivity::class.java))
    }
}