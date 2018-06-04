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
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.dpToPx
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.postDelayed
import ca.allanwang.kau.utils.setMarginBottom
import ca.allanwang.kau.utils.setMarginRight
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.statusBarLight
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.visible
import com.andremion.counterfab.CounterFab
import jahirfiquitiva.libs.blueprint.R
import jahirfiquitiva.libs.blueprint.helpers.extensions.blueprintFormat
import jahirfiquitiva.libs.blueprint.helpers.extensions.defaultLauncher
import jahirfiquitiva.libs.blueprint.helpers.extensions.executeLauncherIntent
import jahirfiquitiva.libs.blueprint.helpers.extensions.showIf
import jahirfiquitiva.libs.blueprint.helpers.utils.BPKonfigs
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_APPLY_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_HOME_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_ICONS_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_REQUEST_SECTION_ID
import jahirfiquitiva.libs.blueprint.helpers.utils.DEFAULT_WALLPAPERS_SECTION_ID
import jahirfiquitiva.libs.blueprint.models.Filter
import jahirfiquitiva.libs.blueprint.models.NavigationItem
import jahirfiquitiva.libs.blueprint.quest.IconRequest
import jahirfiquitiva.libs.blueprint.quest.events.SendRequestCallback
import jahirfiquitiva.libs.blueprint.ui.fragments.ApplyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.EmptyFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.HomeFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.IconsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.RequestsFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.WallpapersFragment
import jahirfiquitiva.libs.blueprint.ui.fragments.dialogs.FiltersBottomSheet
import jahirfiquitiva.libs.frames.helpers.extensions.mdDialog
import jahirfiquitiva.libs.frames.helpers.extensions.showChanges
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_APPLIER
import jahirfiquitiva.libs.frames.helpers.utils.ICONS_PICKER
import jahirfiquitiva.libs.frames.helpers.utils.IMAGE_PICKER
import jahirfiquitiva.libs.frames.ui.activities.base.BaseFramesActivity
import jahirfiquitiva.libs.frames.ui.fragments.base.BaseFramesFragment
import jahirfiquitiva.libs.frames.ui.widgets.CustomToolbar
import jahirfiquitiva.libs.kext.extensions.accentColor
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.drawable
import jahirfiquitiva.libs.kext.extensions.enableTranslucentStatusBar
import jahirfiquitiva.libs.kext.extensions.formatCorrectly
import jahirfiquitiva.libs.kext.extensions.getActiveIconsColorFor
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.getPrimaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.getSecondaryTextColorFor
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.isColorLight
import jahirfiquitiva.libs.kext.extensions.primaryColor
import jahirfiquitiva.libs.kext.extensions.primaryDarkColor
import jahirfiquitiva.libs.kext.extensions.setItemVisibility
import jahirfiquitiva.libs.kext.extensions.stringArray
import jahirfiquitiva.libs.kext.extensions.tint
import jahirfiquitiva.libs.kext.ui.fragments.adapters.FragmentsPagerAdapter
import jahirfiquitiva.libs.kext.ui.layouts.FixedElevationAppBarLayout
import jahirfiquitiva.libs.kext.ui.widgets.CustomSearchView
import jahirfiquitiva.libs.kuper.ui.widgets.PseudoViewPager

abstract class BaseBlueprintActivity : BaseFramesActivity<BPKonfigs>() {
    
    override val configs: BPKonfigs by lazy { BPKonfigs(this) }
    override fun lightTheme(): Int = R.style.BlueprintLightTheme
    override fun darkTheme(): Int = R.style.BlueprintDarkTheme
    override fun amoledTheme(): Int = R.style.BlueprintAmoledTheme
    override fun transparentTheme(): Int = R.style.BlueprintTransparentTheme
    
    abstract fun hasBottomNavigation(): Boolean
    open fun debug(): Boolean = false
    
    private val coordinatorLayout: CoordinatorLayout? by bind(R.id.mainCoordinatorLayout)
    private val appbarLayout: FixedElevationAppBarLayout? by bind(R.id.appbar)
    
    private val iconsFilters: ArrayList<Filter> = ArrayList()
    private val activeFilters: ArrayList<Filter> = ArrayList()
    
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
        toolbar?.enableScroll(true)
        initCurrentSectionId()
        initMainComponents()
        if (isIconsPicker) {
            postDelayed(10) {
                navigateToItem(getNavigationItemWithId(currentSectionId), true, true)
            }
        }
    }
    
    private fun initCurrentSectionId(customId: Int = -1) {
        val defaultSectionId =
            if (customId != -1) customId
            else getNavigationItems().firstOrNull()?.id ?: DEFAULT_HOME_SECTION_ID
        currentSectionId = if (isIconsPicker) {
            getNavigationItems().firstOrNull { it.id == DEFAULT_ICONS_SECTION_ID }?.id
                ?: defaultSectionId
        } else defaultSectionId
    }
    
    private fun initMainComponents() {
        initFragments()
        initFAB()
        updateUI(getNavigationItemWithId(currentSectionId))
    }
    
    private fun initFragments() {
        val fragmentsAdapter = FragmentsPagerAdapter(supportFragmentManager)
        val defFragment = EmptyFragment()
        loop@ for (item in getNavigationItems()) {
            when (item.id) {
                DEFAULT_HOME_SECTION_ID ->
                    fragmentsAdapter.addFragment(if (isIconsPicker) defFragment else HomeFragment())
                DEFAULT_ICONS_SECTION_ID ->
                    fragmentsAdapter.addFragment(IconsFragment.create(pickerKey))
                DEFAULT_WALLPAPERS_SECTION_ID ->
                    fragmentsAdapter.addFragment(
                        if (isIconsPicker) defFragment else
                            WallpapersFragment.create(getLicenseChecker() != null))
                DEFAULT_APPLY_SECTION_ID ->
                    fragmentsAdapter.addFragment(
                        if (isIconsPicker) defFragment else ApplyFragment())
                DEFAULT_REQUEST_SECTION_ID ->
                    fragmentsAdapter.addFragment(
                        if (isIconsPicker) defFragment else RequestsFragment.create(debug()))
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
            when (currentSectionId) {
                DEFAULT_HOME_SECTION_ID -> executeLauncherIntent(defaultLauncher?.name.orEmpty())
                DEFAULT_REQUEST_SECTION_ID -> startRequestsProcess()
                DEFAULT_ICONS_SECTION_ID -> showFiltersBottomSheet()
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
            DEFAULT_ICONS_SECTION_ID -> drawable(R.drawable.ic_filter)
            else -> null
        }
        fab?.setImageDrawable(icon?.tint(getActiveIconsColorFor(accentColor, 0.6F)))
        fab?.showIf(
            (currentSectionId == DEFAULT_HOME_SECTION_ID && launcherName.hasContent())
                || currentSectionId == DEFAULT_REQUEST_SECTION_ID
                || (currentSectionId == DEFAULT_ICONS_SECTION_ID && iconsFilters.size > 1))
    }
    
    fun initFiltersFromCategories(categories: ArrayList<String>) {
        var index = 0
        var colorIndex = 0
        val colors = stringArray(R.array.filters_colors).orEmpty()
        
        val newFilters = ArrayList<Filter>()
        if (categories.size > 1) {
            categories.forEach {
                if (colorIndex >= colors.size) colorIndex = 0
                val name = it.formatCorrectly().blueprintFormat()
                if (!(name.contains("all", true))) {
                    newFilters.add(Filter(name, Color.parseColor(colors[colorIndex]), false))
                    index += 1
                    colorIndex += 1
                }
            }
        }
        
        this.iconsFilters.clear()
        this.iconsFilters.addAll(newFilters)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            updateToolbarMenuItems(getNavigationItemWithId(currentSectionId), it)
            
            searchItem = it.findItem(R.id.search)
            searchView = searchItem?.actionView as? CustomSearchView
            searchView?.onExpand = { toolbar?.enableScroll(false) }
            searchView?.onCollapse = {
                toolbar?.enableScroll(true)
                doSearch(closed = true)
            }
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
                R.id.refresh -> {
                    refreshWallpapers()
                    refreshRequests()
                }
                R.id.changelog -> showChanges()
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
        initCurrentSectionId(savedInstanceState?.getInt("currentSectionId", default) ?: default)
        
        dialog = mdDialog {
            content(R.string.loading)
            progress(true, 0)
            cancelable(false)
            canceledOnTouchOutside(false)
        }
        pager?.gone()
        dialog?.show()
        initFragments()
        postDelayed(25) {
            navigateToItem(getNavigationItemWithId(currentSectionId), true, true)
        }
    }
    
    private fun showFiltersBottomSheet() {
        FiltersBottomSheet.show(
            this@BaseBlueprintActivity, iconsFilters, activeFilters) {
            this.activeFilters.clear()
            this.activeFilters.addAll(it)
            this.applyIconFilters()
        }
    }
    
    internal open fun navigateToItem(
        item: NavigationItem,
        fromClick: Boolean,
        force: Boolean = false
                                    ): Boolean {
        return try {
            if (currentSectionId != item.id || force) {
                val index = getNavigationItems().indexOf(item)
                if (index < 0) return false
                postDelayed(10) {
                    pager?.setCurrentItem(index) {
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
    }
    
    private fun updateToolbarMenuItems(item: NavigationItem, menu: Menu) {
        val isInIconsSection = item.id == DEFAULT_ICONS_SECTION_ID
        menu.setItemVisibility(R.id.donate, donationsEnabled)
        menu.setItemVisibility(
            R.id.search,
            if (isInIconsSection) iconsFilters.isNotEmpty()
            else item.id != DEFAULT_HOME_SECTION_ID)
        menu.setItemVisibility(
            R.id.refresh,
            item.id == DEFAULT_WALLPAPERS_SECTION_ID || item.id == DEFAULT_REQUEST_SECTION_ID)
        menu.setItemVisibility(R.id.select_all, item.id == DEFAULT_REQUEST_SECTION_ID)
        menu.setItemVisibility(R.id.templates, hasTemplates && !isIconsPicker)
        menu.setItemVisibility(R.id.about, hasBottomNavigation())
        menu.setItemVisibility(R.id.settings, hasBottomNavigation() && !isIconsPicker)
        menu.setItemVisibility(R.id.help, hasBottomNavigation() && !isIconsPicker)
    }
    
    open fun getNavigationItems(): Array<NavigationItem> =
        arrayOf(
            NavigationItem.HOME,
            NavigationItem.ICONS,
            NavigationItem.WALLPAPERS,
            NavigationItem.APPLY,
            NavigationItem.REQUESTS)
    
    internal fun getNavigationItemWithId(id: Int): NavigationItem {
        return getNavigationItems().firstOrNull { it.id == id } ?: NavigationItem.HOME
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
    
    private fun startRequestsProcess() {
        IconRequest.get()?.let {
            if (it.selectedApps.size > 0) {
                requestStoragePermission(getString(R.string.permission_request, getAppName())) {
                    doSendRequest()
                }
            } else {
                runOnUiThread {
                    destroyDialog()
                    dialog = mdDialog {
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
        dialog = mdDialog {
            content(R.string.building_request_dialog)
            progress(true, 0)
            cancelable(false)
        }
        IconRequest.get()?.let {
            it.send(
                object : SendRequestCallback() {
                    override fun doWhenStarted() {
                        runOnUiThread { dialog?.show() }
                    }
                    
                    override fun doOnError(msg: String, uploading: Boolean) {
                        runOnUiThread {
                            destroyDialog()
                            dialog = mdDialog {
                                title(R.string.error_title)
                                content(
                                    getString(
                                        if (uploading) R.string.requests_upload_error
                                        else R.string.requests_error,
                                        msg))
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
                                dialog = mdDialog {
                                    title(R.string.request_upload_success)
                                    content(R.string.request_upload_success_content)
                                    positiveText(android.R.string.ok)
                                }
                                dialog?.show()
                            }
                        }
                    }
                })
        } ?: {
            destroyDialog()
            dialog = mdDialog {
                title(R.string.error_title)
                content(R.string.requests_error)
                positiveText(android.R.string.ok)
            }
            dialog?.show()
        }()
    }
    
    private fun applyIconFilters() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
            currentSectionPosition) as? IconsFragment)?.applyFilters(activeFilters)
    }
    
    private fun scrollToTop() {
        val activeFragment =
            (pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
        (activeFragment as? HomeFragment)?.scrollToTop()
        (activeFragment as? IconsFragment)?.scrollToTop()
        (activeFragment as? BaseFramesFragment<*, *>)?.scrollToTop()
        (activeFragment as? ApplyFragment)?.scrollToTop()
        (activeFragment as? RequestsFragment)?.scrollToTop()
    }
    
    private fun doSearch(search: String = "", closed: Boolean = false) {
        val activeFragment =
            (pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
        (activeFragment as? IconsFragment)?.doSearch(search, closed)
        (activeFragment as? BaseFramesFragment<*, *>)?.applyFilter(search, closed)
        (activeFragment as? ApplyFragment)?.applyFilter(search, closed)
        (activeFragment as? RequestsFragment)?.applyFilter(search, closed)
    }
    
    private fun refreshWallpapers() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(currentSectionPosition)
            as? BaseFramesFragment<*, *>)?.reloadData(1)
    }
    
    private fun refreshRequests() {
        ((pager?.adapter as? FragmentsPagerAdapter)?.get(
            currentSectionPosition) as? RequestsFragment)
            ?.refresh()
    }
    
    private fun toggleSelectAll() {
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