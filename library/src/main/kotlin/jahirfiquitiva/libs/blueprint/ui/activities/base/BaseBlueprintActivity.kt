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
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setMarginBottom
import ca.allanwang.kau.utils.setMarginRight
import ca.allanwang.kau.utils.showIf
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import ca.allanwang.kau.xml.showChangelog
import com.andremion.counterfab.CounterFab
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.data.models.NavigationItem
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.helpers.extensions.defaultLauncher
import jahirfiquitiva.libs.blueprint.helpers.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_SECTION_ID
import jahirfiquitiva.libs.blueprint.providers.viewmodels.RequestsViewModel
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.quest.events.OnRequestProgress
import jahirfiquitiva.libs.blueprint.ui.activities.BlueprintKuperActivity
import jahirfiquitiva.libs.blueprint.ui.activities.CreditsActivity
import jahirfiquitiva.libs.blueprint.ui.activities.HelpActivity
import jahirfiquitiva.libs.blueprint.ui.activities.SettingsActivity
import jahirfiquitiva.libs.blueprint.ui.adapters.viewholders.FilterCheckBoxHolder
import jahirfiquitiva.libs.blueprint.ui.fragments.ApplyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.RequestsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.WallpapersFragment
import jahirfiquitiva.libs.blueprint.ui.items.FilterDrawerItem
import jahirfiquitiva.libs.blueprint.ui.items.FilterTitleDrawerItem
import jahirfiquitiva.libs.frames.helpers.extensions.buildMaterialDialog
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_APPLIER
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_PICKER
import jahirfiquitiva.libs.frames.helpers.utils.IMAGE_PICKER
import jahirfiquitiva.libs.frames.ui.activities.base.BaseFramesActivity
import jahirfiquitiva.libs.frames.ui.fragments.base.BaseFramesFragment
import jahirfiquitiva.libs.frames.ui.widgets.CustomToolbar
import jahirfiquitiva.libs.kauextensions.extensions.accentColor
import jahirfiquitiva.libs.kauextensions.extensions.bind
import jahirfiquitiva.libs.kauextensions.extensions.enableTranslucentStatusBar
import jahirfiquitiva.libs.kauextensions.extensions.formatCorrectly
import jahirfiquitiva.libs.kauextensions.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getAppName
import jahirfiquitiva.libs.kauextensions.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kauextensions.extensions.hasContent
import jahirfiquitiva.libs.kauextensions.extensions.isColorLight
import jahirfiquitiva.libs.kauextensions.extensions.primaryColor
import jahirfiquitiva.libs.kauextensions.extensions.primaryDarkColor
import jahirfiquitiva.libs.kauextensions.extensions.secondaryTextColor
import jahirfiquitiva.libs.kauextensions.extensions.setItemVisibility
import jahirfiquitiva.libs.kauextensions.extensions.stringArray
import jahirfiquitiva.libs.kauextensions.extensions.tint
import jahirfiquitiva.libs.kauextensions.ui.fragments.adapters.FragmentsPagerAdapter
import jahirfiquitiva.libs.kauextensions.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.kauextensions.ui.widgets.CustomSearchView
import jahirfiquitiva.libs.kuper.ui.widgets.PseudoViewPager

abstract class BaseBlueprintActivity : BaseFramesActivity<BPKonfigs>(),
                                       FilterTitleDrawerItem.ButtonListener {
    
    override val configs: BPKonfigs by lazy { BPKonfigs(this) }
    override fun lightTheme(): Int = R.style.BlueprintLightTheme
    override fun darkTheme(): Int = R.style.BlueprintDarkTheme
    override fun amoledTheme(): Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme(): Int = R.style.BlueprintTransparentTheme
    
    abstract fun hasBottomNavigation(): Boolean
    
    private val coordinatorLayout: CoordinatorLayout? by bind(R.id.mainCoordinatorLayout)
    private val appbarLayout: FixedElevationAppBarLayout? by bind(R.id.appbar)
    
    private var filtersDrawer: Drawer? = null
    private val iconsFilters: ArrayList<String> = ArrayList()
    private val activeFilters: ArrayList<String> = ArrayList()
    
    internal val toolbar: CustomToolbar? by bind(R.id.toolbar)
    private val fab: CounterFab? by bind(R.id.fab)
    
    private var searchItem: MenuItem? = null
    private var searchView: CustomSearchView? = null
    
    private val pager: PseudoViewPager? by bind(R.id.pager)
    
    internal val isIconsPicker: Boolean
        get() = (pickerKey == ICONS_PICKER || pickerKey == IMAGE_PICKER || pickerKey == ICONS_APPLIER)
    
    internal var currentSectionId: Int =
            if (isIconsPicker) DEFAULT_ICONS_SECTION_ID else DEFAULT_HOME_SECTION_ID
        private set
    
    private val currentSectionPosition: Int
        get() = getNavigationItems().indexOfFirst { it.id == currentSectionId }
    
    internal val hasTemplates: Boolean
        get() {
            var templatesCount = resources?.assets?.list("komponents").orEmpty().size
            templatesCount += resources?.assets?.list("lockscreens").orEmpty().size
            templatesCount += resources?.assets?.list("wallpapers").orEmpty().size
            templatesCount += resources?.assets?.list("widgets").orEmpty().size
            templatesCount += resources?.assets?.list("templates").orEmpty().size
            return templatesCount > 0
        }
    
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableTranslucentStatusBar()
        statusBarLight = primaryDarkColor.isColorLight(0.6F)
        setContentView(R.layout.activity_blueprint)
        toolbar?.bindToActivity(this, false)
        currentSectionId = getNavigationItems().firstOrNull()?.id ?:
                if (isIconsPicker) DEFAULT_ICONS_SECTION_ID else DEFAULT_HOME_SECTION_ID
        initMainComponents(savedInstanceState)
    }
    
    private fun initMainComponents(savedInstance: Bundle?) {
        initFragments()
        initFAB()
        initFiltersDrawer(iconsFilters, savedInstance)
        updateUI(getNavigationItemWithId(currentSectionId))
        RequestsViewModel.initAndLoadRequestApps(
                this, string(R.string.arctic_backend_host), string(R.string.arctic_backend_api_key))
    }
    
    private fun initFragments() {
        val fragmentsAdapter = FragmentsPagerAdapter(supportFragmentManager)
        loop@ for (item in getNavigationItems()) {
            when (item.id) {
                DEFAULT_HOME_SECTION_ID -> fragmentsAdapter.addFragment(HomeFragment())
                DEFAULT_ICONS_SECTION_ID ->
                    fragmentsAdapter.addFragment(IconsFragment.create(pickerKey))
                DEFAULT_WALLPAPERS_SECTION_ID ->
                    fragmentsAdapter.addFragment(
                            WallpapersFragment.create(getLicenseChecker() != null))
                DEFAULT_APPLY_SECTION_ID -> fragmentsAdapter.addFragment(ApplyFragment())
                DEFAULT_REQUEST_SECTION_ID -> fragmentsAdapter.addFragment(RequestsFragment())
                else -> continue@loop
            }
        }
        pager?.offscreenPageLimit = fragmentsAdapter.count
        pager?.adapter = fragmentsAdapter
    }
    
    private fun initFAB() {
        fab?.setMarginRight(16F.dpToPx.toInt())
        fab?.setMarginBottom((if (hasBottomNavigation()) 72F else 16F).dpToPx.toInt())
        fab?.setOnClickListener {
            if (currentSectionId == DEFAULT_HOME_SECTION_ID) {
                executeLauncherIntent(defaultLauncher?.name.orEmpty())
            } else if (currentSectionId == DEFAULT_REQUEST_SECTION_ID) {
                startRequestsProcess()
            }
        }
        updateFAB()
    }
    
    private fun updateFAB() {
        fab?.hide()
        val launcherName = defaultLauncher?.name.orEmpty()
        if (currentSectionId == DEFAULT_HOME_SECTION_ID) fab?.count = 0
        val icon: Drawable? = when (currentSectionId) {
            DEFAULT_HOME_SECTION_ID -> drawable(R.drawable.ic_apply)
            DEFAULT_REQUEST_SECTION_ID -> drawable(R.drawable.ic_send)
            else -> null
        }
        fab?.setImageDrawable(icon?.tint(getActiveIconsColorFor(accentColor, 0.6F)))
        fab?.showIf(
                (currentSectionId == DEFAULT_HOME_SECTION_ID && launcherName.hasContent())
                        || currentSectionId == DEFAULT_REQUEST_SECTION_ID)
    }
    
    fun initFiltersDrawer(filters: ArrayList<String>, savedInstance: Bundle? = null) {
        val filtersDrawerBuilder = DrawerBuilder().withActivity(this).withDrawerGravity(Gravity.END)
        
        if (savedInstance != null) filtersDrawerBuilder.withSavedInstance(savedInstance)
        
        filtersDrawer?.removeAllItems()
        
        if (filters.isNotEmpty()) {
            filtersDrawerBuilder.addDrawerItems(FilterTitleDrawerItem(this))
            setupFiltersDrawerItems(filtersDrawerBuilder, filters)
        }
        
        filtersDrawer = filtersDrawerBuilder.build()
        
        this.iconsFilters.clear()
        this.iconsFilters.addAll(filters)
        
        val item = getNavigationItemWithId(currentSectionId)
        lockFiltersDrawer(item.id != DEFAULT_ICONS_SECTION_ID || filters.size <= 1)
        if (currentSectionId == DEFAULT_ICONS_SECTION_ID) invalidateOptionsMenu()
    }
    
    private fun setupFiltersDrawerItems(builder: DrawerBuilder, filters: ArrayList<String>) {
        var index = 0
        var colorIndex = 0
        val colors = stringArray(R.array.filters_colors)
        val listener = object : FilterCheckBoxHolder.StateChangeListener {
            override fun onStateChanged(
                    checked: Boolean, title: String,
                    fireFiltersListener: Boolean
                                       ) {
                if (activeFilters.contains(title) && !checked) {
                    activeFilters.remove(title)
                    if (fireFiltersListener) applyIconFilters()
                } else if (checked) {
                    activeFilters.add(title)
                    if (fireFiltersListener) applyIconFilters()
                }
            }
        }
        
        if (filters.size > 1) {
            filters.forEach {
                if (colorIndex >= colors.size) colorIndex = 0
                val name = it.formatCorrectly().blueprintFormat()
                if (!(name.contains("all", true))) {
                    builder.addDrawerItems(
                            FilterDrawerItem().withName(it.formatCorrectly().blueprintFormat())
                                    .withColor(Color.parseColor(colors[colorIndex]))
                                    .withListener(listener)
                                    .withDivider(index < (filters.size - 1)))
                    index += 1
                    colorIndex += 1
                }
            }
        }
    }
    
    override fun onButtonPressed() {
        filtersDrawer?.drawerItems?.forEach {
            (it as? FilterDrawerItem)?.checkBoxHolder?.apply(false, false)
        }
        activeFilters.clear()
        applyIconFilters()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            updateToolbarMenuItems(getNavigationItemWithId(currentSectionId), it)
            
            searchItem = it.findItem(R.id.search)
            searchView = searchItem?.actionView as? CustomSearchView
            searchView?.onCollapse = { doSearch() }
            searchView?.onQueryChanged = { doSearch(it) }
            searchView?.onQuerySubmit = { doSearch(it) }
            searchView?.bindToItem(searchItem)
            
            searchView?.queryHint = when (currentSectionId) {
                DEFAULT_ICONS_SECTION_ID -> getString(R.string.search_icons)
                DEFAULT_WALLPAPERS_SECTION_ID -> getString(R.string.search_wallpapers)
                DEFAULT_APPLY_SECTION_ID -> getString(R.string.search_launchers)
                DEFAULT_REQUEST_SECTION_ID -> getString(R.string.search_apps)
                else -> getString(R.string.search)
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
                R.id.filters -> if (iconsFilters.isNotEmpty()) filtersDrawer?.openDrawer()
                R.id.refresh -> {
                    refreshWallpapers()
                    refreshRequests()
                }
                R.id.changelog -> showChangelog(R.xml.changelog, secondaryTextColor)
                R.id.select_all -> toggleSelectAll()
                R.id.templates -> launchKuperActivity()
                R.id.help -> launchHelpActivity()
                R.id.about -> startActivity(Intent(this, CreditsActivity::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.donate -> doDonation()
                else -> {
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        val isOpen = searchView?.isOpen ?: false
        if (isOpen) {
            searchItem?.collapseActionView()
        } else {
            if (!isIconsPicker && !hasBottomNavigation() && currentSectionId != DEFAULT_HOME_SECTION_ID) {
                navigateToItem(getNavigationItemWithId(DEFAULT_HOME_SECTION_ID), false)
            } else {
                supportFinishAfterTransition()
            }
        }
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition) as? HomeFragment)
                ?.scrollToTop()
    }
    
    override fun onPause() {
        super.onPause()
        if (searchView?.isOpen == true) searchItem?.collapseActionView()
    }
    
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("toolbarTitle", toolbar?.title?.toString() ?: getAppName())
        outState?.putInt("currentSectionId", currentSectionId)
        super.onSaveInstanceState(outState)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        toolbar?.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        supportActionBar?.title = savedInstanceState?.getString("toolbarTitle", getAppName())
        val default = if (isIconsPicker) DEFAULT_ICONS_SECTION_ID else DEFAULT_HOME_SECTION_ID
        currentSectionId = savedInstanceState?.getInt("currentSectionId", default) ?: default
        dialog = buildMaterialDialog {
            content(R.string.loading)
            progress(true, 0)
            cancelable(false)
            canceledOnTouchOutside(false)
        }
        pager?.gone()
        dialog?.show()
        initFragments()
        navigateToItem(getNavigationItemWithId(currentSectionId), true, true)
    }
    
    internal open fun navigateToItem(
            item: NavigationItem,
            fromClick: Boolean,
            force: Boolean = false
                                    ): Boolean {
        return try {
            if (currentSectionId != item.id || force) {
                postDelayed(10) {
                    pager?.setCurrentItem(getIndexOfItem(item)) {
                        updateUI(item)
                        pager?.visible()
                        destroyDialog()
                    }
                    currentSectionId = item.id
                    invalidateOptionsMenu()
                    updateUI(item)
                }
                true
            } else {
                if (hasBottomNavigation()) scrollToTop()
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private fun updateUI(item: NavigationItem) {
        updateFAB()
        
        toolbar?.title = getString(
                if (item.id == DEFAULT_HOME_SECTION_ID) R.string.app_name else item.title)
        supportActionBar?.title = getString(
                if (item.id == DEFAULT_HOME_SECTION_ID) R.string.app_name else item.title)
        
        lockFiltersDrawer(item.id != DEFAULT_ICONS_SECTION_ID || iconsFilters.size <= 1)
    }
    
    private fun updateToolbarMenuItems(item: NavigationItem, menu: Menu) {
        val isInIconsSection = item.id == DEFAULT_ICONS_SECTION_ID
        menu.setItemVisibility(R.id.donate, donationsEnabled)
        menu.setItemVisibility(
                R.id.search,
                if (isInIconsSection) iconsFilters.isNotEmpty() else item.id != DEFAULT_HOME_SECTION_ID)
        menu.setItemVisibility(R.id.filters, isInIconsSection && iconsFilters.size > 1)
        menu.setItemVisibility(
                R.id.refresh,
                item.id == DEFAULT_WALLPAPERS_SECTION_ID || item.id == DEFAULT_REQUEST_SECTION_ID)
        menu.setItemVisibility(R.id.select_all, item.id == DEFAULT_REQUEST_SECTION_ID)
        menu.setItemVisibility(R.id.templates, hasTemplates)
        menu.setItemVisibility(R.id.about, hasBottomNavigation())
        menu.setItemVisibility(R.id.settings, hasBottomNavigation())
        menu.setItemVisibility(R.id.help, hasBottomNavigation())
    }
    
    fun lockFiltersDrawer(lock: Boolean) {
        filtersDrawer?.drawerLayout?.setDrawerLockMode(
                if (lock) DrawerLayout.LOCK_MODE_LOCKED_CLOSED else DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.END)
    }
    
    open fun getNavigationItems(): Array<NavigationItem> =
            arrayOf(
                    NavigationItem.HOME,
                    NavigationItem.ICONS,
                    NavigationItem.WALLPAPERS,
                    NavigationItem.APPLY,
                    NavigationItem.REQUESTS)
    
    private fun getIndexOfItem(item: NavigationItem): Int {
        getNavigationItems().forEachIndexed { i, it ->
            if (it.id == item.id) return i
        }
        return -1
    }
    
    internal fun getNavigationItemWithId(id: Int): NavigationItem {
        getNavigationItems().forEach { if (it.id == id) return it }
        return NavigationItem.HOME
    }
    
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
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
                        
                        override fun doWhenReady(forArctic: Boolean) {
                            runOnUiThread {
                                destroyDialog()
                                unselectAll()
                                if (forArctic) {
                                    dialog = buildMaterialDialog {
                                        title(R.string.request_upload_success)
                                        content(R.string.request_upload_success_content)
                                        positiveText(android.R.string.ok)
                                    }
                                    dialog?.show()
                                }
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
    
    internal fun applyIconFilters() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
                currentSectionPosition) as? IconsFragment)
                ?.applyFilters(activeFilters)
    }
    
    internal fun scrollToTop() {
        val activeFragment =
                (pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
        (activeFragment as? HomeFragment)?.scrollToTop()
        (activeFragment as? IconsFragment)?.scrollToTop()
        (activeFragment as? BaseFramesFragment<*, *>)?.scrollToTop()
        (activeFragment as? ApplyFragment)?.scrollToTop()
        (activeFragment as? RequestsFragment)?.scrollToTop()
    }
    
    internal fun doSearch(search: String = "") {
        val activeFragment =
                (pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
        (activeFragment as? IconsFragment)?.doSearch(search)
        (activeFragment as? BaseFramesFragment<*, *>)?.applyFilter(search)
        (activeFragment as? ApplyFragment)?.applyFilter(search)
        (activeFragment as? RequestsFragment)?.applyFilter(search)
    }
    
    internal fun refreshWallpapers() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
                as? BaseFramesFragment<*, *>)?.reloadData(1)
    }
    
    internal fun refreshRequests() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
                currentSectionPosition) as? RequestsFragment)
                ?.refresh()
    }
    
    internal fun toggleSelectAll() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
                currentSectionPosition) as? RequestsFragment)
                ?.toggleSelectAll()
    }
    
    internal fun unselectAll() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
                currentSectionPosition) as? RequestsFragment)
                ?.unselectAll()
    }
    
    internal fun launchHelpActivity() {
        startActivity(Intent(this, HelpActivity::class.java))
    }
    
    internal fun launchKuperActivity() {
        startActivity(Intent(this, BlueprintKuperActivity::class.java))
    }
    
    internal fun postToFab(post: (CounterFab) -> Unit) {
        ((pager?.adapter as? FragmentsPagerAdapter)
                ?.get(currentSectionId) as? RequestsFragment)?.let { fab?.let { post(it) } }
    }
    
    internal fun requestWallpaperPermission(explanation: String, onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            requestStoragePermission(explanation, onGranted)
        } else {
            onGranted()
        }
    }
}